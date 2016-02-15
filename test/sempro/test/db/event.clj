(ns sempro.test.db.event
  (:require
    [sempro.test.db.resources.users  :refer [user-rand]]
    [sempro.test.db.resources.events :as res]
    [clojure.java.jdbc   :as jdbc]
    [sempro.models.event :as m]
    [sempro.utils.test   :as u]
    [sempro.db.core      :as db]
    [midje.sweet :refer :all]))

(conman.core/with-transaction [t-conn db/conn]
  (jdbc/db-set-rollback-only! t-conn)

  (fact "..:: EVENTS ::.."


    (fact "using the models.event"
      (fact "create an event" (first (m/create res/ankneipe)) => true)
      (fact "return ankneipe" (-> (m/get-id 1) (u/ignore-key :id)) => contains res/ankneipe)
      (fact "return 2 ankneipe events" (m/create res/ankneipe)
        (m/get-all) => (conj '() (assoc res/ankneipe :id 2) (assoc res/ankneipe :id 1))))


    (fact "using the handlers"
      (let [user (second (sempro.models.user/create user-rand))
            login-data {:email (:email user) :pass "elmindreda"}
            token (u/get-token login-data)
            get-req #(u/ignore-headers  (u/authenticate-req (u/get-req  %) token))
            post-req #(u/ignore-headers (u/authenticate-req (u/post-req %1 %2) token))]

        (fact "return all events" (get-req "/events") => contains (u/ok-response res/all-events))
        (fact "return abkneipe" (get-req "/events/2" res/abkneipe) => contains (u/ok-response res/abkneipe))
        (fact "create ankneipe" (post-req "/events"  res/ankneipe) => contains (u/ok-response res/ankneipe))))
    ))
