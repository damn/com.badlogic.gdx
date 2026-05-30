(ns com.badlogic.gdx.maps.map-properties
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn get [^MapProperties props k]
  (.get props k))
