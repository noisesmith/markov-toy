(ns markov.core
  (:require [clojure.pprint :as pprint :refer [pprint]]
            [clojure.java.io :as io])
  (:gen-class))

(defn gen-chain
  [text base memory]
  (reduce
   (fn [m ngram]
     (update-in m [(butlast ngram) (last ngram)] (fnil inc 0)))
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

(defn gen-text*
  [markov start]
  (let [next (pick (get markov start))]
    (if (contains? #{nil :stop} next)
      nil
      (cons next
            (lazy-seq (gen-text* markov (concat (rest start) [next])))))))

(defn gen-text
  [markov start]
  (apply str (concat (rest start) (gen-text* markov start))))

(defn -main
  "generates a character-wise markov chain with memory 1 from an input file or
   the Gettysburg address"
  [& [memory input]]
  (let [markov (gen-chain (or (and input (slurp input))
                              (slurp (io/resource "gettysburg.txt")))
                          {}
                          (Long. (or memory 1)))]
    ;; (pprint markov)
    (println (gen-text markov (rand-nth (filter #(= [:start]
                                                    (take 1 %))
                                                (keys markov)))))))
