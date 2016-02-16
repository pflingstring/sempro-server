CREATE TABLE users (
  id            INTEGER   PRIMARY KEY
  , first_name  TEXT
  , last_name   TEXT
  , email       TEXT      UNIQUE
  , phone       TEXT
  , pass        TEXT
  , status      TEXT      DEFAULT "Fux"
  , job         TEXT      DEFAULT ""
);
