(ns sempro.models.user
  (:require
    [sempro.db.core :as db]
    [bouncer.validators :as v]
    [bouncer.core :as b]
    ))

(defn validate-user [user]
  "`user` must be a map
  returns a vector with 2 elements
  the first argument is `nil` if user is valid
  else it is a map with the errors"
  (b/validate user
    :first_name v/required
    :last_name  v/required
    :email [v/email v/required]
    :pass   v/required))

(defn create-user [user & env]
  "`user` must be a map
  returns a vector with 2 elements [bool, {map}]
  if user is valid: [true, {user}]
  else:             [false, {validation-errors}]"
  (let [parsed-user (validate-user user)
        errors (first  parsed-user)
        user   (second parsed-user)]
    (if (= nil errors)
      (if (= (first env) "test")
        (do (db/create-user! user db/test-jdbc) [true user])
        (do (db/create-user! user) [true user]))
      [false errors])))

