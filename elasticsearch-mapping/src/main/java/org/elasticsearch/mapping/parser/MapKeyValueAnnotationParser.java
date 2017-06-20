package org.elasticsearch.mapping.parser;

import org.elasticsearch.annotation.MapKeyValue;
import org.elasticsearch.annotation.StringField;
import org.elasticsearch.mapping.*;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by lucboutier on 15/06/2016.
 */
public class MapKeyValueAnnotationParser implements IPropertyAnnotationParser<MapKeyValue> {
    private static final Logger LOGGER = Logger.getLogger(MapKeyValueAnnotationParser.class.toString());

    private final FieldsMappingBuilder fieldsMappingBuilder;
    private final List<IFilterBuilderHelper> filters;
    private final List<IFacetBuilderHelper> facets;

    public MapKeyValueAnnotationParser(FieldsMappingBuilder fieldsMappingBuilder, List<IFilterBuilderHelper> filters, List<IFacetBuilderHelper> facets) {
        this.fieldsMappingBuilder = fieldsMappingBuilder;
        this.filters = filters;
        this.facets = facets;
    }

    @Override
    public void parseAnnotation(MapKeyValue annotation, Map<String, Object> fieldDefinition, String pathPrefix, String nestedPrefix, Indexable indexable) {
        if (fieldDefinition.get("type") != null) {
            LOGGER.info(String.format("Overriding mapping for field {} for class {} was defined as type {}", indexable.getName(), indexable.getDeclaringClassName(),
                    fieldDefinition.get("type")));
            fieldDefinition.clear();
        }
        // the key of the map is a spring mapping.
        Map<String, Object> properties = new HashMap<>();
        fieldDefinition.put("type", "object");
        fieldDefinition.put("enabled", "true");
        fieldDefinition.put("properties", properties);

        Map<String, Object> keyFieldDefinition = new HashMap<>();
        properties.put("key", keyFieldDefinition);

        keyFieldDefinition.put("type", "text");

        keyFieldDefinition.put("store", annotation.store());
        keyFieldDefinition.put("index", annotation.indexType());
        // TODO doc_values
        keyFieldDefinition.put("term_vector", annotation.termVector());
        keyFieldDefinition.put("boost", annotation.boost());
        if (!StringField.NULL_VALUE_NOT_SPECIFIED.equals(annotation.nullValue())) {
            keyFieldDefinition.put("null_value", annotation.nullValue());
        }
        if (!NormEnabled.DEFAULT.equals(annotation.normsEnabled())) {
            Map<String, Object> norms = new HashMap<String, Object>();
            norms.put("enabled", annotation.normsEnabled().name().toLowerCase());
            if (!NormLoading.DEFAULT.equals(annotation.normsLoading())) {
                norms.put("loading", annotation.normsLoading());
            }
            keyFieldDefinition.put("norms", norms);
        }
        if (!IndexOptions.DEFAULT.equals(annotation.indexOptions())) {
            keyFieldDefinition.put("index_options", annotation.indexOptions());
        }
        if (!annotation.analyser().isEmpty()) {
            keyFieldDefinition.put("analyzer", annotation.analyser());
        }
        if (!annotation.indexAnalyzer().isEmpty()) {
            keyFieldDefinition.put("index_analyzer", annotation.indexAnalyzer());
        }
        if (!annotation.searchAnalyzer().isEmpty()) {
            keyFieldDefinition.put("search_analyzer", annotation.searchAnalyzer());
        }

        keyFieldDefinition.put("include_in_all", annotation.includeInAll());
        if (annotation.ignoreAbove() > 0) {
            keyFieldDefinition.put("ignore_above", annotation.ignoreAbove());
        }

        Map<String, Object> valueFieldDefinition = new HashMap<>();
        properties.put("value", valueFieldDefinition);

        // we need to process the map value type recursively
        Class<?> mapValueType = indexable.getComponentType(1);
        if (mapValueType != null) {
            Map<String, SourceFetchContext> fetchContext = new HashMap<>();
            try {
                this.fieldsMappingBuilder.parseFieldMappings(mapValueType, valueFieldDefinition, facets, filters, fetchContext,
                        indexable.getName() + ".value.", nestedPrefix);
            } catch (IntrospectionException e) {
                LOGGER.log(Level.SEVERE, "Fail to parse object class <" + mapValueType.getName() + ">", e);
            }
        } else {
            LOGGER.log(Level.SEVERE, "Cannot find value class for map with annotation MapKeyValue");
        }
    }
}