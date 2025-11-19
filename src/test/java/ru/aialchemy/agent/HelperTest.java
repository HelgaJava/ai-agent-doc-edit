package ru.aialchemy.agent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.aialchemy.agent.models.docIn.WordDocContent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class HelperTest {
    public static byte[] getFileContentFromResources(String fileName) throws IOException, URISyntaxException {
        var resource = HelperTest.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File not found in resources: " + fileName);
        }
        return Files.readAllBytes(Paths.get(resource.toURI()));
    }

    public static String createEmptyJsonStr() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wordDocReplaces", jsonArray);
        return jsonObject.toString();

    }

    public static WordDocContent createDocContent() {
       return new WordDocContent(
                "document.docx", "Content", 1, List.of("Content"), "docx", null
        );
    }
}
