package org.elasticsearch.mapping;

import org.elasticsearch.mapping.model.City;
import org.elasticsearch.mapping.model.Person;
import org.junit.Before;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.io.IOException;

/**
 * Test the mappings.
 * 
 * @author luc boutier
 */
public class MappingBuilderTest extends MappingBuilderTestBase {

    @Before
    public void setUp() throws IntrospectionException, IOException {
        mappingBuilder.initialize("org.elasticsearch.mapping.model");
    }

    @Test
    public void testSimpleClassMapping() throws IntrospectionException, IOException {
        String personMapping = mappingBuilder.getMapping(Person.class);
        assertSameContent(personMapping, "src/test/resources/person-mapping.json");
    }

    @Test
    public void testSettingsAndMapping() throws IntrospectionException, IOException {
        String citySettings = mappingBuilder.getIndexSettings(City.class);
        assertSameContent(citySettings, "src/test/resources/city-settings.json");

        String cityMapping = mappingBuilder.getMapping(City.class);
        assertSameContent(cityMapping, "src/test/resources/city-mapping.json");
    }


}
