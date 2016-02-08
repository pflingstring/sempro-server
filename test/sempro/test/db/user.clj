(ns sempro.test.db.user
  (:require
    [sempro.models.user :refer [create-user]]
    [sempro.test.db.resources.user :as res]
    [sempro.db.migrations :refer [migrate]]
    [sempro.db.core    :as db]
    [sempro.utils.test :as u]
    [cheshire.core     :as json]
    [midje.sweet :refer :all]
    ))

;;
;; DB STUFF
;;
(def drop-user-table
  (do (migrate ["rollback" "test"])
      (migrate ["migrate"  "test"])))

; execute query in test DB
(def run #(%1 %2 db/test-jdbc))


;;
;; REQUESTS
;;


;;
;; TESTS
;;

(fact "..:: USER  TESTS ::.."

  (facts "create new user"
    (with-state-changes [(before :facts  drop-user-table)]

      (fact "create user with SQL query generated in sempro.db.core"
        (let [harry res/user-harry
              user (do (run db/create-user! harry)
                       (first (run db/get-user-by-email {:email (:email harry)})))]
          user) => (-> res/user-harry (assoc :id 1)))

      (fact "create a user with sempro.models.user/create-user"
        (with-state-changes [(after :facts (db/delete-user-by-email! {:email (:email res/user-rand)}))]
          (fact "should create a user in DEV_DB"
           (let [rand res/user-rand
                 user (do (create-user rand)
                          (first (db/get-user-by-email {:email (:email rand)})))]
             user) => (-> res/user-rand (assoc :id 1))))

          (fact "should create a user in TEST_DB"
            (let [rand res/user-rand
                  user (do (create-user rand "test")
                           (first (run db/get-user-by-email {:email (:email rand)})))]
              user) => (-> res/user-rand (assoc :id 2))))
      ))

  )
