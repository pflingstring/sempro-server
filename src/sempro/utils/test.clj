(ns sempro.utils.test
  (:require
    [ring.mock.request :as m]
    [ring.util.http-response :refer [bad-request]]
    [cheshire.core :as json]
    [sempro.utils.error :as err]))

;; requests util
(defn post-req [url body]
  (m/request :post url body))

(def wrap-middlewares #(sempro.handler/app %))

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

