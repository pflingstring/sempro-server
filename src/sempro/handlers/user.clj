(ns sempro.handlers.user
  (:require
    [ring.util.http-response :refer [ok bad-request!]]
    [sempro.utils.response   :refer [create-response]]
    [sempro.models.user :as user]))

(defn create-user [req]
  (let [parsed-req (user/create-user req)
        valid-user? (first parsed-req)
        body (second parsed-req)]
    (if valid-user?
      (create-response ok body)
      (create-response bad-request! body))))
