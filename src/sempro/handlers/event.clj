(ns sempro.handlers.event
  (:require
    [ring.util.http-response :refer [ok bad-request forbidden]]
    [sempro.utils.response   :refer [create-response]]
    [sempro.models.event :as m]
    [sempro.utils.error  :as err]
    [clojure.string      :as str]
    [sempro.models.user  :as user-m]
    [buddy.auth :refer [authenticated?]]))

(defn create
  "`req` must be a map with an event
  tries to validate the req and returns:
   > ok  response if event is valid
   > bad response if not, with error msg in body
  if a exception is thrown also returns bad response"
  [req user]
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

(defn get-all
  "if user is not logged in returns error
  returns an ok response with all events
  the user has read-permission, if there are any"
  [user]
  (if-not (nil? user)
    (let [events  (m/get-all)
          allowed (-> #(.contains (:can_read %) user)
                      (filter events))]
      (if-not (empty? allowed)
        (create-response ok allowed)
        (create-response bad-request (err/not-found "no events found"))))
    (create-response forbidden (err/access-denied "You must be logged in"))))

(defn get-id
  "`id` must be an Integer
  returns an ok response with the given id
  if id is not nil"
  [id]
  (let [event (m/get-id id)]
    (if-not (nil? event)
      (create-response ok event)
      (create-response bad-request (err/not-found "id not found")))))

(defn delete-id
  "`id` must be an Integer
  returns an ok response if event is deleted"
  [id]
  (let [deleted? (m/delete id)]
    (if (= 1 deleted?)
      (create-response ok {:deleted true})
      (create-response bad-request (err/not-found "id not found")))))

(defn update-event
  "`id` must be an Integer
  `req` must be a full event map
    i.e. must contains all fields"
  [id req]
  (let [parsed (m/update-event id req)
        valid? (first parsed)
        body  (second parsed)]
    (if valid?
      (create-response ok body)
      (create-response bad-request body))))

;; TODO: handle NPE when req does not contain :readers & :writers key or null
;;       add more info when successfully changed permission
(defn change-permissions
  "`fn` must be a function which adds/updates permissions
  `id`  must be an Integer, represents the event's ID
  `req` must be a map with permissions to add/update"
  [fn id req]
  (let [readers (when-not (empty? (:readers req)) (set (str/split (:readers req) #" ")))
        writers (when-not (empty? (:writers req)) (set (str/split (:writers req) #" ")))
        curr-perms (m/get-permissions id)
        old-read  (set (str/split (:can_read  curr-perms) #" "))
        old-write (set (str/split (:can_write curr-perms) #" "))
        new-read  (str/join " " (distinct (concat old-read  readers)))
        new-write (str/join " " (distinct (concat old-write writers)))
        added? (if (= fn m/add-permissions)
                 (m/update-permissions id new-read new-write)
                 (fn id (str/join " " readers) (str/join " " writers)))]
    (if added?
      (create-response ok {:added true})
      (create-response bad-request (err/error-body "no permission added"))
      )))

(def update-permissions #(change-permissions m/update-permissions %1 %2))
(def add-permissions    #(change-permissions m/add-permissions    %1 %2))

(defn add-group-permissions
  "adds permissions based on user-group
  `id` must be an Integer, represend event's ID
  `group` represents a user-group
  `req` is a map containing permissions to be added"
  [id group req]
  (let [users (cond (= group "fuxe")     user-m/get-fuxe
                    (= group "aktivitas" user-m/get-aktivitas)
                    (= group "everyone"  user-m/get-everyone))
        can-read?  (not (empty? (:readers req)))
        can-write? (not (empty? (:writers req)))
        emails (map :email (users))
        curr-perms (m/get-permissions id)
        old-read  (set (str/split (:can_read  curr-perms) #" "))
        old-write (set (str/split (:can_write curr-perms) #" "))
        new-read  (str/join " " (distinct (concat old-read  emails)))
        new-write (str/join " " (distinct (concat old-write emails)))]
    (cond
      (and can-read?
           can-write?)   (update-permissions id {:readers new-read :writers new-write})
      (true? can-read?)  (update-permissions id {:readers new-read :writers (str/join " " old-write)})
      (true? can-write?) (update-permissions id {:readers (str/join " " old-read) :writers new-write})
      :else (create-response bad-request (err/error-body "you must choose at least one")))))

;;
;; Access rules
;;
(defn can?
  "checks if the given `user` has permission to read/write an event
  `action` must be either 'read' or 'write'"
  [action event-id user]
  (let [permissions (m/get-permissions event-id)]
    (if-not (nil? permissions)
      (let [can-read?  (boolean (some #(= % user) (str/split (:can_read  permissions) #" ")))
            can-write? (boolean (some #(= % user) (str/split (:can_write permissions) #" ")))]
        (cond (= action "read")  can-read?
              (= action "write") can-write?))
      false)))

(defn check-permissions
  "`req` must be a ring-request"
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
