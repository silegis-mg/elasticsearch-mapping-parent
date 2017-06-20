package org.elasticsearch.mapping.parser;

import org.elasticsearch.annotation.DateFormat;
import org.elasticsearch.mapping.Indexable;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Parse a {@link DateFormat} annotation.
 * 
 * @author luc boutier
 */
public class DateFormatAnnotationParser implements IPropertyAnnotationParser<DateFormat> {
    private static final Logger LOGGER = Logger.getLogger(DateFormatAnnotationParser.class.getName());

    public void parseAnnotation(DateFormat annotation, Map<String, Object> fieldDefinition, String pathPrefix, String nestedPrefix,
            Indexable indexable) {
        if (fieldDefinition.get("type") == null) {
            fieldDefinition.put("type", "date");
        } else if (!fieldDefinition.get("type").equals("date")) {
            LOGGER.info(String.format("Overriding mapping for field %s for class %s was defined as type %s",
                    indexable.getName(),
                    indexable.getDeclaringClassName(),
                    fieldDefinition.get("type")));
            fieldDefinition.clear();
        }

        fieldDefinition.put("format", annotation.format());
    }
}