(ns sempro.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [config.core :refer [env]]
    [conman.core :as conman]
    [mount.core :refer [defstate]]
    ))

(def ^:dynamic conn)
(def ^:dynamic conn-test)
(def test-jdbc "jdbc:sqlite:sempro_test.db")

(def pool-spec
  {:adapter    :sqlite
   :init-size  1
   :min-idle   1
   :max-idle   4
   :max-active 32
   :jdbc-url   (env :database-url)})

(def pool-test
  (assoc pool-spec :jdbc-url test-jdbc))

(defn connect! [env?]
  (let [conn (atom nil)]
    (if (= env? "test")
      (conman/connect! conn pool-test)
      (conman/connect! conn pool-spec))
  conn))

(defn disconnect! [conn]
  (conman/disconnect! conn))

(defn bind-queries [conn]
  (conman/bind-connection conn "sql/queries.sql"))

(defstate conn
          :start (connect! "dev")
          :stop (disconnect! conn))

(defstate conn-test
          :start (connect! "test")
          :stop (disconnect! conn-test))

