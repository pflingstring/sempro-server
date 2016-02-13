(ns sempro.handlers.event
  (:require
    [ring.util.http-response :refer [ok bad-request]]
    [sempro.utils.response :refer [create-response]]
    [sempro.models.event :as m]))
(defn create [req]
  (let [parsed-req (m/create req)
        valid-event? (first parsed-req)
        body (second parsed-req)]
    (if valid-event?
      (create-response ok body)
      (create-response bad-request body))))

(defn get-all []
  (let [events (m/get-all)]
    (create-response ok events)))

(defn get-id [id]
  (let [event (m/get-id id)]
    (create-response ok event)))

(defn delete-id [id]
  (m/delete id))
