(ns sempro.test.db.event
  (:require
    [sempro.test.db.resources.users  :refer [user-rand]]
    [sempro.test.db.resources.events :as res]
    [clojure.java.jdbc   :as jdbc]
    [sempro.utils.test   :as u]
    [sempro.db.core      :as db]
    [midje.sweet :refer  :all]))

(conman.core/with-transaction [t-conn db/conn]
  (jdbc/db-set-rollback-only! t-conn)
  (fact "..:: EVENTS ::.."

    (fact "using the handlers"
      (let [user (second (sempro.models.user/create user-rand))
            email (:email user)
            login-data {:email email :pass "elmindreda"}
            token (u/get-token login-data)
            get-req  (u/create-request token :get)
            post-req (u/create-request token :post)
            permissions {:can_read email :can_write email}]

        (fact "DB is empty"
          (get-req "/events") => (u/not-found "no events found")
          (get-req "/events/1") => (u/access-denied "/events/1")
          (post-req "/events/1/delete" nil) => (u/access-denied "/events/1/delete"))

        (fact "DB not empty"
          (let [ankneipe (merge {:id 1} res/ankneipe {:info ""} permissions)
                abkneipe (merge {:id 2} res/abkneipe {:info ""} permissions)]
            (fact "create events"
              (post-req "/events" res/ankneipe) => (u/ok-response (merge res/ankneipe permissions))
              (post-req "/events" res/abkneipe) => (u/ok-response (merge res/abkneipe permissions)))
            (fact "get events"
              (get-req "/events/1") => (u/ok-response ankneipe)
              (get-req "/events/2") => (u/ok-response abkneipe)
              (get-req "/events") => (u/ok-response [ankneipe abkneipe]))
            (fact "delete events"
              (post-req "/events/1/delete" nil) => (u/ok-response {:deleted true})
              (post-req "/events/2/delete" nil) => (u/ok-response {:deleted true})
              (post-req "/events/3/delete" nil) => (u/access-denied "/events/3/delete"))
            (get-req "/events") => (u/not-found "no events found")))

        (fact "input-validation"
          (post-req "/events" res/kaput-date) => (u/input-error {:date '("date must be a valid date")})
          (post-req "/events" res/kaput-name) => (u/input-error {:name '("name must be present")})
          (post-req "/events" res/kaput-description) => (u/input-error {:description '("description must be present")}))))))