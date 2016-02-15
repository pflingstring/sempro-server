(ns sempro.models.event
  (:require
    [sempro.db.core :as db]
    [bouncer.core   :as b]
    [bouncer.validators :as v]
    [clj-time.format    :as f]))

(defn validate [event]
  "`event` must be a map
  returns a vector with 2 elements
  the first element is `nil` if event is valid i.e no errors
    else it contains a map with the validation errors
  the second argument is the `event` itself"
  (b/validate event
    :name v/required
    :description v/required
    :date v/datetime [(:date-hour-minute f/formatters)]))

(defn create [event]
  "`event` must be a map
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
  returns the number of deleted rows
  i.e:  0 if nothing was deleted,
        1 otherwise"
  (db/delete-event! {:id id}))
