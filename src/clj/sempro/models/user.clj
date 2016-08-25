(ns sempro.models.user
  (:require
    [bouncer.validators :as v]
    [bouncer.core   :as b]
    [sempro.db.core :as db]
    [buddy.hashers  :as hashers]))

(def hashing-options {:alg :pbkdf2+sha256 :salt "salatic"})
(defn hash-pass
  "hash and salt pass for storage in DB"
  [password]
  (hashers/encrypt password hashing-options))

(defn password-matches?
  "hashes the password and matches it agains the email"
  [email password]
  (let [hashed-pass (-> {:email email}
                        (db/get-user-by-email)
                        (get :pass))]
    (hashers/check password hashed-pass)))

(defn validate
  "`user` must be a map
  returns a vector with 2 elements
  the first argument is `nil` if user is valid
  else it is a map with the errors"
  [user]
  (b/validate user                                          ; TODO: add better validation
    :first_name v/required                                  ; may contain only letters
    :last_name  v/required
    :email [v/email     v/required]
    :pass  [v/required [v/min-count 6]]))

(defn create
  "`user` must be a map
  returns a vector with 2 elements [bool, {map}]
  if user is valid: [true, {user}]
  else:             [false, {validation-errors}]"
  [user]
  (let [parsed (validate user)
        errors (first  parsed)
        user   (second parsed)]
    (if (nil? errors)
      (let [pass   (hash-pass (:pass user))
            row-id (db/create-user! (assoc user :pass pass))]
        (conj [true] (-> (assoc user :id (row-id (first (keys row-id))))
                         (dissoc :pass))))
      [false {:error {:input-validation errors}}])))

;;
;; user status and jobs
;;
(defn set-status [id status]
  (let [status {:id id :status status}]
    (db/set-user-status! status)))

(defn set-job [id job]
  (let [job {:id id :job job}]
    (db/set-user-job! job)))

(def status
  {:fux "Fux"
   :AB  "ABursch"
   :IAB "IBursch"
   :AUS "Auswertiger"
   :AH  "Alter Herr"})

(def bosses
  {:X   "ErstX"
   :XX  "ZweitX"
   :XXX "DrittX"
   :FM  "FuxMajor"})

(def jobs
  {:BKW "Bierwart"
   :HKW "Hauptkasse"
   :SW  "Sportwart"
   :CW  "Coleurwart"
   :HW  "Hauswart"
   :VD  "Vergnugungsdirektor"
   :KD  "Keildirektor"})

(defn get-users-by-status [status]
  (db/get-users-by-status {:status status}))

(defn get-fuxe      [] (get-users-by-status (:fux status)))
(defn get-aburschen [] (get-users-by-status (:AB  status)))
(defn get-iburschen [] (get-users-by-status (:IAB status)))
(defn get-auswertig [] (get-users-by-status (:AUS status)))
(defn get-alteherrn [] (get-users-by-status (:AH  status)))

(defn get-aktivitas []
  (concat get-fuxe
          get-aburschen
          get-iburschen))

(defn get-everyone []
  (concat get-aktivitas
          get-auswertig
          get-alteherrn))
