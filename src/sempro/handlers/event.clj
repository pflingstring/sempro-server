(ns sempro.handlers.event
  (:require
    [ring.util.http-response :refer [ok bad-request created no-content]]
    [sempro.utils.response   :refer [create-response]]
    [sempro.models.event :as m]
    [sempro.utils.error  :as err]))

(defn create [req]
  "`req` must be a map with an event
  tries to validate the req and returns:
   > ok  response if event is valid
   > bad response if not, with error msg in body
  if a exception is thrown also returns bad response"
  (try
    (let [parsed (m/create req)
          valid? (first parsed)
          body (second parsed)]
      (if valid?
        (create-response created body)
        (create-response bad-request body)))
    (catch Exception e
      (create-response bad-request (err/sql-exception (.getMessage e))))))

(defn get-all []
  "returns an ok response with all events
  if there are any"
  (let [events (m/get-all)]
    (if-not (empty? events)
      (create-response ok events)
      (no-content))))

(defn get-id [id]
  "`id` must be an Integer
  returns an ok response with the given id
  if id is not nil"
  (let [event (m/get-id id)]
    (if-not (nil? event)
      (create-response ok event)
      (no-content))))

(defn delete-id [id]
  "`id` must be an Integer
  returns a success(204) response if event is deleted"
  (let [deleted? (m/delete id)]
    (when (= 1 deleted?)
      (no-content))))
