(ns sempro.routes.home
  (:require
    [sempro.utils.response :refer [create-response]]
    [sempro.handlers.user  :as user]
    [sempro.handlers.event :as event]

    [ring.util.http-response :refer [ok bad-request!]]
    [compojure.core :refer [defroutes context GET POST]]
    ))

(defroutes home-routes
  (GET "/"      [] user/home)
  (GET "/about" [] (create-response ok {:message "about"})))

(defroutes user-routes
  (POST "/user/create" req (user/create (:params req)))
  (POST "/login"       req (user/login  (:params req)))
  (POST "/user/:id/update/status" [id status] (user/update-status id status))
  (POST "/user/:id/update/job"    [id job]    (user/update-job    id job)))

(defroutes restricted
  (GET "/restricted/:id" req (event/restricted req)))

(defroutes event-routes
  (GET "/events"     []   (event/get-all))
  (GET "/events/:id" [id] (event/get-id id))

  (POST "/events"            req        (event/create (:params req)))
  (POST "/events/:id/delete" [id]       (event/delete-id    id))
  (POST "/events/:id/update" [id & req] (event/update-event id req)))

