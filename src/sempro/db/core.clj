(ns sempro.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [config.core :refer [env]]
    [conman.core :as conman]
    [mount.core :refer [defstate]]
))

(def pool-spec
  {:adapter   :sqlite
   :init-size 1
   :min-idle  1
   :max-idle  4
   :max-active 32
   :jdbc-url (env :database-url)})

(defonce ^:dynamic conn (atom nil))

(defn connect! []
  (conman/connect! conn
                   pool-spec))

(defn disconnect! [conn]
  (conman/disconnect! conn))

(defstate conn
          :start (connect!)
          :stop (disconnect! conn))

(conman/bind-connection conn "sql/queries.sql")
