package org.elasticsearch.mapping.parser;

import org.elasticsearch.annotation.ObjectField;
import org.elasticsearch.mapping.*;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parse a {@link org.elasticsearch.annotation.ObjectField} to enrich the mapping
 */
public class ObjectFieldAnnotationParser implements IPropertyAnnotationParser<ObjectField> {
    private static final Logger LOGGER = Logger.getLogger(ObjectFieldAnnotationParser.class.toString());

    private final FieldsMappingBuilder fieldsMappingBuilder;
    private final List<IFilterBuilderHelper> filters;
    private final List<IFacetBuilderHelper> facets;

    public ObjectFieldAnnotationParser(FieldsMappingBuilder fieldsMappingBuilder, List<IFilterBuilderHelper> filters, List<IFacetBuilderHelper> facets) {
        this.fieldsMappingBuilder = fieldsMappingBuilder;
        this.filters = filters;
        this.facets = facets;
    }

    @Override
    public void parseAnnotation(ObjectField annotation, Map<String, Object> fieldDefinition, String pathPrefix, String nestedPrefix, Indexable indexable) {
        if (fieldDefinition.get("type") != null) {
            LOGGER.info(String.format("Overriding mapping for field %s for class %s was defined as type %s",
                    indexable.getName(),
                    indexable.getDeclaringClassName(),
                    fieldDefinition.get("type")));
            fieldDefinition.clear();
        }

        Class<?> objectClass = annotation == null ? ObjectField.class : annotation.objectClass();
        Boolean enabled = annotation == null ? true : annotation.enabled();

        fieldDefinition.put("type", "object");
        fieldDefinition.put("enabled", enabled);
        if (enabled) {
            Map<String, SourceFetchContext> fetchContext = new HashMap<>();
            // nested types can provide replacement class to be managed. This can be usefull to override map default type for example.
            Class<?> replaceClass = objectClass.equals(ObjectField.class) ? indexable.getType() : objectClass;
            try {
                String newPrefix = pathPrefix == null ? indexable.getName() + "." : pathPrefix + indexable.getName() + ".";
                this.fieldsMappingBuilder.parseFieldMappings(replaceClass, fieldDefinition, facets, filters, fetchContext, newPrefix, nestedPrefix);
            } catch (IntrospectionException e) {
                LOGGER.log(Level.SEVERE, "Fail to parse object class <" + replaceClass.getName() + ">", e);
            }
        }
    }
}
