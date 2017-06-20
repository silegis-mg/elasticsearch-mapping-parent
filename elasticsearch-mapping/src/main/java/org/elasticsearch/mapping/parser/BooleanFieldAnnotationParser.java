package org.elasticsearch.mapping.parser;

import org.elasticsearch.annotation.BooleanField;
import org.elasticsearch.mapping.Indexable;
import org.elasticsearch.mapping.MappingBuilder;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Parse a {@link BooleanField} annotation.
 * 
 * @author luc boutier
 */
public class BooleanFieldAnnotationParser implements IPropertyAnnotationParser<BooleanField> {
    private static final Logger LOGGER = Logger.getLogger(MappingBuilder.class.toString());

    public void parseAnnotation(BooleanField annotation, Map<String, Object> fieldDefinition, String pathPrefix, String nestedPrefix,
            Indexable indexable) {
        if (fieldDefinition.get("type") != null) {
            LOGGER.info(String.format("Overriding mapping for field %s for class %s was defined as type %s", indexable.getName(), indexable.getDeclaringClassName(),
                    fieldDefinition.get("type")));
            fieldDefinition.clear();
        }

        fieldDefinition.put("type", "boolean");
        fieldDefinition.put("store", annotation.store());
        fieldDefinition.put("index", annotation.index());
        fieldDefinition.put("boost", annotation.boost());
        fieldDefinition.put("include_in_all", annotation.includeInAll());
    }
}