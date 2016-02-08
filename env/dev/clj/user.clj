(ns user
  (:require
    [sempro.handler :refer [app init destroy]]
    [luminus.http-server :as http]
    [config.core :refer [env]]
    [sempro.db.migrations :as m]
))

(defn start []
  (http/start {:handler app
               :init    init
               :port    (:port env)}))

(defn stop []
  (http/stop destroy))

(defn restart []
  (stop)
  (start))

(defn clean-db [db]
  (m/migrate ["rollback" db])
  (m/migrate ["migrate" db]))
