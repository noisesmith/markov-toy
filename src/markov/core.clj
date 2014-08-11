(ns markov.core
  (:require [clojure.pprint :as pprint :refer [pprint]]
            [clojure.java.io :as io])
  (:gen-class))

(defn gen-chain
  [text base memory]
  (reduce
   (fn [m ngram]
     (update-in m ngram (fnil inc 0)))
   {}
   (partition (inc memory) 1 (concat [:start] text [:stop]))))

(defn pick
  [probs]
  (when probs
    (let [total (apply + (vals probs))
          selector (rand-int total)]
      (reduce
       (fn [selector [token chance]]
         (if (< selector chance)
           (reduced token)
           (- selector chance)))
       selector
       probs))))

(defn gen-text
  [markov & [start]]
  (let [start (or start (pick (:start markov)))
        next (pick (get markov start))]
    (if (contains? #{nil :stop} next)
      nil
      (cons next
            (lazy-seq (gen-text markov next))))))

(defn -main
  "generates a character-wise markov chain with memory 1 from an input file or
   the Gettysburg address"
  [& [input]]
  (let [markov (gen-chain (or (and input (slurp input))
                              (slurp (io/resource "gettysburg.txt")))
                          {}
                          1)]
    (pprint markov)
    (println (apply str (gen-text markov)))))
