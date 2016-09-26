-- :name create-event! :i! :raw
-- creates a new event record
INSERT INTO events
       (name  , description , date , can_read , can_write)
VALUES (:name, :description, :date, :can_read, :can_write);

-- :name get-event :? :1
-- retrieve event given the id
SELECT * FROM events
WHERE id = :id;

-- :name get-all-events :? :*
-- retrieve all events
SELECT * FROM events;

-- :name delete-event! :! :n
-- delete event with de given id
DELETE FROM events
WHERE id = :id;

-- :name update-event! :! :n
-- update event given the id
UPDATE events SET
  name          = :name
  , description = :description
  , date        = :date
  , info        = :info
WHERE id = :id;

-- :name get-event-permissions :? :1
-- get can_write and can_read columns for given id
SELECT can_read, can_write
FROM events
WHERE id = :id;

-- :name update-event-permissions! :! :n
-- update permissions for given ID
UPDATE events SET
  can_read  = :readers,
  can_write = :writers
WHERE id = :id;

-- :name add-event-permissions! :! :n
-- add permissions for given ID
UPDATE events SET
  can_read  = CASE WHEN :readers != ""
              THEN can_read  || " " || :readers
              ELSE can_read END,

  can_write = CASE WHEN :writers != ""
              THEN can_write || " " || :writers
              ELSE can_write END
WHERE id = :id;