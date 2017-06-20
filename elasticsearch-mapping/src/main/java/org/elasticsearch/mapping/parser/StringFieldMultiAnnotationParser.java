package org.elasticsearch.mapping.parser;

import org.elasticsearch.annotation.StringField;
import org.elasticsearch.annotation.StringFieldMulti;
import org.elasticsearch.mapping.Indexable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Parse a {@link StringField} annotation.
 * 
 * @author luc boutier
 */
public class StringFieldMultiAnnotationParser implements IPropertyAnnotationParser<StringFieldMulti> {
    private static final Logger LOGGER = Logger.getLogger(StringFieldMultiAnnotationParser.class.toString());
    private StringFieldAnnotationParser wrapped = new StringFieldAnnotationParser();

    public void parseAnnotation(StringFieldMulti annotation, Map<String, Object> fieldDefinition, String pathPrefix, String nestedPrefix, Indexable indexable) {
        if (fieldDefinition.get("type") != null) {
            LOGGER.info(String.format("Overriding mapping for field %s for class %s was defined as type %s", indexable.getName(), indexable.getDeclaringClassName(),
                    fieldDefinition.get("type")));
            fieldDefinition.clear();
        }

        StringField mainStringField = annotation.main();
        wrapped.parseAnnotation(mainStringField, fieldDefinition, pathPrefix, nestedPrefix, indexable);

        Map<String, Object> multiFields = new HashMap<>();

        for (int i = 0; i < annotation.multi().length; i++) {
            StringField multi = annotation.multi()[i];
            Map<String, Object> multiFieldDefinition = new HashMap<>();
            wrapped.parseAnnotation(multi, multiFieldDefinition, pathPrefix, nestedPrefix, indexable);
            multiFields.put(annotation.multiNames()[i], multiFieldDefinition);
        }
        fieldDefinition.put("fields", multiFields);
    }
}