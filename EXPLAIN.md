## Поиск документов и индексы

### Пример поискового запроса

Поиск документов по статусу, автору и периоду дат (упрощённый SQL, соответствующий JPA-запросу):

```sql
EXPLAIN ANALYZE
SELECT d.id, d.doc_number, d.author, d.title, d.status, d.created_at, d.updated_at
FROM documents d
WHERE d.status = 'SUBMITTED'
  AND d.author = 'generator'
  AND d.created_at BETWEEN TIMESTAMP '2025-01-01' AND TIMESTAMP '2025-12-31'
ORDER BY d.created_at DESC
LIMIT 50 OFFSET 0;
```

### Используемые индексы

В Liquibase-миграции созданы индексы:

- `idx_documents_status` на поле `status`
- `idx_documents_author` на поле `author`
- `idx_documents_created_at` на поле `created_at`

Для типичного запроса со статусом и периодом по дате оптимизатор PostgreSQL может:

- Использовать индекс `idx_documents_status` для быстрого отбора по статусу.
- Далее отфильтровать по диапазону `created_at` (и, при необходимости, по автору).

Для высоконагруженного сценария с частыми поисками по нескольким фильтрам можно добавить составной индекс, например:

```sql
CREATE INDEX IF NOT EXISTS idx_documents_status_created_at_author
  ON documents (status, created_at DESC, author);
```

Тогда план выполнения будет использовать один составной индекс для диапазонного условия по `created_at` и точечного по `status` и `author`, что уменьшит число обращений к таблице и ускорит поиск.

