#!/usr/bin/env bb

(require '[babashka.fs :as fs]
         '[babashka.tasks :refer [shell]])

(fs/copy "resources/public/index.html" "gh-pages"
         {:replace-existing true})
(shell "clojure -M:dev -m shadow.cljs.devtools.cli release main")
(def index-file (fs/file "gh-pages" "index.html"))

(def js-source-dir (fs/file "resources" "public" "js"))
(def js-target-dir (fs/file "gh-pages" "js"))
(fs/create-dirs js-target-dir)

(run! (fn [f]
        (println "Copying" (str f))
        (fs/copy f
                 js-target-dir
                 {:replace-existing true}))
      (fs/glob js-source-dir "sci-script-tag*.js"))

(def with-gh-pages (partial shell {:dir "gh-pages"}))
(with-gh-pages "git add .")
(with-gh-pages "git commit -m 'update build'")
(with-gh-pages "git push origin gh-pages")

nil
