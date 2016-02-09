(ns sempro.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [config.core :refer [env]]
    [conman.core :as conman]
    [mount.core :refer [defstate]]
    ))

(def ^:dynamic conn)

(def pool-spec
  {:adapter    :sqlite
   :init-size  1
   :min-idle   1
   :max-idle   4
   :max-active 32
   :jdbc-url   (env :database-url)})

(defn connect! []
  (let [conn (atom nil)]
    (conman/connect! conn pool-spec)
    conn))

(defn disconnect! [conn]
  (conman/disconnect! conn))

(conman/bind-connection conn "sql/queries.sql")

(defstate conn
          :start (connect!)
          :stop  (disconnect! conn))
