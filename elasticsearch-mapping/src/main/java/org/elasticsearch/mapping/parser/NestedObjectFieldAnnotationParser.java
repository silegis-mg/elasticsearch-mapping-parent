package org.elasticsearch.mapping.parser;

import org.elasticsearch.annotation.NestedObject;
import org.elasticsearch.mapping.*;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NestedObjectFieldAnnotationParser implements IPropertyAnnotationParser<NestedObject> {
    private static final Logger LOGGER = Logger.getLogger(NestedObjectFieldAnnotationParser.class.toString());

    private final FieldsMappingBuilder fieldsMappingBuilder;
    private final List<IFilterBuilderHelper> filters;
    private final List<IFacetBuilderHelper> facets;

    public NestedObjectFieldAnnotationParser(FieldsMappingBuilder fieldsMappingBuilder, List<IFilterBuilderHelper> filters, List<IFacetBuilderHelper> facets) {
        this.fieldsMappingBuilder = fieldsMappingBuilder;
        this.filters = filters;
        this.facets = facets;
    }

    @Override
    public void parseAnnotation(NestedObject annotation, Map<String, Object> fieldDefinition, String pathPrefix, String nestedPrefix, Indexable indexable) {
        if (fieldDefinition.get("type") != null) {
            LOGGER.info(String.format("Overriding mapping for field %s for class %s was defined as type %s", indexable.getName(), indexable.getDeclaringClassName(),
                    fieldDefinition.get("type")));
            fieldDefinition.clear();
        }

        fieldDefinition.put("type", "nested");
        Map<String, SourceFetchContext> fetchContext = new HashMap<>();
        // nested types can provide replacement class to be managed. This can be usefull to override map default type for example.
        Class<?> replaceClass = annotation.nestedClass().equals(NestedObject.class) ? indexable.getType() : annotation.nestedClass();
        try {
            this.fieldsMappingBuilder.parseFieldMappings(replaceClass, fieldDefinition, facets, filters, fetchContext, indexable.getName() + ".",
                    indexable.getName());
        } catch (IntrospectionException e) {
            LOGGER.log(Level.SEVERE, "Fail to parse nested class <" + replaceClass.getName() + ">", e);
        }
    }
}