package org.elasticsearch.mapping.parser;

import org.elasticsearch.annotation.DateField;
import org.elasticsearch.mapping.Indexable;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Parse a {@link DateField} annotation.
 * 
 * @author luc boutier
 */
public class DateFieldAnnotationParser implements IPropertyAnnotationParser<DateField> {
    private static final Logger LOGGER = Logger.getLogger(DateFieldAnnotationParser.class.toString());

    public void parseAnnotation(DateField annotation, Map<String, Object> fieldDefinition, String pathPrefix, String nestedPrefix, Indexable indexable) {
        if (fieldDefinition.get("type") != null) {
            LOGGER.info(String.format("Overriding mapping for field %s for class %s was defined as type %s",
                    indexable.getName(),
                    indexable.getDeclaringClassName(),
                    fieldDefinition.get("type")));
            fieldDefinition.clear();
        }

        fieldDefinition.put("type", "date");
        fieldDefinition.put("store", annotation.store());
        fieldDefinition.put("index", annotation.index());
        fieldDefinition.put("boost", annotation.boost());
        fieldDefinition.put("include_in_all", annotation.includeInAll());
        fieldDefinition.put("ignore_malformed", annotation.ignoreMalformed());
    }
}