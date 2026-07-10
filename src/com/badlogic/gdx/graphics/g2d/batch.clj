(ns com.badlogic.gdx.graphics.g2d.batch
  "A Batch is used to draw 2D rectangles that reference a texture (region). The class will batch the drawing commands and optimize them for processing by the GPU.

  To draw something with a Batch one has to first call the [[begin|begin()]] method which will setup appropriate render states. When you are done with drawing you have to call [[end|end()]] which will actually draw the things you specified.

  All drawing commands of the Batch operate in screen coordinates. The screen coordinate system has an x-axis pointing to the right, an y-axis pointing upwards and the origin is in the lower left corner of the screen. You can also provide your own transformation and projection matrices if you so wish.

  A Batch is managed. In case the OpenGL context is lost all OpenGL resources a Batch uses internally get invalidated. A context is lost when a user switches to another application or receives an incoming call on Android. A Batch will be automatically reloaded after the OpenGL context is restored.

  A Batch is a pretty heavy object so you should only ever have one in your program.

  A Batch works with OpenGL ES 2.0. It will use its own custom shader to draw all provided sprites. You can set your own custom shader via setShader(ShaderProgram).

  A Batch has to be disposed if it is no longer used."
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
  "Sets up the Batch for drawing. This will disable depth buffer writing. It enables blending and texturing. If you have more texture units enabled than the first one you have to disable them before calling this. Uses a screen coordinate system by default where everything is given in pixels. You can specify your own projection and modelview matrices via [[setProjectionMatrix|setProjectionMatrix(Matrix4)]] and setTransformMatrix(Matrix4)."
  [batch]
  (.begin ^Batch batch))

(defn end
  "Finishes off rendering. Enables depth writes, disables blending and texturing. Must always be called after a call to [[begin|begin()]]"
  [batch]
  (.end ^Batch batch))

(defn setColor
  "Sets the color used to tint images when they are added to the Batch. Default is Color.WHITE."
  [batch r g b a]
  (.setColor ^Batch batch
             (float r)
             (float g)
             (float b)
             (float a)))

(defn getColor
  "The rendering color of this Batch. If the returned instance is manipulated, [[setColor|setColor(Color)]] must be called afterward."
  [batch]
  (.getColor ^Batch batch))

(defn setProjectionMatrix
  "Sets the projection matrix to be used by this Batch. If this is called inside a [[begin|begin()]]/[[end|end()]] block, the current batch is flushed to the gpu."
  [batch matrix4]
  (.setProjectionMatrix ^Batch batch
                        matrix4))

(defn draw
  "Draws a rectangle using the given vertices. There must be 4 vertices, each made up of 5 elements in this order: x, y, color, u, v. The [[getColor|getColor()]] from the Batch is not applied.

  Draws a rectangle with the bottom left corner at x,y and stretching the region to cover the given width and height.

  Draws a rectangle with the bottom left corner at x,y and stretching the region to cover the given width and height. The rectangle is offset by originX, originY relative to the origin. Scale specifies the scaling factor by which the rectangle should be scaled around originX, originY. Rotation specifies the angle of counter clockwise rotation of the rectangle around originX, originY."
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
