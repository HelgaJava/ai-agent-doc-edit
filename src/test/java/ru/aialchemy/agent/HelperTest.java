package ru.aialchemy.agent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HelperTest {
    public static byte[] getFileContentFromResources(String fileName) throws IOException, URISyntaxException {
        var resource = HelperTest.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File not found in resources: " + fileName);
        }
        return Files.readAllBytes(Paths.get(resource.toURI()));
    }
}
