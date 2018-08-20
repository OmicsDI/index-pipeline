package uk.ac.ebi.ddi.pipeline.indexer.tasklet.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class TestUtils {
    public static String getResource(Class clazz, String resourcePath) throws IOException {
        try (InputStream inputStream = clazz.getResourceAsStream(resourcePath)) {
            return IOUtils.toString(inputStream, "UTF-8");
        }
    }
}
