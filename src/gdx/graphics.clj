(ns gdx.graphics
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap])
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics GL20)))

(defn delta-time [^Graphics graphics]
  (.getDeltaTime graphics))

(defn frames-per-second [^Graphics graphics]
  (.getFramesPerSecond graphics))

(defn clear! [^Graphics graphics r g b a]
  (let [gl (.getGL20 graphics)]
    (.glClearColor gl r g b a)
    (.glClear gl GL20/GL_COLOR_BUFFER_BIT)))

(defn set-cursor! [^Graphics graphics cursor]
  (.setCursor graphics cursor))

(defn new-cursor [^Graphics graphics pixmap hotspot-x hotspot-y]
  (.newCursor graphics pixmap hotspot-x hotspot-y))

(defn white-pixel-texture []
  (let [pixmap (doto (pixmap/create 1 1)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (pixmap/texture pixmap)]
    (pixmap/dispose! pixmap)
    texture))
