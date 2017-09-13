package org.elasticsearch.mapping;

import org.elasticsearch.mapping.model.DateListModel;
import org.junit.Before;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.io.IOException;

public class DateListTest extends MappingBuilderTestBase {

    @Before
    public void setUp() throws IntrospectionException, IOException {
        mappingBuilder.initialize("org.elasticsearch.mapping.model");
    }

    @Test
    public void testDateListClassMapping() throws IntrospectionException, IOException {
        String dateMapping = mappingBuilder.getMapping(DateListModel.class);
        assertSameContent(dateMapping, "src/test/resources/datelist-mapping.json");
    }

}
