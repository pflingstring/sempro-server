(ns sempro.test.db.event
  (:require
    [sempro.test.db.resources.events :as res]
    [sempro.db.core :as db]
    [sempro.models.event :as event]

    [clojure.java.jdbc :as jdbc]
    [midje.sweet :refer :all]))

(conman.core/with-transaction [t-conn db/conn]
  (jdbc/db-set-rollback-only! t-conn)

  (fact "..:: EVENTS ::.."
    (let [ankneipe (event/create res/an-kneipe)
          row-id   (first (keys ankneipe))]
      (fact "should create an ankneipe event"
        (row-id ankneipe) => 1)
      (fact "should return ankneipe"
        (-> (event/get-id 1)
            (assoc :id 'IGNORE))  => (contains res/an-kneipe))
      (fact "should return 2 ankneipe events"
        (event/create res/an-kneipe)
        (event/get-all) => (conj '()
                                 (assoc res/an-kneipe :id 2)
                                 (assoc res/an-kneipe :id 1))))
    ))

