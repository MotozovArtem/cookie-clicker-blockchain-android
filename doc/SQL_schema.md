## SQL schema

Table: Block
```sqlite
CREATE TABLE blocks (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  message VARCHAR(255),
  goal  INTEGER,
  creation_time  INTEGER,
  hash_of_previous_block  VARCHAR(32),
  hash_of_block  VARCHAR(32)
)
```
