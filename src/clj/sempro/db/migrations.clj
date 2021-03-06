(ns sempro.db.migrations
  (:require
    [migratus.core :as migratus]
    [config.core :refer [env]]
    [to-jdbc-uri.core :refer [to-jdbc-uri]]
))

(defn parse-ids [args]
  (map #(Long/parseLong %) (rest args)))

(defn migrate [args & id]
  (let [config {:store :database
                :db    {:connection-uri (:database-url env)}}]
    (case args
      "migrate"
      (if id
        (apply migratus/up config (parse-ids id))
        (migratus/migrate config))
      "rollback"
      (if id
        (apply migratus/down config (parse-ids id))
        (migratus/rollback config)))))
