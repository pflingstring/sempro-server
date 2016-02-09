(ns sempro.utils.test
  (:require
    [ring.mock.request :as m]
    [ring.util.http-response :refer [bad-request]]
    [cheshire.core :as json]))

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
(def error-body  #(assoc {} :error %))
(def input-error #(-> (assoc {} :input-validation %) (error-body)))
(def dissoc-headers #(dissoc % :headers))

(def error-response #(-> (input-error %)
                         (json/generate-string)
                         (bad-request)
                         (dissoc-headers)))

