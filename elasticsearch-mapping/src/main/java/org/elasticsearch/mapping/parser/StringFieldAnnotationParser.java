package org.elasticsearch.mapping.parser;

import org.elasticsearch.annotation.StringField;
import org.elasticsearch.mapping.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Parse a {@link StringField} annotation.
 * 
 * @author luc boutier
 */
public class StringFieldAnnotationParser implements IPropertyAnnotationParser<StringField> {
    private static final Logger LOGGER = Logger.getLogger(MappingBuilder.class.toString());

    public void parseAnnotation(StringField annotation, Map<String, Object> fieldDefinition, String pathPrefix, String nestedPrefix, Indexable indexable) {
        if (fieldDefinition.get("type") != null) {
            LOGGER.info(String.format("Overriding mapping for field %s for class %s was defined as type %s", indexable.getName(), indexable.getDeclaringClassName(),
                    fieldDefinition.get("type")));
            fieldDefinition.clear();
        }

        if(annotation.type() == StringType.KEYWORD) {
            fieldDefinition.put("type", "keyword");
        } else {
            fieldDefinition.put("type", "text");
        }

        fieldDefinition.put("store", annotation.store());
        fieldDefinition.put("index", annotation.indexType());
        // TODO doc_values
        if(annotation.type() == StringType.TEXT) {
            //term_vector is not supported for KEYWORD type
            fieldDefinition.put("term_vector", annotation.termVector());
        }

        fieldDefinition.put("boost", annotation.boost());
        if (!StringField.NULL_VALUE_NOT_SPECIFIED.equals(annotation.nullValue())) {
            fieldDefinition.put("null_value", annotation.nullValue());
        }
        if (!NormEnabled.DEFAULT.equals(annotation.normsEnabled())) {
            Map<String, Object> norms = new HashMap<String, Object>();
            norms.put("enabled", annotation.normsEnabled().name().toLowerCase());
            if (!NormLoading.DEFAULT.equals(annotation.normsLoading())) {
                norms.put("loading", annotation.normsLoading());
            }
            fieldDefinition.put("norms", norms);
        }
        if (!org.elasticsearch.mapping.IndexOptions.DEFAULT.equals(annotation.indexOptions())) {
            fieldDefinition.put("index_options", annotation.indexOptions());
        }
        if (!annotation.analyser().isEmpty()) {
            fieldDefinition.put("analyzer", annotation.analyser());
        }
        if (!annotation.indexAnalyzer().isEmpty()) {
            fieldDefinition.put("index_analyzer", annotation.indexAnalyzer());
        }
        if (!annotation.searchAnalyzer().isEmpty()) {
            fieldDefinition.put("search_analyzer", annotation.searchAnalyzer());
        }

        fieldDefinition.put("include_in_all", annotation.includeInAll());
        if (annotation.ignoreAbove() > 0) {
            fieldDefinition.put("ignore_above", annotation.ignoreAbove());
        }

        // FIXME annotation.positionOffsetGap();
    }
}