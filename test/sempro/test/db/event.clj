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
            login-data {:email (:email user) :pass "elmindreda"}
            token (u/get-token login-data)
            get-req  #(u/dissoc-headers (u/authenticate-req (u/get-req %) token))
            post-req #(u/dissoc-headers (u/authenticate-req (u/post-req %1 %2) token))]

        (fact "DB is empty"
          (get-req "/events")   => (u/not-found "no events found")
          (get-req "/events/1") => (u/not-found "id not found")
          (post-req "/events/1/delete" nil) => (u/not-found "id not found"))

        (fact "DB not empty"
          (let [ankneipe (merge {:id 1} res/ankneipe {:info ""})
                abkneipe (merge {:id 2} res/abkneipe {:info ""})]
            (fact "create events"
              (post-req "/events" res/ankneipe) => (u/ok-response res/ankneipe)
              (post-req "/events" res/abkneipe) => (u/ok-response res/abkneipe))
            (fact "get events"
              (get-req "/events/1") => (u/ok-response ankneipe)
              (get-req "/events/2") => (u/ok-response abkneipe)
              (get-req "/events") => (u/ok-response [ankneipe abkneipe]))
            (fact "delete events"
              (post-req "/events/1/delete" nil) => (u/ok-response {:deleted true})
              (post-req "/events/2/delete" nil) => (u/ok-response {:deleted true})
              (post-req "/events/3/delete" nil) => (u/not-found "id not found"))
            (get-req "/events") => (u/not-found "no events found")))

        (fact "input-validation"
          (post-req "/events" res/kaput-date) => (u/input-error {:date '("date must be a valid date")})
          (post-req "/events" res/kaput-name) => (u/input-error {:name '("name must be present")})
          (post-req "/events" res/kaput-description) => (u/input-error {:description '("description must be present")}))
        ))
    ))