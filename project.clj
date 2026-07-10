(defproject com.badlogic.gdx "1.14.2-0.1"
  :description "Thin Clojure facades over libGDX 1.14.x"
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [[com.badlogicgames.gdx/gdx "1.14.2"]
                 [org.clojure/clojure "1.12.0"]]
  :global-vars {*warn-on-reflection* true})
