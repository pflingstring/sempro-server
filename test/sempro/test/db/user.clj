(ns sempro.test.db.user
  (:require
    [sempro.models.user :refer [create-user]]
    [sempro.test.db.resources.user :as res]
    [sempro.db.core    :as db]
    [sempro.utils.test :as u]
    [cheshire.core     :as json]
    [midje.sweet :refer :all]
    [clojure.java.jdbc :as jdbc]))

;;
;; TESTS
;;
(conman.core/with-transaction [t-conn db/conn]
  (jdbc/db-set-rollback-only! t-conn)

  (fact "..:: USER  TESTS ::.."

    (facts "create new user using models.user"
      (fact "should successfully create a user"
        (let [rand res/user-rand
              user (do (create-user rand)
                       (-> (db/get-user-by-email {:email (:email rand)})
                           (first) (u/ignore-key :id)))]
          user) => contains res/user-rand))
    ))
