(let [width "40%"] 
  [:v-vertical-layout {:size-full true :margin true}
   [:v-label {:style-name "h1 colored" ":center" true ":expand" "0.2" :size-auto true} "Corp Inc" [:strong "App"]]
   [:v-panel {:caption "Please Login To Continue" ":expand" "0.8" ":center" true :width width}
    [:v-vertical-layout {:size-full true :margin true :spacing true}
     [:v-label {:_id "error" :style-name "failure" :visible false} "Error message here"]
     [:v-text-field {:_id "username" :size-full true :input-prompt "user@example.com" :caption "Username"}]
     [:v-password-field {:_id "password" :size-full true :caption "Password"}]
     [:v-horizontal-layout {:spacing true :width "100%"}
      [:v-button "Cancel"]
      [:v-button {:style-name "primary" ":right" true} "Login"]]]]
   [:v-label {:style-name "small" ":right" true :size-auto true} "Copyright © 2042 Corp Inc.  All Rights Reserved."]])
