(ns sempro.handlers.user
  (:require
    [ring.util.http-response :refer [ok bad-request]]
    [sempro.utils.response :refer [create-response]]
    [sempro.models.user :as user]
    [sempro.utils.error :as err]
    [sempro.db.core :as db]
    [sempro.auth :as auth]
    [buddy.auth :refer [authenticated? throw-unauthorized]])
  (:import (java.sql SQLException)))

(defn create [req]
  (try
    (let [parsed-req (user/create req)
          valid-user? (first parsed-req)
          body (second parsed-req)]
      (if valid-user?
        (create-response ok body)
        (create-response bad-request body)))
    (catch SQLException e
      (create-response bad-request (err/sql-exception (.getMessage e))))
    (catch Exception e
      (create-response bad-request (err/sql-exception (.getMessage e))))))

(defn login [request]
  (let [pass  (:pass request)
        email (assoc {} :email (:email request))                            ; TODO: validate email
        user  (first (db/get-user-by-email email))
        token (auth/sign-token email)]
    (println (get-in [:headers :authorisation] request))
    (if (user/password-matches? (:email user) pass)
      (create-response ok (str "Token " token))
      (create-response bad-request {:error "login error"}))))

(defn home [request]
  (if (authenticated? request)
    (create-response ok (:identity request))
    (throw-unauthorized "Must be authorized")))

(defn restricted [request]
  (create-response ok {:message "RESTRICTED"}))
