-- name: create-user<!
-- creates a new user record
INSERT INTO users
(first_name, last_name, email, pass)
VALUES (:first_name, :last_name, :email, :pass)

-- name: update-user!
-- update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- name: get-user
-- retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- name: get-user-by-email
-- retrieve a user given the email
SELECT * FROM users
WHERE email = :email

-- name: delete-user!
-- delete a user given the id
DELETE FROM users
WHERE id = :id

-- name: delete-user-by-email!
-- delete a user given the email
DELETE FROM users
WHERE email = :email