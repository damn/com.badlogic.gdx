(ns com.badlogic.gdx.graphics.texture
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Texture)))

(defprotocol New
  (new [source]))

(extend-type FileHandle
  New
  (new [file-handle]
    (Texture. file-handle)))

(extend-type Pixmap
  New
  (new [pixmap]
    (Texture. pixmap)))
