(ns gdx.input
  (:import (com.badlogic.gdx Input)))

(defn set-processor! [^Input input processor]
  (.setInputProcessor input processor))

(defn key-pressed? [^Input input k]
  (.isKeyPressed input k))
