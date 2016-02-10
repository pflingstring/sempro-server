(ns sempro.handlers.user
  (:require
    [ring.util.http-response :refer [ok bad-request]]
    [sempro.utils.response :refer [create-response]]
    [sempro.models.user :as user]
    [sempro.utils.error :as err])
  (:import (java.sql SQLException)))

(defn create-user [req]
  (try
    (let [parsed-req (user/create-user req)
          valid-user? (first parsed-req)
          body (second parsed-req)]
      (if valid-user?
        (create-response ok body)
        (create-response bad-request body)))
    (catch SQLException e
      (create-response bad-request
                       (err/sql-exception (.getMessage e))))
    (catch Exception e
      (create-response bad-request (err/sql-exception (.getMessage e))))))
