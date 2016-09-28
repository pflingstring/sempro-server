(ns sempro.core
  (:require [reagent.core :as r]))

(defn hello []
  [:p "Hello World from ClojureScript!"])

(defn mount-components []
  (r/render [hello] (.getElementById js/document "root")))

(defn init! []
  (mount-components))
