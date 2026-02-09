# ai-agent-doc-edit

Приложение для AI анализа и редактирования Word-отчётов с помощью LLM (Gigachat) и локальных инструментов обработки документа.

## 1. Бизнес проблема:

Организации часто имеют отчёты в формате Word, которые требуют массовых правок, согласований или извлечения структурированных данных (таблицы, ячейки). 
Ручная обработка таких документов требует времени и уязвима к ошибкам; интеграция с LLM позволяет формулировать правки на естественном языке, 
но требует безопасного механизма передачи контекста и применения изменений в исходном документе.

## 2. Решение (Architecture & Solution)

Проект реализует серверное Spring Boot приложение, которое:
- Принимает multipart-запросы с файлом Word и JSON-запросом пользователя.
- Парсит и валидирует содержимое документа (текст, таблицы) в модель `WordDocContent`.
- Формирует контекст и вызывает LLM через Spring AI (`GigaChatSrv`).
- Предоставляет LLM набор локальных инструментов (`@Tool` в `ContentTools`) для безопасного доступа к структуре документа и применения правок.
- После получения результата редактор (`WordDocEditor`) вносит правки в Word-файл и сохраняет итог.

Ключевые компоненты:
- Контроллер: `AiController` — точка входа HTTP API.
- Сервис LLM: `GigaChatSrv` — обёртка над `ChatClient`.
- Инструменты (tools): `ContentTools` — методы, доступные модели для чтения/модификации контента.
- Редактор: `WordDocEditor` — применяет изменения в DOCX через Apache POI.

Диаграмма вызовов и взаимодействия компонентов:

```mermaid
flowchart LR
    A[Client] --> B[AiController]
    B --> C[GigaChatSrv]
    C <--> D[ContentTools]
    C --> E[WordDocEditor]
    E --> F[FileSystem]
    C -.-> B
    B -.-> A

    linkStyle 0 stroke:#2196f3
    linkStyle 1 stroke:#4caf50
    linkStyle 2,3 stroke:#ff9800
    linkStyle 4 stroke:#f44336
    linkStyle 5 stroke:#9c27b0
```
```plantuml
@startuml
participant Client
participant AiController as "/api/v1/documents/edit\n(AiController)"
participant GigaChatSrv as "GigaChatSrv\n(LLM client)"
participant ContentTools as "ContentTools\n(@Tool)"
participant WordDocEditor as "WordDocEditor"
participant FileSystem as "File system\n(save.custom.path)"

Client -> AiController : multipart (userRq + file)
AiController -> GigaChatSrv : parse & validate -> WordDocContent
GigaChatSrv -> ContentTools : provide toolContext (fileContent)
ContentTools -> GigaChatSrv : read/tables/text (tool methods)
GigaChatSrv -> WordDocEditor : instructions / jobResult (apply edits)
WordDocEditor -> FileSystem : save edited file
GigaChatSrv -> AiController : return result / response
@enduml
```


## 3. Техническая реализация (коротко)

- Формат входа: multipart/form-data: JSON с запросом к LLM и файл Word для передачи в контекст.
- Tool-context: `fileContent: WordDocContent` передаётся в LLM, чтобы локальные `@Tool` могли возвращать структурированные данные (списки таблиц, ячеек и т.д.).
- Применение изменений: LLM ищет и исправляет ошибки, возвращает JSON с результатом работы, который используется `WordDocEditor` для преобразования в конкретные операции Apache POI (замены текста, правка ячеек таблиц).
- После `WordDocEditor` сохраняет исправленный документ.

## 4. Стек технологий:

- Язык: Java 17
- Фреймворк: Spring Boot 3.x
- LLM-интеграция: Spring AI (Gigachat starter)
- Работа с Word: Apache POI (poi, poi-ooxml)
- JSON: Jackson
- Lombok для логов/снижения шаблонного кода
- Сборка: Maven

## 5. Ссылки:
- Демо-видео работы агента: https://t.me/ai_alchemy2025/10
