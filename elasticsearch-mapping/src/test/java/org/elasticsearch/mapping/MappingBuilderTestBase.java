package org.elasticsearch.mapping;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Mauricio Maia on 13/09/2017.
 */
public class MappingBuilderTestBase {

    protected MappingBuilder mappingBuilder = new MappingBuilder();

    protected void assertSameContent(String content, String expectedContentFromFile) throws IOException {
        BufferedReader brMappingTest = new BufferedReader(new FileReader(Paths.get(expectedContentFromFile).toFile()));
        String expectedMapping = brMappingTest.readLine();
        Assert.assertEquals(expectedMapping, content);
    }

}
