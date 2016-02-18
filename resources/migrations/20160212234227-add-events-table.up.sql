CREATE TABLE events (
    id INTEGER PRIMARY KEY
    , name          TEXT
    , description   TEXT
    , date          TEXT
    , info          TEXT    DEFAULT  ""
    , can_read      TEXT    DEFAULT  ""
    , can_write     TEXT    DEFAULT  ""
);