(ns sempro.test.db.core
  (:require
    [sempro.db.core :as db]
    [clojure.test :refer :all]
))

(defn setup-db [f]
  (db/connect!)
  (f)
  (db/disconnect! db/conn))

(use-fixtures :each
  setup-db)

(deftest test-users
  (is (= [{:id         "1"
           :first_name "Sam"
           :last_name  "Smith"
           :email      "sam.smith@example.com"
           :pass       "pass"
           :admin      nil
           :last_login nil
           :is_active  nil}]
         (db/get-user {:id "1"}))))
