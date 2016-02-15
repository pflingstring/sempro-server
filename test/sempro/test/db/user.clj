(ns sempro.test.db.user
  (:require
    [sempro.models.user      :refer [create password-matches? hash-pass]]
    [ring.util.http-response :refer [ok bad-request]]
    [sempro.test.db.resources.users :as res]
    [sempro.auth    :as auth]
    [sempro.db.core :as db]
    [clojure.java.jdbc  :as jdbc]
    [sempro.utils.test  :as u]
    [midje.sweet :refer :all]))

;;
;; REQUESTS
;;
(defn new-user-req [body]
  (-> (u/post-req "/user/create" body)
      (u/dissoc-headers)))

(defn login-req [body]
  (-> (u/post-req "/login" body)
      (u/dissoc-headers)))

;;
;; TESTS
;;
(conman.core/with-transaction [t-conn db/conn]
  (jdbc/db-set-rollback-only! t-conn)
  (fact "..:: USER ::.."

    (facts "using models.user"
      (fact "successfully create a user"
        (let [rand res/user-rand
              user (do (create rand)
                       (-> (db/get-user-by-email {:email (:email rand)})
                           (first) (u/ignore-key :id)))]
          user) => contains res/user-rand)

      (facts "password hashing and token generating"
        (let [plain-pass "s3cr3t_P@s$w0rd"
              user-email (-> (create res/hashed-password-user) (second) (:email))]
          (fact "should pass the test"
            (hash-pass plain-pass) => "pbkdf2+sha256$73616c61746963$100000$1bcaca26f7b0eca0216174b1859f19fc6ef2a5a1a34beb301e0f793103e65520"
            (password-matches? user-email "HASHmyPASSword") => true
            (password-matches? user-email plain-pass)       => false
            (hash-pass plain-pass) =not=> "IMAHackerLOL_89")
          (fact "create auth-token and verify it"
            (let [id {:id 123}] id => (-> id auth/sign-token auth/unsign-token))))))


    (fact "using handlers.user"
      (facts "input-validation"
        (fact "email-error"    (new-user-req res/kaput-email) => (u/input-error {:email '("email must be a valid email address")}))
        (fact "password-error" (new-user-req res/kaput-pass)  => (u/input-error {:pass  '("pass is less than the minimum")}))))


    (facts "authorise"
      (let [user (second (create res/user-harry))]
        (fact "accept password" (password-matches? (:email user) "expeliarmus") => true)
        (fact "reject password" (password-matches? (:email user) "ronWeaserby") => false)

        (fact "authorise user"
          (let [user-info   {:email (:email user) :pass "expeliarmus"}
                wrong-email (assoc user-info :email "you@know.who")
                wrong-pass  (assoc user-info :pass  "Vingardium LeviosAAA")]
            (fact "return login error"
              (login-req wrong-email) => (u/error "login error")
              (login-req wrong-pass)  => (u/error "login error"))
            (fact "successfully authenticate the user"
                (auth/unsign-token (u/get-token user-info)) => (assoc {} :email (:email user)))))))
    ))
