(defproject gdx "-SNAPSHOT"
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [
                 [com.badlogicgames.gdx/gdx                   "1.14.0"]
                 [com.badlogicgames.gdx/gdx-freetype          "1.14.0"]
                 [com.badlogicgames.gdx/gdx-freetype-platform "1.14.0" :classifier "natives-desktop"]

                 ;;;

                 ;[com.badlogicgames.gdx/gdx-platform          "1.14.0" :classifier "natives-desktop"]
                 ;[com.badlogicgames.gdx/gdx-backend-lwjgl3    "1.14.0"]
                 [com.badlogic.gdx.backends.lwjgl3 "-SNAPSHOT"]

                 ;;;

                 [space.earlygrey/shapedrawer "2.6.0"]
                 [org.clojure/clojure "1.12.0"]
                 ]
  :source-paths ["src"]
  :java-source-paths ["java-src"]
  :resource-paths ["resources/"]
  :plugins [[lein-hiera "2.0.0"]
            [lein-codox "0.10.8"]]
  :codox {:source-uri "https://github.com/damn/gdx/blob/main/{filepath}#L{line}"
          :metadata {:doc/format :markdown}}
  :global-vars {*warn-on-reflection* true})
