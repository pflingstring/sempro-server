(ns sempro.routes.home
  (:require
    [sempro.utils.response :refer [create-response]]
    [sempro.handlers.user  :as user]
    [sempro.handlers.event :as event]

    [ring.util.http-response :refer [ok bad-request!]]
    [compojure.core :refer [defroutes context GET POST]]

    [buddy.auth.accessrules :refer [restrict]]
    ))

(defroutes home-routes
  (GET "/"      [] user/home)
  (GET "/about" [] (create-response ok {:message "about"})))

(defroutes user-routes
  (POST "/user/create" req (user/create (:params req)))
  (POST "/login"       req (user/login  (:params req)))
  (POST "/user/:id/update/status" [id status] (user/update-status id status))
  (POST "/user/:id/update/job"    [id job]    (user/update-job    id job)))

(defroutes event-routes
  (GET "/events"     req (event/get-all (get-in req [:identity :email])))
  (GET "/events/:id" req (event/get-id  (get-in req [:params :id])))

  (POST "/events" req (event/create (:params req) (get-in req [:identity :email])))

  (POST "/events/:id/delete" [id]       (event/delete-id    id))
  (POST "/events/:id/update" [id & req] (event/update-event id req))
  )

