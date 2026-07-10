(defproject com.badlogic.gdx "1.14.2-0.1"
  :description "Thin Clojure facades over libGDX 1.14.x"
  :repositories [["jitpack" "https://jitpack.io"]]
  :plugins [[lein-codox "0.10.8"]]
  :dependencies [[com.badlogicgames.gdx/gdx "1.14.2"]
                 [org.clojure/clojure "1.12.0"]]
  :codox {:source-uri "https://github.com/damn/com.badlogic.gdx/blob/main/{filepath}#L{line}"
          :metadata {:doc/format :markdown}
          :source-paths ["src"]
          :output-path "target/doc"}
  :global-vars {*warn-on-reflection* true})
