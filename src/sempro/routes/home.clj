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
  (POST "/login"       req (user/login  (:params req))))

(defroutes restricted
  (GET "/restricted" [] (user/restricted)))

(defroutes event-routes
  (GET "/events"     []   (event/get-all))
  (GET "/events/:id" [id] (event/get-id id))

  (POST "/events" req (event/create (:params req)))
  (POST "/events/:id/delete" [id] (event/delete-id id)))

(def event-updates
  (context "/events/:id/update" [id]
    (POST "/name" [name] (event/update-name id name))))


