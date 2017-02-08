(ns sempro.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-frisk.core :refer [enable-re-frisk!]]
            ))

(rf/reg-event-db
  :init-db
  (fn [_ _]
    {:clicked 0}))

(rf/reg-event-db
  :inc
  (fn [db _]
    (let [new-value (inc (:clicked db))]
      (assoc db :clicked new-value))))

(rf/reg-sub
  :times-clicked
  (fn [db _]
    (:clicked db)))

(rf/reg-event-db
  :reset
  (fn [db _]
    (assoc db :clicked 0)))

(defn hello []
  [:div
   [:p "You have clicked me " @(rf/subscribe [:times-clicked]) " times"]
   [:input {:type  "button"
            :value "Click"
            :on-click #(rf/dispatch [:inc])}]

   [:input {:type "button"
            :value "Reset"
            :on-click #(rf/dispatch [:reset])}]
   ]
  )

(defn mount-components []
  (r/render [hello] (.getElementById js/document "root")))

(defn init! []
  (rf/dispatch-sync [:init-db])
  (enable-re-frisk!)
  (mount-components))
