(ns sempro.db.core
  (:require
    [config.core :refer [env]]
    [conman.core :as conman]
    [mount.core :refer [defstate]]
    ))

(def ^:dynamic conn)

(def pool-spec
  {
   ;:adapter    :sqlite
   :init-size  1
   :min-idle   1
   :max-idle   4
   :max-active 32
   :jdbc-url   (env :database-url)})

(defn connect! []
  (let [conn (atom nil)]
    (conman/connect! pool-spec)
    conn))

(defn disconnect! [conn]
  (conman/disconnect! conn))

(conman/bind-connection conn
  "sql/users.sql"
  "sql/events.sql")

(defstate conn
          :start (connect!)
          :stop  (disconnect! conn))
