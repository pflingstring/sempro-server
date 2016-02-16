-- name: create-event<!
-- creates a new event record
INSERT INTO events
       (name  , description , date)
VALUES (:name, :description, :date)

-- name: get-event
-- retrieve event given the id
SELECT * FROM events
WHERE id = :id

-- name: get-all-events
-- retrieve all events
SELECT * FROM events;

-- name: delete-event!
-- delete event with de given id
DELETE FROM events
WHERE id = :id

-- name: update-event!
-- update event given the id
UPDATE events SET
  name          = :name
  , description = :description
  , date        = :date
  , info        = :info
WHERE id = :id