(ns sempro.handlers.user
  (:require
    [buddy.auth :refer [authenticated? throw-unauthorized]]
    [sempro.utils.response   :refer [create-response]]
    [ring.util.http-response :refer [ok bad-request created]]
    [sempro.utils.error :as err]
    [sempro.models.user :as m]
    [sempro.auth    :as auth]
    [sempro.db.core :as db])
  (:import (java.sql SQLException)))

(defn create [req]
  "`req` must be a map with an user
  tries to validate the req and returns:
   > ok  response if user is valid
   > bad response if not, with error msg in body
  if a exception is thrown also returns bad response"
  (try
    (let [parsed (m/create req)
          valid? (first parsed)
          body (second parsed)]
      (if valid?
        (create-response created body)
        (create-response bad-request body)))
    (catch SQLException e
      (create-response bad-request (err/sql-exception (.getMessage e))))
    (catch Exception e
      (create-response bad-request (err/sql-exception (.getMessage e))))))

(defn login [request]
  "`request` must be a map with :email and :pass keys
  checks if the credentials are valid and returns:
   > ok  response with the auth-token body
   > bad response if not"
  (let [pass  (:pass request)
        email (assoc {} :email (:email request))                            ; TODO: validate email
        user  (first (db/get-user-by-email email))
        token (auth/sign-token email)]
    (if (m/password-matches? (:email user) pass)
      (create-response ok (str "Token " token))
      (create-response bad-request {:error "login error"}))))

(defn home [request]
  "`request` must be a ring request
  checks if the request is authenticated and returns:
   > ok  response with users email
   > throws an exception otherwise"
  (if (authenticated? request)
    (create-response ok (:identity request))
    (throw-unauthorized "Must be authorized")))

(defn restricted []
  "`request` must be a ring request
  only returns ok if the request is authenticated
  authentication is checked by access-list"
  (create-response ok {:message "RESTRICTED"}))
