(ns sempro.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [config.core :refer [env]]
))

(def conn
  {:classname      "org.sqlite.JDBC"
   :connection-uri (:database-url env)
   :naming         {:keys   clojure.string/lower-case
                    :fields clojure.string/upper-case}})

(defqueries "sql/queries.sql" {:connection conn})
