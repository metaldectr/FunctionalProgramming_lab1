(ns com.romario.fplab1.file.file-manager
  (:require [clojure.java.io :as io]
   :require [clojure.string :as string]))

(def A 2)
(def B (* 1.5 A))
(def alpha (/ 4 (* A A)))
(def beta (/ 4 (* B B)))
(declare getClusterCenters)

(def filePath "./src/com/romario/fplab1/resources/glass.data.txt")


(defn readTextFile [file] (with-open [r (io/reader file)]
                              (reduce conj [] (line-seq r))
                          )
)

(defn- convertLinesToMap [linesText]
  (reduce (fn [pointsMap lineText] (let [data (string/split lineText #",")
                                         key (read-string (first data))
                                         value (vec (map read-string (rest data)))]
                                     (assoc pointsMap key value)))
   {} linesText))

(defn- calculateDistance [x y]
  (apply + (map (fn [xi yj] (Math/abs (- yj xi))) x y)
))

(defn- getDistances [pointsMap]
  (reduce (fn [distancesMap keyPoints]
    (assoc distancesMap keyPoints
      (reduce (fn [distance1Map restKeyPoint] (let [distance (get-in distancesMap [restKeyPoint keyPoints])]
                assoc distance1Map restKeyPoint
                (if (not= distance nill)
                  distance
                  (calculateDistance (get pointsMap keyPoints) (get pointsMap restKeyPoint))
                  )
                ))
        {} (keys (dissoc pointsMap keyPoints))
      )))
    {} pointsMap
  ))



(defn getClusterCenters []
  (let [linesText (readTextFile filePath)
        pointsMap (convertLinesToMap linesText)
        distancesMap (getDistances pointsMap)
        ]
    (println "end getClusterCenters")
  ))
