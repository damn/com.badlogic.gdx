# com.badlogic.gdx

Thin Clojure facades over [libGDX](https://libgdx.com/) 1.14.x.

Each Java class gets one Clojure namespace with the same package path. Game code
depends on these namespaces instead of calling libGDX Java classes directly.

**Why wrap instead of plain interop?** See [WHY_WRAP.md](WHY_WRAP.md).

**Stability guarantee?** See [stable.md](stable.md).

## What this is

- A plain interop layer: `(table/add table actor)`, not a game framework
- Reflection-clean (`*warn-on-reflection* true`, verified with `lein check`)
- Incrementally grown: ~72 classes facaded so far, more added as needed

## What this is not

- Not full libGDX API coverage
- Not a Clojure game engine (yet)
- Not on Clojars yet — use a git dependency (see below)

## Requirements

- Clojure 1.12+
- Leiningen
- libGDX 1.14.2 (transitive dependency)

## Usage

```clojure
(ns my.game.core
  (:require [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.gdx :as gdx]))

(def ui (table/new))
(table/add ui label)
(table/row ui)

(gdx/graphics)  ; => Gdx graphics singleton
```

### Namespace mapping

| Java | Clojure ns |
|------|------------|
| `com.badlogic.gdx.scenes.scene2d.ui.Table` | `com.badlogic.gdx.scenes.scene2d.ui.table` |
| `com.badlogic.gdx.Input$Keys` | `com.badlogic.gdx.input$keys` |
| `com.badlogic.gdx.ApplicationListener` | `com.badlogic.gdx.application-listener` |

Inner classes use `$` in the filename (`input$keys.clj`).

### Naming convention

We tried shorter names first — `gdx`, `clojure.gdx`, and similar. For libGDX alone that felt fine.

Then we noticed the same game also depends on other Java libraries, each with its own package root:

| Library | Java package (example) |
|---------|------------------------|
| VisUI | `com.kotcrab.vis.ui` |
| ShapeDrawer | `space.earlygrey.shapedrawer` |
| LWJGL | `org.lwjgl.system` |

Renaming libGDX to `gdx` or `clojure.gdx` would be a special case. Every other wrapper would need its own naming scheme. What we want is **one rule that works for all of them**.

Two conventions fit that:

| Java class | Mirror package (this repo) | `clojure.` prefix |
|------------|------------------------------|-------------------|
| `com.badlogic.gdx.Gdx` | `com.badlogic.gdx.gdx` | `clojure.com.badlogic.gdx.gdx` |
| `org.lwjgl.system.Configuration` | `org.lwjgl.system.configuration` | `clojure.org.lwjgl.system.configuration` |
| `space.earlygrey.shapedrawer.ShapeDrawer` | `space.earlygrey.shapedrawer.shape-drawer` | `clojure.space.earlygrey.shapedrawer.shape-drawer` |

**Rule (both):** take the Java FQN, kebab-case the simple class name, keep inner classes as `$` segments — only the prefix differs.

**`clojure.` prefix** — every Java wrapper lives under `clojure.<exact-java-package>`. You always know a namespace is a Clojure facade, not raw interop. Same pattern as `clojure.java.jdbc` sitting beside `java.sql`. All wrappers are grouped under `clojure.*` in search and deps.

**Mirror package (no prefix)** — the Clojure ns *is* the Java package path. Shortest mapping: stack trace, JavaDoc, or `(import ...)` → facade ns with no translation. This repo uses this today.

Either works across libGDX, VisUI, ShapeDrawer, LWJGL, and anything else — pick one and apply it everywhere. We may standardize on the `clojure.` prefix across wrapper repos later; for now this library keeps the mirror for the shorter paths already in use.

### Style guide

Facade namespaces use **mechanical 1-1 mapping** from Java. Names stay as close to libGDX as possible — no Clojure renaming of methods or constants.

| Java | Clojure facade |
|------|----------------|
| `table.add(actor)` | `(table/add table actor)` |
| `batch.setColor(r, g, b, a)` | `(batch/setColor batch r g b a)` |
| `input.isKeyPressed(code)` | `(input/isKeyPressed input code)` |
| `Input$Keys.SPACE` | `input$keys/SPACE` |
| `Batch.X1` | `batch/X1` |
| `new Table()` | `(table/new)` |

**Method names** — keep Java camelCase (`setVisible`, `isVisible`, `getColor`, `addActor`). Do not add `!` or `?` suffixes.

**Constructors** — `new`. Overloaded constructors keep Java names (`newDrawable`, `newTexture`).

**Constants** — mirror Java static field names (`SPACE`, `X1`, `linear`).

**Each `defn` wraps one Java method.** Multi-arity only where Java has overloads.

**No docstrings** — see [Why no docstrings?](#why-no-docstrings) below.

**Interop** — `.method` or `Class/.method` inside the facade. Type hints on params (`^Actor`), not return types. Cast primitives (`float`, `int`) inside the facade, not at call sites.

**`:refer-clojure :exclude`** when a facade name shadows `clojure.core` (`new`, `add`, `get`, `contains?`, …).

**Boundary sugar** (sparingly) — `reify`/`proxy` for listeners, map-of-fns for `ApplicationListener`, `into-array` hidden inside helpers like `setItems`.

### Conventions

- One namespace per Java class (exact package path)
- Facade namespaces only `:import` Java — no `:require` between facade ns
- Follow the style guide above for all new facades
- No reflection warnings

### Why no docstrings?

Facade namespaces do **not** carry docstrings. libGDX JavaDoc is the documentation.

With 1-1 name mapping, there is nothing to explain at the Clojure layer: `table/add` is `Table.add()`, `batch/setColor` is `Batch.setColor()`. The facade is a one-line mechanical wrapper — copying JavaDoc into Clojure would duplicate noise, create a second source of truth that drifts, and work against the [stability](#stability) promise (rewriting docs is still churn).

For behavior, semantics, and examples, use the [libGDX API reference](https://libgdx.com/dev/api/). README and [WHY_WRAP.md](WHY_WRAP.md) cover wrapper philosophy only.

### Stability

See [stable.md](stable.md) for the full policy. In short:

- **Accretion-only** — add new namespaces and vars; never rename or remove existing ones
- **No bug fixes** — incorrect mirroring stays; add a new var if you need different behavior
- **Pin a git sha** — what you depend on today is what you get tomorrow

## Adding as a dependency

### Leiningen (local checkout)

```clojure
:dependencies [[com.badlogicgames.gdx/gdx "1.14.2"]
               [org.clojure/clojure "1.12.0"]]
:source-paths ["checkouts/com.badlogic.gdx/src"]
```

Clone this repo into `checkouts/com.badlogic.gdx`, or `lein install` after building locally.

### deps.edn (git lib)

```clojure
com.badlogic.gdx/com.badlogic.gdx {:git/url "https://github.com/damn/com.badlogic.gdx"
                                   :git/sha "<tag-or-commit>"}
```

## Development

```bash
git clone https://github.com/damn/com.badlogic.gdx.git
cd com.badlogic.gdx
lein check    # verify all namespaces compile, no reflection warnings
```

## Contributing

Missing a libGDX class? Add a facade namespace following the [style guide](#style-guide) above,
run `lein check`, open a PR. See [TODO.md](TODO.md) for planned work.

New vars only — do not rename or change existing public functions ([stable.md](stable.md)).

## License

MIT — see [LICENSE](LICENSE).
