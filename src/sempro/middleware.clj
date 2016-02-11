(ns sempro.middleware
  (:require
    [clojure.tools.logging :as log]
    [sempro.config :refer [defaults]]
    [config.core :refer [env]]

    [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
    [ring.middleware.format :refer [wrap-restful-format]]
    [ring.util.http-response :as response]

    [sempro.auth :refer [auth-backend]]
    [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t)
        (response/bad-request "Internal Error")))))

(defn wrap-formats [handler]
  (let [wrapped (wrap-restful-format
                  handler
                  {:formats [:json-kw :transit-json :transit-msgpack]})]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      (wrap-authentication auth-backend)
      (wrap-authorization auth-backend)
      wrap-formats
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false) ))
      wrap-internal-error))
