package org.elasticsearch.mapping;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.mapping.model.Address;
import org.elasticsearch.mapping.model.Person;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Ignore;

@Ignore
public class ElasticSearchInsertMappingTest {
    private Client esClient;
    private MappingBuilder mappingBuilder;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Before
    public void connect() {
        Settings settings = Settings.builder()
                .put("client.transport.sniff", true)
                .put("cluster.name", "elasticsearch").build();

        try {
            esClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        mappingBuilder = new MappingBuilder();
    }

    private QueryHelper.IQueryBuilderHelper queryBuilder() {
        return QueryHelper.buildQuery(mappingBuilder, esClient);
    }

    @Test
    public void testMappingAndInsert() throws Exception {

        String indexName = Person.class.getSimpleName().toLowerCase();
        mappingBuilder.initialize("org.elasticsearch.mapping.model");
        initIndexes(indexName, new Class[] { Person.class });



        Person person = new Person();
        person.setId("personId");
        person.setFirstname("firstname");
        person.setLastname("lastname");
        Address address = new Address();
        address.setCity("Fontainebleau");
        person.setAddress(address);
        save(indexName, person);

        person.setId("AnotherPersonId");
        address.setCity("Paris");
        save(indexName, person);

        esClient.admin().indices().prepareRefresh(indexName).execute().actionGet();

        Assert.assertNotNull(readById(indexName, "personId"));

        SearchResponse response = search(indexName, "first");
        Assert.assertEquals(0, response.getHits().getTotalHits());
        Assert.assertEquals(0, response.getHits().getHits().length);

        String[] searchIndexes = new String[] { indexName };
        Class<?>[] requestedTypes = new Class[] { Person.class };

        Map<String, String[]> filters = new HashMap<>();

        response = queryBuilder().types(requestedTypes).filters(filters).prepareSearch(searchIndexes).execute(0, 100);
        Assert.assertEquals(2, response.getHits().getTotalHits());
        filters.put("address.city", new String[] { "Paris" });
        response = queryBuilder().types(requestedTypes).filters(filters).prepareSearch(searchIndexes).execute(0, 100);
        Assert.assertEquals(1, response.getHits().getTotalHits());
        filters.put("address.city", new String[] { "Issy" });
        response = queryBuilder().types(requestedTypes).filters(filters).prepareSearch(searchIndexes).execute(0, 100);
        Assert.assertEquals(0, response.getHits().getTotalHits());
        filters.put("address.city", new String[] { "Fontainebleau" });
        response = queryBuilder().types(requestedTypes).filters(filters).prepareSearch(searchIndexes).execute(0, 100);
        Assert.assertEquals(1, response.getHits().getTotalHits());
    }

    public void initIndexes(String indexName, Class<?>[] classes) throws Exception {
        // check if existing before
        final ActionFuture<IndicesExistsResponse> indexExistFuture = esClient.admin().indices().exists(new IndicesExistsRequest(indexName));
        IndicesExistsResponse response;
        response = indexExistFuture.get();

        if (!response.isExists()) {
            // create the index and add the mapping
            CreateIndexRequestBuilder createIndexRequestBuilder = esClient.admin().indices().prepareCreate(indexName);

            for (Class<?> clazz : classes) {
                System.out.println(mappingBuilder.getMapping(clazz));
                createIndexRequestBuilder.addMapping(clazz.getSimpleName().toLowerCase(), mappingBuilder.getMapping(clazz));
            }
            final CreateIndexResponse createResponse = createIndexRequestBuilder.execute().actionGet();
            if (!createResponse.isAcknowledged()) {
                throw new Exception("Failed to create index <" + indexName + ">");
            }
        }
    }

    public void save(String indexName, Person data) throws JsonGenerationException, JsonMappingException, IOException {
        String json = jsonMapper.writeValueAsString(data);
        esClient.prepareIndex(indexName, indexName).setSource(json, XContentType.JSON).setId(data.getId()).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).execute().actionGet();
    }

    public Person readById(String indexName, String id) throws JsonParseException, JsonMappingException, IOException {
        GetResponse response = esClient.prepareGet(indexName, indexName, id).execute().actionGet();

        if (response == null || !response.isExists()) {
            return null;
        }

        return jsonMapper.readValue(response.getSourceAsString(), Person.class);
    }

    public SearchResponse search(String indexName, String searchText) {
        SearchRequestBuilder searchRequestBuilder = esClient.prepareSearch(indexName);
        searchRequestBuilder.setTypes(Person.class.getSimpleName());

        QueryBuilder queryBuilder;
        queryBuilder = QueryBuilders.matchPhrasePrefixQuery("_all", searchText).maxExpansions(10);

        searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH).setQuery(queryBuilder).setSize(10).setFrom(0);

        searchRequestBuilder.addSort(SortBuilders.scoreSort());

        return searchRequestBuilder.execute().actionGet();
    }
}