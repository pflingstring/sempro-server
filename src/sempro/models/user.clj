(ns sempro.models.user
  (:require
    [sempro.db.core :as db]
    [bouncer.validators :as v]
    [bouncer.core :as b]
    [buddy.hashers :as hashers]))

(def hashing-options {:alg :pbkdf2+sha256 :salt "salatic"})
(defn hash-pass [password]
  (hashers/encrypt password hashing-options))

(defn password-matches? [email password]
  (let [hashed-pass (-> (assoc {} :email email)
                        (db/get-user-by-email)
                        (first)
                        (get :pass))]
    (hashers/check password hashed-pass)))

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
      (let [pass (hash-pass (:pass user))
            id (db/create-user<! (assoc user :pass pass))]
        (conj [true] (-> (assoc user :id (id (first (keys id))))
                         (dissoc :pass))))
      [false {:error {:input-validation errors}}])))

