(ns sempro.test.db.user
  (:require
    [sempro.db.core :as db]
    [sempro.models.user :as user]
    [sempro.db.migrations :refer [migrate]]
    [sempro.test.db.resources.user :as res]

    [midje.sweet :refer :all]
    ))

;; execute query in test DB
(def run
  (fn [query param]
    (query param db/test-jdbc)))

(def drop-user-table (do (migrate ["rollback" "test"])
                         (migrate ["migrate"  "test"])))

(with-state-changes [(before :facts drop-user-table)]
  (fact "User Test"
    (fact "Add user to database"
      (user/create-user res/user-harry db/test-jdbc) => [true, res/user-harry]
      )))
