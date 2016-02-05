(ns sempro.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [sempro.routes.home :refer [home-routes]]
            [sempro.middleware :as middleware]
            [clojure.tools.logging :as log]
            [compojure.route :as route]
            [config.core :refer [env]]
            [sempro.config :refer [defaults]]
            [mount.core :as mount]
            [ring.util.http-response :as response]
            [luminus.logger :as logger]))

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
    (log/info component "stopped"))
  (log/info "shutdown complete!"))

(def app-routes
  (routes
    (wrap-routes #'home-routes middleware/wrap-csrf)
    (route/not-found
      (response/bad-request "not found")

      ;(:body
      ;  (error-page {:status 404
      ;               :title "page not found"}))
    )))

(def app (middleware/wrap-base #'app-routes))
