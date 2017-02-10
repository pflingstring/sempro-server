(ns sempro.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-frisk.core :refer [enable-re-frisk!]]
            [ajax.core :refer [GET POST]]))

;;
;; Event Handlers
;;
(rf/reg-event-db :init-db (fn [_ _] {}))


;; TODO: get rid of side effects, i.e. 'describe', not 'cause'

(rf/reg-event-db
  :to-localStorage
  (fn [db [_ token]]
    (.setItem js/localStorage "pem" token)
    (assoc db :token token)))

(rf/reg-event-db
  :from-localStorage
  (fn [db _]
    (let [header (.getItem js/localStorage "pem")]
      (assoc db :header header))))

(rf/reg-event-db
  :login
  (fn [db [_ login-data]]
    (POST "/login"
          {:params @login-data
           :handler #(rf/dispatch [:to-localStorage %1])})
    (assoc db :logged-in true)))

(rf/reg-event-db
  :pop-events
  (fn [db [_ events]]
    (assoc db :events events)))

(rf/reg-event-db
  :get-events
  (fn [db _]
    (GET "/events"
         {:headers {"Authorization" (:token db)}
          :handler #(rf/dispatch [:pop-events %])})
    (assoc db :events [])))

(rf/reg-sub
  :show-events
  (fn [db _]
    (:events db)))

(defn login-view [value]
  [:div
   [:h1 "Login"]
   [:input {:type      "text" :value (:email @value)
            :on-change #(swap! value assoc :email (-> % .-target .-value))}]

   [:input {:type      "text" :value (:pass @value)
            :on-change #(swap! value assoc :pass (-> % .-target .-value))}]

   [:input {:type     "button" :value "Login"
            :on-click #(rf/dispatch [:login value])}]

   [:input {:type     "button" :value "GET EVENTS"
            :on-click #(rf/dispatch [:get-events])}]

   [:h1 "Events"]
   [:p (str "my events " @(rf/subscribe [:show-events]))]])


(defn ui []
  [:div
   [login-view (r/atom {})]])

(defn mount-components []
  (r/render [ui] (.getElementById js/document "root")))

(defn init! []
  (rf/dispatch-sync [:init-db])
  (enable-re-frisk!)
  (mount-components))