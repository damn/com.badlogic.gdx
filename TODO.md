# TODO

## Project rules (facade conventions)

- [ ] Codify README rules as a Cursor rule (`.cursor/rules/libgdx-facade.mdc`)
  - One namespace per Java class (exact class path, even if long)
  - No cross-dependencies between facade namespaces — only `:import`
  - No reflection warnings (`*warn-on-reflection* true` in `project.clj`)
  - No return-type hints on `defn` (param hints like `^Actor` are fine)
  - Plain facade only — wrap Java, no extra abstraction layer
- [ ] Optionally add `AGENTS.md` pointing agents at the rules and this file

## Compile / test workflow

- [ ] Update README: document `lein check` as the primary compile verification command
- [ ] Fix README version drift (README says gdx `1.14.0`, `project.clj` uses `1.14.2`)
- [ ] Fix or replace broken `lein run -m app-test` entry in README
  - Option A: add minimal `app-test` namespace that `(require)`s every facade ns (smoke test)
  - Option B: remove `app-test` from README and rely on `lein check` only
- [ ] Consider CI (GitHub Actions) running `lein check` on push

## Continue the facade

- [ ] Add new facade namespaces on demand as the consumer game needs more libGDX classes
- [ ] Keep inner-class naming convention: `$` in filename (`input$keys.clj`, `pixmap$format.clj`)
- [ ] Keep Java interop style: `Class/.method` (e.g. `(Table/.add table actor)`)
- [ ] Use `(:refer-clojure :exclude [...])` when shadowing Clojure builtins (`class`, `new`, etc.)
- [ ] Optional sugar is allowed where it already exists: keyword lookup on proxies, `reify` for listeners, `proxy` for actors

## Integration

- [ ] Wire this repo as a dependency in the consumer game project (git dep or local `checkouts/`)
- [ ] Document how to add a new facade class (template / checklist in README)

## Small consistency nits (optional)

- [ ] `stage.clj`: use `Stage/.hit` instead of bare `.hit` for consistency
- [ ] `actor.clj`: add `^Actor` type hint on `get-stage` param (siblings already have hints)

## Done / verified

- [x] `lein compile` succeeds
- [x] `lein check` succeeds — all 72 namespaces load with no reflection warnings
- [x] No `:require` between facade namespaces (imports only)
- [x] Code extracted from consumer project into standalone library
