(ns sempro.routes.home
  (:require
    [sempro.utils.response :refer [create-response]]
    [sempro.handlers.user  :refer [create-user]]

    [ring.util.http-response :refer [ok bad-request!]]
    [compojure.core :refer [defroutes GET POST]]
    ))

(defroutes home-routes
  (GET "/" [] (create-response ok {:body "home"}))
  (GET "/about" [] (create-response ok {:message "about"})))

(defroutes user-routes
  (POST "/user/create" req (create-user (:params req))))
