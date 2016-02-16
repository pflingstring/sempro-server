(ns sempro.models.event
  (:require
    [sempro.db.core :as db]
    [bouncer.core   :as b]
    [bouncer.validators :as v]
    [clj-time.format    :as f]))

;; validators
(def name-validator #(%1 %2 :name [v/required v/string]))
(def date-validator #(%1 %2 :date [v/datetime (:date-hour-minute f/formatter)]))
(def dscr-validator #(%1 %2 :description [v/string v/required]))

(def validators [name-validator
                 dscr-validator
                 date-validator])

(defn validate-event [event type]
  "`event` must be a map
  `type` can be either `b/valid?` or `b/validate`"
  (map #(% type event) validators))

(defn is-valid? [event]
  (->> (validate-event event b/valid?)
       (some #(= false %))
       (boolean)
       (not)))

(defn get-errors [parsed]
  (into {} (filter #(not (nil? %))
                   (map first parsed))))

(defn validate [event]
  "`event` must be a map
  returns a vector with 2 elements
  the first element is `nil` if event is valid i.e no errors
    else it contains a map with the validation errors
  the second argument is the `event` itself"
  (if (is-valid? event)
    [nil event]
    [(get-errors (validate-event event b/validate))]))

(defn create [event]
  "`event` must be a map
  creates a new event in DB
  returns a vector with 2 elements [bool, {map}]
  if event is valid: [true, {user}]
  else:              [false, {validation-errors}]"
  (let [parsed (validate event)
        errors (first  parsed)
        event  (second parsed)]
    (if (nil? errors)
      (do (db/create-event<! event)
          [true event])
      [false {:error {:input-validation errors}}])))

(defn get-id [id]
  "id must be an Integer
  returns a map with the required id"
  (first (db/get-event {:id id})))

(defn get-all []
  "returns a LazySeq with all events"
  (db/get-all-events))

(defn delete [id]
  "id must be an Integer
  deletes the event with the given ID from DB
  returns the number of deleted rows
  i.e:  0 if nothing was deleted,
        1 otherwise"
  (db/delete-event! {:id id}))

(defn update-name [id name]
  (let [name (assoc {:name name} :id id)]
    (when (name-validator b/valid? name)
     (db/update-event-name! name))))

(defn update-event [id event]
  "`id` must be an Integer
  `event` must be a map with {:event-field updated-value}
    it may contain multiple :event-fields
  updates the event in DB
  returns a vector with 2 elements [bool {map}]
  if successfuly updated: [true  {:updated values}]
  else:                   [false {errors]"
  )
