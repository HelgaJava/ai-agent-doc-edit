package ru.aialchemy.agent.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Утилитный класс с вспомогательными методами
 */
public class DocUtil {
    /**
     * Получение расширения файла
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        var extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return isValidExtension(extension) ? extension : null;
    }

    private static boolean isValidExtension(String extension) {
        return "docx".equals(extension) || "doc".equals(extension);
    }

    public static String cleanJsonStr(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Пустой ответ от LLM");
        }

        // Ищем начало JSON (первая {)
        int jsonStart = jsonStr.indexOf('{');
        if (jsonStart == -1) {
            throw new IllegalArgumentException("JSON не найден в ответе LLM");
        }

        String json = jsonStr.substring(jsonStart);

        // Убираем экранирование кавычек
        json = json.replace("\\\"", "\"");

        // Проверяем валидность JSON
        if (!isValidJson(json)) {
            throw new IllegalArgumentException("Невалидный JSON после очистки");
        }

        return json;
    }

    private static boolean isValidJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
