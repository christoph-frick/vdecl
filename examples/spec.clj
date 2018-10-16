(require '[clojure.spec.alpha :as s])
(require '[clojure.spec.gen.alpha :as gen])

(s/def :component/type #{:v-label :v-button})
(s/def :component-container/type #{:v-vertical-layout :v-horizontal-layout})
(s/def :component/attributes (s/keys))
(s/def :component/content string?)
(s/def :component/element-def (s/cat :type :component/type
                                     :attr (s/? :component/attributes)
                                     :content :component/content))
(s/def :component/element (s/with-gen 
                            (s/and 
                              vector? 
                              :component/element-def)
                            #(gen/fmap vec (s/gen :component/element-def))))
(s/def :component-container/content (s/* (s/alt :component :component/element #_#_ :container :component-container/element))) ;FIXME: needs to be limited
(s/def :component-container/element-def (s/cat :type :component-container/type
                                               :attr (s/? :component/attributes)
                                               :content :component-container/content))
(s/def :component-container/element (s/with-gen
                                      (s/and
                                        vector?
                                        :component-container/element-def)
                                      #(gen/fmap vec (s/gen :component-container/element-def))))

(gen/generate (s/gen :component-container/element))
