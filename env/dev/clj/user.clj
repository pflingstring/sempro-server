(ns user
  (:require [mount.core :as mount]
            sempro.core))

(defn start []
  (mount/start-without #'sempro.core/repl-server))

(defn stop []
  (mount/stop-except #'sempro.core/repl-server))

(defn restart []
  (stop)
  (start))

