(let [width "40%"
      show-error? false
      error-message "Username unknown or wrong password"] 
  [:v-vertical-layout {:size-full true :margin true}
   [:v-label {:style-name "h1 colored" ":center" true ":expand" "0.2" :size-auto true} "Corp Inc" [:strong "App"]]
   [:v-panel {:caption "Please Login To Continue" ":expand" "0.8" ":center" true :width width}
    [:v-vertical-layout {:size-full true :margin true :spacing true}
     [:v-label {:_id "error" :style-name "failure" :visible show-error? :size-full true} error-message]
     [:v-text-field {:_id "username" :size-full true :placeHolder "user@example.com" :caption "Username" :focus true}]
     [:v-password-field {:_id "password" :size-full true :caption "Password"}]
     [:v-horizontal-layout {:spacing true :width "100%"}
      [:v-button "Cancel"]
      [:v-button {:style-name "primary" ":right" true} "Login"]]]]
   [:v-label {:style-name "small" ":right" true :size-auto true} "Copyright © 2042 Corp Inc.  All Rights Reserved."]])
