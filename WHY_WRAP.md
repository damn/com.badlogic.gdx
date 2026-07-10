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

Nobody tells you to call `RT/conj` directly in application code. The standard library establishes a **Clojure-shaped seam** over the JVM. This library does the same for libGDX: `table/add` over `(.add table actor)`.

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
(table/add table actor)
(stage/draw stage)
```

your dependency is a **Clojure var**. You can `with-redef` it in tests or experiments without touching call sites, and replace one namespace with pure Clojure later without rewriting game code.

```clojure
(with-redefs [stage/draw (fn [_] nil)]
  (run-game-loop-once))
```

You cannot `with-redef` a Java method. You can redef a function.

---

## 1-1 Java name mapping

Facades use **mechanical 1-1 mapping** from libGDX. Method and constant names stay as close to Java as possible — no Clojure renaming.

```clojure
;; com.badlogic.gdx.scenes.scene2d.ui.table
(table/add table actor)
(table/row table)

;; com.badlogic.gdx.scenes.scene2d.stage
(stage/act stage)
(stage/draw stage)

;; com.badlogic.gdx.input
(input/isKeyPressed input code)
(input/isKeyJustPressed input code)

;; com.badlogic.gdx.scenes.scene2d.actor
(actor/isVisible actor)
(actor/setVisible actor true)
```

Why not `add!`, `draw!`, `visible?`, `key-pressed?`? Those read nicely in Clojure, but they add a translation layer between your code and libGDX docs, stack traces, and Java examples. With 1-1 mapping, `table.add` in JavaDoc is `table/add` in Clojure — no mental lookup table.

| Java | Facade |
|------|--------|
| `setVisible(boolean)` | `setVisible` |
| `isKeyPressed(int)` | `isKeyPressed` |
| `Input$Keys.SPACE` | `input$keys/SPACE` |
| `Batch.X1` | `batch/X1` |
| `new Table()` | `table/new` |

Constructors are `new`. Overloaded constructors keep Java names (`image/newDrawable`, `image/newTexture`). Multi-arity `defn` only where Java has overloads.

For behavior and semantics, read [libGDX JavaDoc](https://libgdx.com/dev/api/) — not Clojure docstrings. Facades intentionally carry none; see [Why no docstrings?](README.md#why-no-docstrings) in the README.

---

## Constants as plain `def`s

```clojure
batch/X1              ; vertex attribute index
batch/Y1
input$keys/SPACE
input$keys/ESCAPE
texture$texture-filter/linear
pixmap$format/rgba8888
```

Same names as Java static fields. Refer by namespace — no static import syntax in game code.

---

## Hiding Java collection ceremony

```clojure
;; com.badlogic.gdx.scenes.scene2d.ui.select-box
(select-box/setItems box ["Option A" "Option B" "Option C"])
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
(batch/draw batch region x y w h)
;; (float x) (float y) etc. handled inside

;; com.badlogic.gdx.scenes.scene2d.ui.cell
(cell/height cell 100)
(cell/pad cell 5)

;; com.badlogic.gdx.scenes.scene2d.ui.button-group
(button-group/setMaxCheckCount group 1)
```

Game code passes normal numbers; no `(float x)` at every call site.

---

## Callbacks as functions (not anonymous Java classes)

```clojure
;; com.badlogic.gdx.scenes.scene2d.utils.click-listener
(click-listener/create
  (fn [event x y]
    (handle-click x y)))

;; com.badlogic.gdx.scenes.scene2d.utils.change-listener
(change-listener/create
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

Java overloads become separate Clojure functions or multi-arity `defn`:

```clojure
;; com.badlogic.gdx.scenes.scene2d.ui.image
(image/new texture-region)
(image/newDrawable drawable)
(image/newTexture texture)
```

Java fluent builders become plain functions (easier to compose):

```clojure
;; com.badlogic.gdx.scenes.scene2d.ui.cell
(-> (table/add t widget)
    (cell/width 100)
    (cell/pad 5)
    (cell/center))
```

---

## Avoiding clashes with `clojure.core`

Facade namespaces exclude Clojure builtins so game code can use familiar names:

```clojure
(table/new)                        ; not shadowed by clojure.core/new
(table/add t actor)                ; not shadowed by clojure.core/add
(map-properties/get props "key")     ; not shadowed by clojure.core/get
(rectangle/contains rect x y)        ; not shadowed by clojure.core/contains?
(button-group/remove group btn)      ; not shadowed by clojure.core/remove
```

---

## Reflection hints centralized

`*warn-on-reflection* true` in `project.clj`. Type hints live in the facade, not scattered through game code:

```clojure
;; facade (one place)
(defn add [^Table table ^Actor actor] ...)

;; game code (no hints needed)
(table/add t actor)
```

---

## Multi-arity where Java has overloads

```clojure
;; com.badlogic.gdx.graphics.g2d.texture-region
(texture-region/new texture)
(texture-region/new texture x y w h)

;; com.badlogic.gdx.scenes.scene2d.actor
(actor/setPosition actor x y)
(actor/setPosition actor x y align)
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
| `with-redef` / testability | `(with-redefs [stage/draw ...])` |
| 1-1 Java name mapping | `table/add`, `isKeyPressed`, `input$keys/SPACE` |
| libGDX JavaDoc as docs | no duplicated docstrings in facades |
| `into-array` hidden | `select-box/setItems` |
| `float`/`int` casting | `batch`, `cell`, `button-group` |
| Callbacks as fns | `click-listener`, `actor/new` |
| Builder → fns | `cell/width`, `cell/pad` |
| `clojure.core` clashes | `:exclude [new add get ...]` |
| Constants as `def` | `batch/X1`, `input$keys/SPACE` |
| Reflection hints | centralized in facade namespaces |
