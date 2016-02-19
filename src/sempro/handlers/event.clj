(ns sempro.handlers.event
  (:require
    [ring.util.http-response :refer [ok bad-request]]
    [sempro.utils.response   :refer [create-response]]
    [sempro.models.event :as m]
    [sempro.utils.error  :as err]
    [buddy.auth :refer [authenticated?]]))

(defn create [req user]
  "`req` must be a map with an event
  tries to validate the req and returns:
   > ok  response if event is valid
   > bad response if not, with error msg in body
  if a exception is thrown also returns bad response"
  (try
    (let [permissions {:can_read user :can_write user}
          parsed (m/create (merge req permissions))
          valid? (first parsed)
          body (second parsed)]
      (if valid?
        (create-response ok body)
        (create-response bad-request body)))
    (catch Exception e
      (create-response bad-request (err/sql-exception (.getMessage e))))))

(defn get-all [user]
  "returns an ok response with all events
  if there are any"
  (let [events (filter #(= (:can_read %) user) (m/get-all))]
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

;;
;; Access rules
;;
(defn can?
  [action event-id user]
  (let [permissions (m/get-permissions event-id)
        can-read?  (boolean (some #(= % user) (clojure.string/split (:can_read permissions)  #" ")))
        can-write? (boolean (some #(= % user) (clojure.string/split (:can_write permissions) #" ")))]
    (cond
      (= action "read")  can-read?
      (= action "write") can-write?)))

(defn check-permissions
  [action req]
  (let [id   (get-in req [:match-params :id])
        user (get-in req [:identity :email])]
    (if (= action "read")
      (can? "read"  id user)
      (can? "write" id user))))

(def can-read?  #(check-permissions "read"  %))
(def can-write? #(check-permissions "write" %))

(def access-rules
  [{:uri    "/events/:id"
    :handler {:and [authenticated? can-read?]}}

   {:uri "/events/:id/*"
    :handler {:and [authenticated? can-write?]}}
   ])
