package org.elasticsearch.mapping;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class MatchFilterBuilderHelper extends AbstractFilterBuilderHelper {

    /**
     * Initialize the helper to build match filters.
     *
     * @param nestedPath The path to the nested object if any.
     * @param filterPath The path to the field to filter.
     */
    public MatchFilterBuilderHelper(final String nestedPath, final String filterPath) {
        super(nestedPath, filterPath);
    }

    @Override
    public QueryBuilder buildFilter(final String key, final String... values) {
        preProcessValues(values);
        if (values.length == 1) {
            if (values[0] == null) {
                return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(key));
            }
            return QueryBuilders.matchQuery(key, values[0]);
        }
        return QueryBuilders.matchQuery(key, values);
    }

    @Override
    public QueryBuilder buildQuery(String key, String[] values) {
        preProcessValues(values);
        if (values.length == 1) {
            return QueryBuilders.matchQuery(key, values[0]);
        }
        return QueryBuilders.matchQuery(key, values);
    }

    private void preProcessValues(String[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Filter values cannot be null or empty");
        }
    }
}