(ns sempro.handlers.event
  (:require
    [ring.util.http-response :refer [ok bad-request]]
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
        (create-response ok body)
        (create-response bad-request body)))
    (catch Exception e
      (create-response bad-request (err/sql-exception (.getMessage e))))))

(defn get-all []
  "returns an ok response with all events
  if there are any"
  (let [events (m/get-all)]
    (if-not (empty? events)
      (create-response ok events)
      (create-response bad-request (err/not-found "no events found")))))

(defn get-id [id]
  "`id` must be an Integer
  returns an ok response with the given id
  if id is not nil"
  (let [event (m/get-id id)]
    (if-not (nil? event)
      (create-response ok event)
      (create-response bad-request (err/not-found "id not found")))))

(defn delete-id [id]
  "`id` must be an Integer
  returns an ok response if event is deleted"
  (let [deleted? (m/delete id)]
    (if (= 1 deleted?)
      (create-response ok {:deleted true})
      (create-response bad-request (err/not-found "id not found")))))

(defn update-event [id req]
  "`id` must be an Integer
  `req` must be a full event map
    i.e. must contains all fields"
  (let [parsed (m/update-event id req)
        valid? (first parsed)
        body  (second parsed)]
    (if valid?
      (create-response ok body)
      (create-response bad-request body))))
