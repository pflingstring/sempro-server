(ns sempro.utils.test
  (:require
    [ring.mock.request :as m]
    [ring.util.http-response :refer [bad-request ok header]]
    [sempro.utils.response :refer [create-response]]
    [cheshire.core :as json]
    [sempro.utils.error :as err]
    [sempro.auth :as auth]))

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

(defn ignore-headers [request]
  "adds an 'IGNORE symbol for :headers"
  (ignore-key request :headers))


;; response util
(def dissoc-headers #(dissoc % :headers))

(defn error-response [error-type]
  #(-> (error-type %)
       (json/generate-string)
       (bad-request)
       (dissoc-headers)))

(defn ok-response [body]
  (create-response ok body))

;; user util
(defn get-token [user]
  (let [login-req (post-req "/login" user)
        token   (json/parse-string (:body login-req))
        pattern (re-pattern "^Token (.+)$")]
    (-> (re-find pattern token)
        (second))))
