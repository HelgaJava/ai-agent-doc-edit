package ru.aialchemy.agent.util;

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
}
