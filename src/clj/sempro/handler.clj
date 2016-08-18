(ns sempro.handler
  (:require
    [compojure.core :refer [routes wrap-routes]]
    [compojure.route :as route]

    [sempro.routes.home :refer :all]
    [sempro.middleware :as middleware]
    [sempro.env :refer [defaults]]

    [ring.util.http-response :as response]
    [mount.core :as mount]
    ))


(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    home-routes
    user-routes
    event-routes
    (route/not-found
      (response/bad-request "not found"))))

(def app (middleware/wrap-base #'app-routes))
