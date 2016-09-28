(ns user
  (:require [mount.core :as mount]
            [sempro.figwheel :refer [start-fw stop-fw cljs]]
            sempro.core))

(defn start []
  (mount/start-without #'sempro.core/repl-server))

(defn stop []
  (mount/stop-except #'sempro.core/repl-server))

(defn restart []
  (stop)
  (start))

