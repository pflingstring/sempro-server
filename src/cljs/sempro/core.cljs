(ns sempro.core
  (:require
    [re-frisk.core :refer [enable-re-frisk!]]
    [ajax.core :refer [GET POST]]

    [cljsjs.material-ui]
    [cljs-react-material-ui.core :refer [get-mui-theme color]]
    [cljs-react-material-ui.reagent :as ui]
    [cljs-react-material-ui.icons :as ic]

    [reagent.core :as r]
    [re-frame.core :as rf]
    ))


(defn event-view []
  [ui/mui-theme-provider
   {:mui-theme (get-mui-theme
                 {:palette {:text-color (color :green800)}})}


   [:div
    [ui/list
     [ui/list-item
      ;[ui/paper {:z-depth 1}
       [ui/card {:expandable true}
        [ui/card-header {:title                  "Begrusungsabend"
                         :style                  {:background "#E3F2FD"
                                                  :padding 8}
                         :subtitle               "1. April 2017 | 20:15 Uhr"
                         :show-expandable-button true
                         :act-as-expander        true}]
        [ui/card-text {:expandable true}
         [:div
          [ui/text-field {:default-value "ASDA DAS DA DAS DA DAS DAS DAS "
                          :id            "LOLs"}]

          [:br]
          [ui/mui-theme-provider
           {:mui-theme (get-mui-theme {:palette {:text-color (color :blue800)}})}
           [ui/raised-button {:label   "Edit"
                              :primary true}]]
          [ui/mui-theme-provider
           {:mui-theme (get-mui-theme {:palette {:text-color (color :blue800)}})}
           [ui/raised-button {:label   " Permissions"
                              :style   {:margin-left 5}
                              :primary true}]]
          ]]]]]
    ;]

    [ui/list-item {:pirmary-text "Nova Pakalenia | 20.05.2017 20:15"
                   ;:primaty-toggles-nested-list true
                   ;:initially-open true
                   :nested-items [[ui/list-item {:key 1
                                                 :primary-text "LOL SAU DRU"}]
                                  [ui/list-item {:key 2
                                                :primary-text "Vtaroi maioi laboi"}]]}]


    [ui/list-item
     [ui/paper
      [ui/card {:expandable true}
       [ui/card-header {:title                  "HausundGartentage"
                        :subtitle               "4. 5. April 2017 | 10:15 Uhr"
                        :show-expandable-button true
                        :act-as-expander        true}]
       [ui/card-text {:expandable true}
        [ui/divider]
        "Yo yo yo chak noris"]]]]


    [ui/list-item
     [ui/paper
      [ui/card {:expandable true}
       [ui/card-header {:title                  "Convent"
                        :subtitle               "4. April 2017 | 20:15 Uhr"
                        :show-expandable-button true
                        :act-as-expander        true}]
       [ui/card-text {:expandable true}
        [ui/divider]
        [:br]
        "Anconvent des SS. 2917"]]]]

    [ui/list-item
     [ui/paper
      [ui/card {:expandable true}
       [ui/card-header {:title                  "Ankneipe"
                        :subtitle               "4. April 2017 | 20:15 Uhr"
                        :show-expandable-button true
                        :act-as-expander        true}]]]]


    ]])

(defn header [current-view]
  [ui/mui-theme-provider
   {:mui-theme (get-mui-theme {:palette {:text-color (color :green800)}})}

   [ui/app-bar {:title "Sempro"}]

    ]
  )

(defn menu []
  [ui/mui-theme-provider
   {:mui-theme (get-mui-theme
                 {:palette {:text-color (color :green800)}})}
   [ui/paper {:z-depth 2}
    [ui/menu
     [ui/menu-item {:primary-text "All"}]
     [ui/menu-item {:primary-text "Tasks"}]]]])

(defn display [menu screen]
  [:div
   [:tr {:valign "top"}
    [:td {:width "220"} menu]
    [:div {:style {:width "900px"}} screen]]])

(defn ui []
  [:body
   [header (r/atom {:state {:value    1
                            :title    "Semesterprogramm"
                            :semester "SS 2017"}})]
   (display [menu] [event-view])
   ; []
   ]
  )

(defn mount-components []
  (r/render [ui] (.getElementById js/document "root")))

(defn init! []
  (enable-re-frisk!)
  (mount-components))