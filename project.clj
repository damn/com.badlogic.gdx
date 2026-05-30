(defproject com.badlogic.gdx "1.14.0"
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [
                 [com.badlogicgames.gdx/gdx "1.14.0"]
                 [org.clojure/clojure "1.12.0"]
                 ]
  :source-paths ["src"]
  :resource-paths ["resources/"]
  :plugins [[lein-hiera "2.0.0"]
            [lein-codox "0.10.8"]]
  :codox {:source-uri "https://github.com/damn/com.badlogic.gdx/blob/main/{filepath}#L{line}"
          :metadata {:doc/format :markdown}}
  :global-vars {*warn-on-reflection* true})
