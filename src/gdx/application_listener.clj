(ns gdx.application-listener
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.gdx :as gdx]))

(defn create
  [{:keys [create!
           dispose!
           render!
           resize!
           pause!
           resume!]
    :as listener}]
  (application-listener/create
   (assoc listener :create!
          (fn []
            (create! (gdx/app)))))) ; this is not simple, stateful decision move more up in the code !
