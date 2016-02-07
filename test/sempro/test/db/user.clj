(ns sempro.test.db.user
  (:require
    [sempro.test.db.resources.user :as res]
    [sempro.db.core :as db]

    [midje.sweet :refer :all]
    ))

(facts "User Test"
  (first (db/get-user {:id 5} db/test-jdbc)) => res/user-rand)

