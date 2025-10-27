package ru.aialchemy.agent.util;

public class DocUtil {
    /**
     * Получение расширения файла
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
