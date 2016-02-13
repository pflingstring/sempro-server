(ns sempro.test.db.event
  (:require
    [sempro.test.db.resources.users  :refer [user-rand]]
    [sempro.test.db.resources.events :as res]
    [sempro.models.event   :as m]
    [sempro.handlers.event :as h]
    [sempro.utils.test :as u]
    [sempro.db.core   :as db]

    [clojure.java.jdbc :as jdbc]
    [midje.sweet :refer :all]))

(conman.core/with-transaction [t-conn db/conn]
  (jdbc/db-set-rollback-only! t-conn)

  (fact "..:: EVENTS ::.."
    (fact "using models.event"
      (fact "should create an event" (first (m/create res/ankneipe)) => true)
      (fact "should return ankneipe" (-> (m/get-id 1) (u/ignore-key :id)) => contains res/ankneipe)
      (fact "should return 2 ankneipe events" (m/create res/ankneipe)
        (m/get-all) => (conj '() (assoc res/ankneipe :id 2) (assoc res/ankneipe :id 1))))

    (let [user (second (sempro.models.user/create user-rand))
          login-data {:email (:email user) :pass "elmindreda"}
          token     (u/get-token login-data)
          get-req  #(u/authenticate-req (u/get-req %) token)
          post-req #(u/authenticate-req (u/post-req %1 %2) token)]

      (fact "using handlers/event"
        (fact "should create ankneipe"  (u/ignore-headers (post-req "/events" res/ankneipe))  => contains (u/ok-response res/ankneipe))
        (fact "should return abkneipe"  (u/ignore-headers (get-req "/events/2" res/abkneipe)) => contains (u/ok-response res/abkneipe))
        (fact "should return all events"(u/ignore-headers(get-req "/events")) => contains (u/ok-response res/all-events))))
    ))
