(ns sempro.models.event
  (:require
    [sempro.db.core :as db]))

(defn create [event]
  "creates an event instance in the DB"
  (db/create-event<! event))

(defn get-id [id]
  "return the event with the given id"
  (first (db/get-event {:id id})))

(defn get-all []
  "return all events"
  (db/get-all-events))
