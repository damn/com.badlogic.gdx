(ns gdx.backends.lwjgl
  (:require [gdx.application-listener :as application-listener]
            [gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl3-application-configuration]))

(defn application! [config]
  (lwjgl3-application-configuration/use-glfw-async!)
  (lwjgl3-application/create (application-listener/create config)
                             (lwjgl3-application-configuration/create config)))
