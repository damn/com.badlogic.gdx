(ns gdx.maps.layers
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn get [^MapLayers layers ^String layer-name]
  (.get layers layer-name))
