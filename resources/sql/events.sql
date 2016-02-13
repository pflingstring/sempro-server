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