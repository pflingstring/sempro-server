(ns sempro.utils.test
  (:require
    [ring.mock.request  :as m]
    [sempro.utils.error :as err]
    [cheshire.core      :as json]
    [ring.util.http-response :refer [bad-request forbidden ok header]]
    [sempro.utils.response   :refer [create-response]]
    ))


;; requests util
(def wrap-middlewares #(sempro.handler/app %))

(defn authenticate-req [request token]
  (-> (header request "Authorization" (str "Token " token))
      (wrap-middlewares)))

(defn post-req [url body]
  (m/request :post url body))

(defn get-req [url]
  (m/request :get url))

(defn ignore-key [data key]
  (assoc data key 'IGNORE))

;; response util
(def dissoc-headers #(dissoc % :headers))

(defn error-response [error-type response-code]
  #(-> (error-type %)
       (json/generate-string)
       (response-code)
       (dissoc-headers)))

(def error (error-response err/error-body bad-request))
(def not-found (error-response err/not-found bad-request))
(def sql-error (error-response err/sql-exception bad-request))
(def input-error (error-response err/input-error bad-request))
(def access-denied (error-response err/access-denied forbidden))

(defn ok-response [body]
  (dissoc-headers (create-response ok body)))

;; user util
(defn get-token [user]
  (let [login-req (wrap-middlewares (post-req "/login" user))
        token   (json/parse-string (:body login-req))
        pattern (re-pattern "^Token (.+)$")]
    (-> (re-find pattern token)
        (second))))
