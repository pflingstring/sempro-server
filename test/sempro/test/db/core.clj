(ns sempro.test.db.core
  (:require
    [sempro.db.core :as db]
    [clojure.test :refer :all]
))

(defn setup-db [f]
  db/start-test-db
  (f)
  (db/disconnect! db/conn-test))

(use-fixtures :each
  setup-db)

(deftest test-users
  (is (= [{:id         1
           :first_name "Sam"
           :last_name  "Smith"
           :email      "sam.smith@example.com"
           :phone      nil
           :role       nil
           :pass       "pass"}]
         (db/get-user {:id "1"}
                      db/test-db-url))))