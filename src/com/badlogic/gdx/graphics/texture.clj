(ns com.badlogic.gdx.graphics.texture
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Texture)))

(defn new
  [source]
  (if (instance? FileHandle source)
    (Texture. ^FileHandle source)
    (Texture. ^Pixmap source)))
