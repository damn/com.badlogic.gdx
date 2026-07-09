# Why wrap libGDX?

The Clojure community usually says: *just use Java interop — it's fine, don't wrap everything.*

That's true for small scripts and one-off calls. This library exists for a game that will live for years and may outgrow Java libGDX.

## `clojure.core` wraps Java too

The "don't wrap" advice ignores how Clojure itself is built. Most of `clojure.core` is thin functions over `clojure.lang.RT`, `PersistentVector`, and other Java classes — not raw interop at every call site:

```clojure
;; clojure.core (simplified)
(defn conj [coll x] (.conj RT coll x))
(defn first [coll] (.first RT coll))
(defn rest [coll] (.rest RT coll))
(defn nth [coll i] (.nth RT coll i))
```

Nobody tells you to call `RT/conj` directly in application code. The standard library establishes a **Clojure-shaped seam** over the JVM. This library does the same for libGDX: `table/add!` over `(.add table actor)`.

---

## Functions are easier to depend on than Java objects

When game code calls Java directly:

```clojure
(.add table actor)
(.draw stage)
```

your dependencies are **Java objects and their methods**. To swap an implementation you must replace the object, rewire constructors, or mock at the JVM boundary.

When game code calls a facade function:

```clojure
(table/add! table actor)
(stage/draw! stage)
```

your dependency is a **Clojure var**. You can `with-redef` it in tests or experiments without touching call sites, and replace one namespace with pure Clojure later without rewriting game code.

```clojure
(with-redefs [stage/draw! (fn [_] nil)]
  (run-game-loop-once))
```

You cannot `with-redef` a Java method. You can redef a function.

---

## Clojure naming conventions

### `!` = mutation / side effect

Java `setX` / `add` / `draw` become obvious mutators:

```clojure
;; com.badlogic.gdx.scenes.scene2d.ui.table
(table/add! table actor)
(table/row! table)

;; com.badlogic.gdx.scenes.scene2d.stage
(stage/act! stage)
(stage/draw! stage)
```

Plain interop: `(.draw stage)` — is it pure? With the facade: `stage/draw!` — clearly not.

### `?` = boolean predicates

Java `isVisible`, `isKeyPressed`, `overlaps` → Clojure predicates:

```clojure
;; com.badlogic.gdx.scenes.scene2d.actor
(actor/visible? actor)

;; com.badlogic.gdx.input
(input/key-pressed? input code)
(input/key-just-pressed? input code)

;; com.badlogic.gdx.math.rectangle
(rectangle/overlaps? a b)
(rectangle/contains? rect [x y])
```

Game code reads naturally: `(when (input/key-pressed? input code) ...)`.

---

## Keywords instead of Java constants

Magic ints become namespaced keywords — searchable, greppable, no static import:

```clojure
;; com.badlogic.gdx.input$keys
(input$keys/key-to-value :input.keys/space)  ; => Input$Keys/SPACE
(input$keys/key-to-value :input.keys/escape)

;; com.badlogic.gdx.input$buttons
(input$buttons/key-to-value :input.buttons/left)
```

Viewport objects can act like maps via keyword lookup:

```clojure
;; com.badlogic.gdx.utils.viewport.fit-viewport
(:viewport/camera viewport)
(:viewport/world-width viewport)
(:viewport/world-height viewport)
```

Instead of `(.getCamera viewport)` everywhere.

---

## Maps and vectors at the boundary

### Constructor maps

```clojure
;; com.badlogic.gdx.application-listener
(app-listener/new
  {:create!  #(init-game)
   :render!  #(render-frame)
   :dispose! #(cleanup)
   :resize!  (fn [w h] ...)
   :pause!   #(pause)
   :resume!  #(resume)})

;; com.badlogic.gdx.math.circle
(circle/new {:position [x y] :radius 10})
```

No anonymous Java class boilerplate. Pass a map of functions.

### Vector args and destructuring

```clojure
;; com.badlogic.gdx.graphics.color
(color/new [r g b a])

;; com.badlogic.gdx.math.vector2
(vector2/new [x y])

;; com.badlogic.gdx.math.rectangle
(rectangle/contains? rect [x y])
```

### Java collections → Clojure data

```clojure
;; com.badlogic.gdx.maps.map-properties
(map-properties/clojurize props)  ; => plain Clojure map

;; com.badlogic.gdx.math.vector2
(vector2/clojurize v2)  ; => [x y]

;; com.badlogic.gdx.math.vector3
(vector3/clojurize v3)  ; => [x y z]
```

---

## Hiding Java collection ceremony

```clojure
;; com.badlogic.gdx.scenes.scene2d.ui.select-box
(select-box/set-items! box ["Option A" "Option B" "Option C"])
```

Plain interop every time:

```clojure
(.setItems box (into-array Object ["a" "b" "c"]))
```

The facade accepts a Clojure seq/vector. `into-array` and the type hint live in one place.

---

## Hiding `float` / `int` casting

Java libGDX is picky about primitives. The facade absorbs that:

```clojure
;; com.badlogic.gdx.graphics.g2d.batch
(batch/draw-texture-region! batch region x y w h)
;; (float x) (float y) etc. handled inside

;; com.badlogic.gdx.scenes.scene2d.ui.cell
(cell/height! cell 100)
(cell/pad! cell 5)

;; com.badlogic.gdx.scenes.scene2d.ui.button-group
(button-group/set-max-check-count! group 1)
```

Game code passes normal numbers; no `(float x)` at every call site.

---

## Callbacks as functions (not anonymous Java classes)

```clojure
;; com.badlogic.gdx.scenes.scene2d.utils.click-listener
(click-listener/new
  (fn [event x y]
    (handle-click x y)))

;; com.badlogic.gdx.scenes.scene2d.utils.change-listener
(change-listener/new
  (fn [event actor]
    (on-change actor)))

;; com.badlogic.gdx.scenes.scene2d.actor
(actor/new
  (fn [self delta] (update-logic self delta))
  (fn [self batch alpha] (draw-self self batch alpha)))
```

Pass a function; the `proxy` / `reify` noise stays in the facade.

---

## Disambiguating Java overloads and fluent builders

Java overloads become separate Clojure functions:

```clojure
;; com.badlogic.gdx.scenes.scene2d.ui.image
(image/new texture-region)
(image/new-drawable drawable)
(image/new-texture texture)
```

Java fluent builders become plain functions (easier to compose):

```clojure
;; com.badlogic.gdx.scenes.scene2d.ui.cell
(-> (table/add! t widget)
    (cell/width! 100)
    (cell/pad! 5)
    (cell/center!))
```

---

## Avoiding clashes with `clojure.core`

Facade namespaces exclude Clojure builtins so game code can use familiar names:

```clojure
(table/new)                        ; not shadowed by clojure.core/new
(table/add! t actor)               ; not shadowed by clojure.core/add
(map-properties/get props "key")   ; not shadowed by clojure.core/get
(rectangle/contains? rect [x y])   ; not shadowed by clojure.core/contains?
(button-group/remove! group btn)   ; not shadowed by clojure.core/remove
```

---

## Constants as plain `def`s

```clojure
batch/x1              ; vertex attribute index
batch/y1
align/center          ; alignment constant
touchable/disabled    ; touchable mode
texture$texture-filter/linear
pixmap$format/rgba8888
```

Refer by namespace — no Java static field syntax in game code.

---

## Type references without Java imports

```clojure
;; com.badlogic.gdx.scenes.scene2d.ui.window
window/class   ; => Window class, for type hints or instance?
```

Game code can reference types without importing Java classes directly.

---

## Reflection hints centralized

`*warn-on-reflection* true` in `project.clj`. Type hints live in the facade, not scattered through game code:

```clojure
;; facade (one place)
(defn add! [^Table table ^Actor actor] ...)

;; game code (no hints needed)
(table/add! t actor)
```

---

## Multi-arity where Java has overloads

```clojure
;; com.badlogic.gdx.graphics.g2d.texture-region
(texture-region/new texture)
(texture-region/new texture x y w h)

;; com.badlogic.gdx.scenes.scene2d.actor
(actor/set-position! actor x y)
(actor/set-position! actor x y align)
```

---

## What we are not claiming

- This is not "Clojure-ifying" libGDX into idiomatic Clojure data
- This is not hiding Java — you still pass Java objects around
- This is not full API coverage — only classes you actually use get a namespace

It is a **seam**: game code → Clojure functions → Java libGDX today → pure Clojure tomorrow.

---

## Summary

| Advantage | Example |
|-----------|---------|
| Same pattern as `clojure.core` | thin fns over `RT` / Java internals |
| `with-redef` / testability | `(with-redefs [stage/draw! ...])` |
| `!` / `?` naming | `draw!`, `visible?`, `key-pressed?` |
| Keywords | `:input.keys/space`, `:viewport/camera` |
| Maps at boundary | `application-listener/new`, `circle/new` |
| Vectors | `color/new`, `vector2/new`, `contains?` with `[x y]` |
| `into-array` hidden | `select-box/set-items!` |
| `float`/`int` casting | `batch`, `cell`, `button-group` |
| Callbacks as fns | `click-listener`, `actor/new` |
| Builder → fns | `cell/width!`, `cell/pad!` |
| `clojure.core` clashes | `:exclude [new add get ...]` |
| Constants as `def` | `batch/x1`, `align/center` |
| Java → Clojure data | `map-properties/clojurize`, `vector2/clojurize` |
| Reflection hints | centralized in facade namespaces |
