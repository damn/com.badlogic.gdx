(ns gdx.graphics
  (:import (com.badlogic.gdx GL20
                             Graphics
                             Pixmap
                             Pixmap$Format
                             Texture)))

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
  (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                 (.setColor 1 1 1 1)
                 (.drawPixel 0 0))
        texture (Texture. pixmap)]
    (.dispose pixmap)
    texture))
