(ns sempro.models.event
  (:require
    [sempro.db.core :as db]
    [bouncer.core :as b]
    [bouncer.validators :as v]))

(defn validate [event]
  "`event` must be a map
  returns a vector with 2 elements
  the first element is `nil` if event is valid i.e no errors
    else it contains a map with the validation errors
  the second argument is the `event` itself"
  (b/validate event
    :name v/required
    :description v/required
    :date [v/required v/datetime]))

(defn create [event]
  (let [parsed-event (validate event)
        errors (first  parsed-event)
        event  (second parsed-event)]
    (if (= nil errors)
      (do (db/create-event<! event)
          [true event])
      (false {:error {:input-validation errors}}))))

(defn get-id [id]
  "return the event with the given id"
  (first (db/get-event {:id id})))

(defn get-all []
  "return all events"
  (db/get-all-events))

(defn delete [id]
  "deletes the event with the given ID"
  (db/delete-event! {:id id}))
