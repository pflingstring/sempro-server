(ns sempro.core
  (:require
    [re-frisk.core :refer [enable-re-frisk!]]
    [ajax.core :refer [GET POST]]

    [cljsjs.material-ui]
    [cljs-react-material-ui.core :refer [get-mui-theme color]]
    [cljs-react-material-ui.reagent :as ui]
    [cljs-react-material-ui.icons   :as ic]

    [reagent.core  :as r]
    [re-frame.core :as rf]
    ))


(defn event-view []
  [ui/mui-theme-provider
   {:mui-theme (get-mui-theme
                 {:palette {:text-color (color :green800)}})}

   [:div
    [ui/app-bar {:title "Sempro"}]

    [ui/paper {:style {:width "550px"}}
     [ui/card {:expandable true}
      [ui/card-header {:title                  "Begrusungsabend"
                       :subtitle               "1. April 2017 | 20:15 Uhr"
                       :show-expandable-button true
                       :act-as-expander        true}]
      [ui/card-text {:expandable true
                     :text-color "black"}
       [ui/divider] [:br]
       "Welcome back!"]]

     [ui/card {:expandable true}
      [ui/card-header {:title                  "HausundGartentage"
                       :subtitle               "4. 5. April 2017 | 10:15 Uhr"
                       :show-expandable-button true
                       :act-as-expander        true}]]

     [ui/card {:expandable true}
      [ui/card-header {:title                  "Convent"
                       :subtitle               "4. April 2017 | 20:15 Uhr"
                       :show-expandable-button true
                       :act-as-expander        true}]
      [ui/card-text {:expandable true}
       [ui/divider]
       [:br]
       "Anconvent des SS. 2917"]]

     [ui/card {:expandable true}
      [ui/card-header {:title                  "Ankneipe"
                       :subtitle               "4. April 2017 | 20:15 Uhr"
                       :show-expandable-button true
                       :act-as-expander        true}]]

     ]]])


(defn ui []
  [:div
   [event-view]
   ])

(defn mount-components []
  (r/render [ui] (.getElementById js/document "root")))

(defn init! []
  (enable-re-frisk!)
  (mount-components))