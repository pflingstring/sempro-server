(ns sempro.middleware
  (:require
    [ring.util.http-response :as response]
    [clojure.tools.logging   :as log]
    [sempro.config :refer [defaults]]
    [config.core   :refer [env]]
    [buddy.auth.middleware    :refer [wrap-authentication wrap-authorization]]
    [buddy.auth.accessrules   :refer [restrict wrap-access-rules]]
    [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
    [ring.middleware.format   :refer [wrap-restful-format]]
    [buddy.auth  :refer [authenticated?]]
    [sempro.auth :refer [auth-backend]]))

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

(defn on-error [request response]
  {:status 403
   :headers {"Content-Type" "text/plain"}
   :body (str "Acces to " (:uri request) " is not authorized")})

(def all-rules
  (concat
    sempro.handlers.event/access-rules
    sempro.handlers.user/access-rules
    ))

(def access-rules (reduce conj [] all-rules))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      (wrap-access-rules {:rules access-rules :on-error on-error})
      (wrap-authentication auth-backend)
      (wrap-authorization  auth-backend)
      wrap-formats
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false) ))
      wrap-internal-error))