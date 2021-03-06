(ns vinyasa.maven.file
  (:require [clojure.string :as string]
            [clojure.java.io :as io])
  (:import [clojure.lang Symbol]))

(def ^:dynamic *sep* (System/getProperty "file.separator"))

(def ^:dynamic *clojure-loader*
  (or (.getClassLoader clojure.lang.RT)
      (.getContextClassLoader (Thread/currentThread))))

(def ^:dynamic *java-class-path*
  (->> (string/split (System/getProperty "java.class.path") #":")
       (filter (fn [x] (.endsWith x ".jar")))))

(def ^:dynamic *java-home* (System/getProperty "java.home"))

(def ^:dynamic *java-runtime-jar* (str *java-home* "/lib/rt.jar"))

(defn resource-symbol-path [sym]
  (let [sym-str (-> (str sym)
                    (.replaceAll "\\." *sep*)
                    (.replaceAll "-" "_"))
        f-char (-> sym-str (string/split (re-pattern *sep*)) last first)]

    (str sym-str
         (if (<= (int \A) (int f-char) (int \Z))
           ".class"
           ".clj"))))

(defn resource-path [x]
  (condp = (type x)
    String x
    Symbol (resource-symbol-path x)
    Class (-> (.getName x)
              (.replaceAll "\\." *sep*)
              (str  ".class"))))
