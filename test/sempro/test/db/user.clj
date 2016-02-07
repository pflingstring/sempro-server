(ns sempro.test.db.user
  (:require
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
(def new-user-req (u/post-req "/user/create" res/user-harry))


;;
;; TESTS
;;

(fact "..:: USER  TESTS ::.."
  (facts "USER ROUTES"
    (u/ignore-headers new-user-req) => (contains {:status 200 :body (json/generate-string res/user-harry)}))
  )
