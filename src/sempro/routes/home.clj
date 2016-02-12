(ns sempro.routes.home
  (:require
    [sempro.utils.response :refer [create-response]]
    [sempro.handlers.user  :as h]

    [ring.util.http-response :refer [ok bad-request!]]
    [compojure.core :refer [defroutes GET POST]]
    ))

(defroutes home-routes
  (GET "/" [] h/home)
  (GET "/about" [] (create-response ok {:message "about"})))

(defroutes user-routes
  (POST "/user/create" req (h/create-user (:params req)))
  (POST "/login"       req (h/login       (:params req))))

(defroutes restricted
  (GET "/restricted" req (h/restricted req)))
