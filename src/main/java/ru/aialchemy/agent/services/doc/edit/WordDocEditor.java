package ru.aialchemy.agent.services.doc.edit;

import org.springframework.stereotype.Component;
import ru.aialchemy.agent.models.WordDocContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WordDocEditor {

    /**
     * Точечное редактирование текста в WordDocContent
     */
    public WordDocContent editContent(WordDocContent original, String searchText, String replacement) {
        // Редактируем полный текст
        String editedContent = original.content().replace(searchText, replacement);

        // Редактируем параграфы
        List<String> editedParagraphs = original.paragraphs().stream()
                .map(p -> p.replace(searchText, replacement))
                .collect(Collectors.toList());

        // Возвращаем новый объект с теми же метаданными
        return new WordDocContent(
                original.fileName(),
                editedContent,
                editedParagraphs.size(),
                editedParagraphs,
                original.fileExtension()
        );
    }

    /**
     * Множественные замены
     */
    public WordDocContent editContent(WordDocContent original, Map<String, String> replacements) {
        String editedContent = original.content();
        List<String> editedParagraphs = new ArrayList<>();

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            editedContent = editedContent.replace(entry.getKey(), entry.getValue());
        }

        for (String paragraph : original.paragraphs()) {
            String editedParagraph = paragraph;
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                editedParagraph = editedParagraph.replace(entry.getKey(), entry.getValue());
            }
            editedParagraphs.add(editedParagraph);
        }

        return new WordDocContent(
                original.fileName(),
                editedContent,
                editedParagraphs.size(),
                editedParagraphs,
                original.fileExtension()
        );
    }

}
