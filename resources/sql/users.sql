-- :name create-user! :i! :raw
-- creates a new user record
INSERT INTO users
(first_name, last_name, email, pass)
VALUES (:first_name, :last_name, :email, :pass)

-- :name update-user! :! :n
-- update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name set-user-status! :! :n
-- update status for given id
UPDATE users SET
  status = :status
WHERE id = :id

-- :name set-user-job! :1 :n
-- update job for given id
UPDATE users SET
  job = :job
WHERE id = :id

-- :name get-user :? :1
-- retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name get-user-by-email :? :1
-- retrieve a user given the email
SELECT * FROM users
WHERE email = :email

-- :name get-users-by-status :? :*
SELECT * FROM users
WHERE status = :status

-- :name get-users-by-job :? :*
SELECT * from users
WHERE job = :job


-- :name delete-user! :! :n
-- delete a user given the id
DELETE FROM users
WHERE id = :id

-- :name delete-user-by-email! :! :n
-- delete a user given the email
DELETE FROM users
WHERE email = :email
