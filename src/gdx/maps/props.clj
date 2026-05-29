(ns gdx.maps.props
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn get [^MapProperties props k]
  (.get props k))
