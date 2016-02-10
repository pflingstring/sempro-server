(ns sempro.test.db.user
  (:require
    [sempro.models.user :refer [create-user]]
    [sempro.test.db.resources.user :as res]
    [sempro.db.core :as db]
    [sempro.utils.test :as u]
    [midje.sweet :refer :all]
    [clojure.java.jdbc :as jdbc]
    [ring.util.http-response :refer [ok bad-request]]))
;;
;; REQUESTS
;;
(defn new-user-req [body]
  (-> (u/post-req "/user/create" body)
      (u/wrap-middlewares)
      (u/ignore-headers)))

;;
;; TESTS
;;
(conman.core/with-transaction [t-conn db/conn]
  (jdbc/db-set-rollback-only! t-conn)

  (fact "..:: USER  TESTS ::.."

    (facts "attempt to create a new user using models.user"
      (fact "should successfully create a user"
        (let [rand res/user-rand
              user (do (create-user rand)
                       (-> (db/get-user-by-email {:email (:email rand)})
                           (first) (u/ignore-key :id)))]
          user) => contains res/user-rand)

      (facts "should return bad request and an validation-error message"
        (fact "email-error"
          (new-user-req res/kaput-email) => (contains (u/error-response {:email '("email must be a valid email address")})))

        (fact "password-error"
          (new-user-req res/kaput-pass) => (contains (u/error-response {:pass '("pass is less than the minimum")})))
          ))
    ))
