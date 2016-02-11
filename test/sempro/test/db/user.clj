(ns sempro.test.db.user
  (:require
    [sempro.models.user :refer [create-user password-matches? hash-pass]]
    [sempro.test.db.resources.user :as res]
    [sempro.db.core :as db]
    [sempro.utils.test :as u]
    [midje.sweet :refer :all]
    [clojure.java.jdbc :as jdbc]
    [ring.util.http-response :refer [ok bad-request]]
    [sempro.auth :as auth]
    [cheshire.core :as json]
    [sempro.utils.error :as err]))
;;
;; REQUESTS
;;
(defn new-user-req [body]
  (-> (u/post-req "/user/create" body)
      (u/wrap-middlewares)
      (u/ignore-headers)))

(defn login-req [body]
  (-> (u/post-req "/login" body)
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

      (fact "validation-input"
        (facts "should return bad request and a validation-error message"
          (fact "email-error"
            (new-user-req res/kaput-email) => (contains ((u/error-response err/input-error) {:email '("email must be a valid email address")})))
          (fact "password-error"
            (new-user-req res/kaput-pass)  => (contains ((u/error-response err/input-error) {:pass  '("pass is less than the minimum")}))))))

    (facts "password hashing and token generating"
      (let [plain-pass "s3cr3t_P@s$w0rd"
            user-email (-> (create-user res/hashed-password-user)
                           (second)
                           (:email))]
        (fact "should pass the test"
          (hash-pass plain-pass) => "pbkdf2+sha256$73616c61746963$100000$1bcaca26f7b0eca0216174b1859f19fc6ef2a5a1a34beb301e0f793103e65520"
          (password-matches? user-email "HASHmyPASSword") => true
          (password-matches? user-email plain-pass) => false
          (hash-pass plain-pass) =not=> "IMAHackerLOL_89")
        (fact "create auth-token and verify it"
          (let [id {:id 123}]
            id => (-> id auth/sign-token auth/unsign-token)))))

    (facts "authorise"
      (let [user (second (create-user res/user-harry))]
        (fact "accept the correct password"
          (password-matches? (:email user) "expeliarmus") => true)
        (fact "reject incorrect password"
          (password-matches? (:email user) "ronWeaserby") => false)

        (fact "attempt to authorise a user"
          (let [user-info   {:email (:email user) :pass "expeliarmus"}
                wrong-email (assoc user-info :email "you@know.who")
                wrong-pass  (assoc user-info :pass  "Vingardium LeviosAAA")]
            (fact "should return login error"
              (login-req wrong-email) => (contains ((u/error-response err/error-body) "login error"))
              (login-req wrong-pass)  => (contains ((u/error-response err/error-body) "login error")))
            (fact "should successfully authenticate the user"
              (let [token   (json/parse-string (:body (login-req user-info)))
                    pattern (re-pattern "^Token (.+)$")]
                (-> (re-find pattern token)
                    (second)
                    (auth/unsign-token))) => (assoc {} :email (:email user)))
            ))
        ))
    ))
