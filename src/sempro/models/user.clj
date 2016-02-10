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
  (b/validate user                                          ; TODO: add better validation
    :first_name v/required                                  ; may contain only letters
    :last_name  v/required
    :email [v/email v/required]
    :pass [v/required [v/min-count 6]]))

(defn create-user [user]
  "`user` must be a map
  returns a vector with 2 elements [bool, {map}]
  if user is valid: [true, {user}]
  else:             [false, {validation-errors}]"
  (let [parsed-user (validate-user user)
        errors (first  parsed-user)
        user   (second parsed-user)]
    (if (= nil errors)
      (let [id (db/create-user<! user)]
        (conj [true] (-> (assoc user :id (id (first (keys id))))
                         (dissoc :pass))))
      [false {:error {:input-validation errors}}])))

