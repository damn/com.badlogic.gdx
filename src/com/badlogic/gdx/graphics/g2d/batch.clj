(ns com.badlogic.gdx.graphics.g2d.batch
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d Batch TextureRegion)))

(def X1 Batch/X1)
(def Y1 Batch/Y1)
(def C1 Batch/C1)
(def U1 Batch/U1)
(def V1 Batch/V1)
(def X2 Batch/X2)
(def Y2 Batch/Y2)
(def C2 Batch/C2)
(def U2 Batch/U2)
(def V2 Batch/V2)
(def X3 Batch/X3)
(def Y3 Batch/Y3)
(def C3 Batch/C3)
(def U3 Batch/U3)
(def V3 Batch/V3)
(def X4 Batch/X4)
(def Y4 Batch/Y4)
(def C4 Batch/C4)
(def U4 Batch/U4)
(def V4 Batch/V4)

(defn begin
  [batch]
  (.begin ^Batch batch))

(defn end
  [batch]
  (.end ^Batch batch))

(defn setColor
  [batch r g b a]
  (.setColor ^Batch batch
             (float r)
             (float g)
             (float b)
             (float a)))

(defn getColor
  [batch]
  (.getColor ^Batch batch))

(defn setProjectionMatrix
  [batch matrix4]
  (.setProjectionMatrix ^Batch batch
                        matrix4))

(defn draw
([batch texture verts offset cnt]
   (.draw ^Batch batch
          ^Texture texture
          ^floats verts
          (int offset)
          (int cnt)))
  ([batch texture-region x y w h]
   (.draw ^Batch batch
          ^TextureRegion texture-region
          (float x)
          (float y)
          (float w)
          (float h)))
  ([batch texture-region x y origin-x origin-y w h scale-x scale-y rotation]
   (Batch/.draw ^Batch batch
                ^TextureRegion texture-region
                (float x)
                (float y)
                (float origin-x)
                (float origin-y)
                (float w)
                (float h)
                (float scale-x)
                (float scale-y)
                (float rotation))))
