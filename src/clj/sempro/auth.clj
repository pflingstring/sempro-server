(ns sempro.auth
  (:require
    [buddy.sign.jws :as jws]
    [buddy.core.keys :as keys]
    [buddy.auth.backends.token :refer [jws-backend]]))

(def path (config.core/env :user-dir))
(def private-key (keys/private-key (str path "/ec-privatekey.pem")))
(def public-key  (keys/public-key  (str path "/ec-publickey.pem")))
(def sign-alg {:alg :es256})

(defn sign-token [user-id]
  "user-id must be a map
  signs the map and returns it as a string"
  (jws/sign user-id private-key sign-alg))

(defn unsign-token [token]
  "token must be a string
  unsigns the map and returns the original map"
  (jws/unsign token public-key sign-alg))

(def auth-backend (jws-backend {:secret  public-key
                                :options sign-alg}))
