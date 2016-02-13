(ns sempro.handler
  (:require
    [compojure.core :refer [defroutes routes wrap-routes]]
    [compojure.route :as route]

    [sempro.routes.home :refer [home-routes user-routes restricted event-routes]]
    [sempro.middleware :as middleware]
    [sempro.config :refer [defaults]]
    [sempro.db.core :as db]

    [ring.util.http-response :as response]
    [clojure.tools.logging :as log]
    [luminus.logger :as logger]
    [config.core :refer [env]]
    [mount.core :as mount]
))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (logger/init env)
  (doseq [component (:started (mount/start))]
    (log/info component "started"))
  ((:init defaults)))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (log/info "sempro is shutting down...")
  (doseq [component (:stopped (mount/stop))]
    (mount.core/stop component)
    (log/info component "stopped"))
  (log/info "shutdown complete!"))

(def app-routes
  (routes
    home-routes
    user-routes
    event-routes
    (wrap-routes restricted middleware/wrap-restricted)
    (route/not-found
      (response/bad-request "not found"))))

(def app (middleware/wrap-base #'app-routes))
