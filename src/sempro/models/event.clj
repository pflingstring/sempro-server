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
(def info-validator #(%1 %2 :info [v/string v/required]))

;; validators needed when creating an event
(def create-validators [name-validator
                        dscr-validator
                        date-validator])

;; validators needed when updating an event
(def update-validators (conj create-validators
                             info-validator))

(defn validate-event
  "`event` must be a map
  `fn` can be either `b/valid?` or `b/validate`"
  [event fn validators]
  (map #(% fn event) validators))

(defn is-valid?
  [event validators]
  (->> (validate-event event b/valid? validators)
       (some #(= false %))
       (boolean)
       (not)))

(defn get-errors
  "returs validation errors"
  [parsed]
  (into {} (filter #(not (nil? %))
                   (map first parsed))))

(defn validate
  "`event` must be a map
  returns a vector with 2 elements
  the first element is `nil` if event is valid i.e no errors
    else it contains a map with the validation errors
  the second argument is the `event` itself"
  [event validators]
  (if (is-valid? event validators)
    [nil event]
    [(get-errors (validate-event event b/validate validators))]))

(defn create
  "`event` must be a map
  creates a new event in DB
  returns a vector with 2 elements [bool, {map}]
  if event is valid: [true, {user}]
  else:              [false, {validation-errors}]"
  [event]
  (let [parsed (validate event create-validators)
        errors (first  parsed)
        event  (second parsed)]
    (if (nil? errors)
      (do (db/create-event<! event)
          [true event])
      [false {:error {:input-validation errors}}])))

(defn get-id
  "id must be an Integer
  returns a map with the required id"
  [id]
  (first (db/get-event {:id id})))

(defn get-all
  "returns a LazySeq with all events"
  []
  (db/get-all-events))

(defn delete
  "id must be an Integer
  deletes the event with the given ID from DB
  returns the number of deleted rows
  i.e:  0 if nothing was deleted,
        1 otherwise"
  [id]
  (db/delete-event! {:id id}))

(defn update-event
  "`event` must be a map
  updates the event with givend ID
  returns a vector with 2 elements [bool, {map}]
  if event is valid: [true, {user}]
  else:              [false, {validation-errors}]"
  [id event]
  (let [parsed (validate event update-validators)
        errors (first  parsed)
        event  (merge {:id id} (second parsed))]
    (if (nil? errors)
      (do (db/update-event! event)
          [true event])
      [false {:error {:input-validation errors}}])))

(defn get-permissions
  [id]
  (first (db/get-event-permissions {:id id})))

(defn update-permissions
  "updates permissions for given ID
  note that it will replace the existing permissions"
  [id readers writers]
  (db/update-event-permissions!
    {:id        id
     :readers readers
     :writers writers}))

(defn add-permissions
  "adds permisions for given ID"
  [id readers writers]
  (db/add-event-permissions!
    {:id id
     :readers readers
     :writers writers}))
