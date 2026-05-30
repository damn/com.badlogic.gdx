(ns com.badlogic.gdx.scenes.scene2d.ui.window
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)))

(defn create [title skin]
  (Window. ^String title ^Skin skin))

(defn set-modal! [^Window window]
  (.setModal window true))

(defn title-table [^Window window]
  (.getTitleTable window))
