(ns sempro.utils.test
  (:require
    [ring.mock.request  :as m]
    [sempro.utils.error :as err]
    [cheshire.core      :as json]
    [ring.util.http-response :refer [bad-request ok header]]
    [sempro.utils.response   :refer [create-response]]
    ))


;; requests util
(def wrap-middlewares #(sempro.handler/app %))

(defn authenticate-req [request token]
  (header request "Authorization" token))

(defn post-req [url body]
  (-> (m/request :post url body)
      (wrap-middlewares)))

(defn get-req [url]
  (-> (m/request :get url)
      (wrap-middlewares)))

(defn ignore-key [data key]
  (assoc data key 'IGNORE))


;; response util
(def dissoc-headers #(dissoc % :headers))

(defn error-response [error-type]
  #(-> (error-type %)
       (json/generate-string)
       (bad-request)
       (dissoc-headers)))

(def error (error-response err/error-body))
(def not-found (error-response err/not-found))
(def sql-error (error-response err/sql-exception))
(def input-error (error-response err/input-error))

(defn ok-response [body]
  (dissoc-headers (create-response ok body)))

;; user util
(defn get-token [user]
  (let [login-req (post-req "/login" user)
        token   (json/parse-string (:body login-req))
        pattern (re-pattern "^Token (.+)$")]
    (-> (re-find pattern token)
        (second))))
