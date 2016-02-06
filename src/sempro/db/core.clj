(ns sempro.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [config.core :refer [env]]
    [conman.core :as conman]
    [mount.core :refer [defstate]]
))

(def test-db-url "jdbc:sqlite:sempro_test.db")
(def pool-spec
  {:adapter    :sqlite
   :init-size  1
   :min-idle   1
   :max-idle   4
   :max-active 32
   :jdbc-url   (env :database-url)})

;;
;; State Management
;;
(defonce ^:dynamic conn-dev  (atom nil))
(defonce ^:dynamic conn-test (atom nil))

(defn connect! [conn pool]
  (conman/connect! conn pool))

(defn disconnect! [conn]
  (conman/disconnect! conn))

(defn run-db [conn pool]
  (defstate conn
            :start (connect! conn pool)
            :stop (disconnect! conn)))

(defn create-queries [conn]
  (conman/bind-connection conn "sql/queries.sql"))

;;
;; Utils
;;
(def start-test-db
  (do (create-queries conn-test)
      (run-db conn-test
              (assoc pool-spec
                     :jdbc-url test-db-url))))