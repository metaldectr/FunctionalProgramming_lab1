(ns com.romario.fplab1.file.cluster_estimation
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
                (assoc distance1Map restKeyPoint
                (if (not= distance nil)
                  distance
                  (calculateDistance (get pointsMap keyPoints) (get pointsMap restKeyPoint)))
                  )
                ))
        {} (keys (dissoc pointsMap keyPoints))
      )))
    {} (keys pointsMap)
  ))

(defn- getPotentialForPoint [idPoint pointsMap distancesMap]
  (let [restPoints (dissoc pointsMap idPoint)]
    (apply + (map (fn [restPointsId]
                    (Math/pow Math/E (- (* alpha (get-in distancesMap [idPoint restPointsId])))))
                  (keys restPoints)
                  )))
)

(defn- getPotentialsMap [pointsMap distancesMap]
  (reduce (fn [potentialsMap idPoint]
            (assoc potentialsMap idPoint (getPotentialForPoint idPoint pointsMap distancesMap)))
    {} (keys pointsMap)
  )
)

(defn- getMaxPotential [potentialMap]
  (key (apply max-key val potentialMap))
)

(defn- getMinDistanceToCenter [nextCenter centersMap distanceMap]
  (apply min (map (fn [idCenter] (get-in distanceMap [nextCenter idCenter]))
                (keys centersMap)
              ))
)

(defn- revicePotentials [lastCenterId lastPotential restPotentials distancesMap]
  (reduce (fn [revicedpotentialMap restPotentialId]
            (assoc revicedpotentialMap restPotentialId
              (- (get restPotentials restPotentialId) (* lastPotential (Math/pow Math/E (- (* beta (get-in distancesMap [lastCenterId restPotentialId]))))))))
    {} (keys restPotentials))
)

(defn- getCenters [potentialMap distanceMap]
  (loop [lastCenterId (getMaxPotential potentialMap)
    centersMap { lastCenterId (get potentialMap lastCenterId) }
    revisedPotentials (revicePotentials lastCenterId (get potentialMap lastCenterId) (dissoc potentialMap lastCenterId) distanceMap)]
    (let [nextCenterId (getMaxPotential revisedPotentials)
         nextCenterPotential (get revisedPotentials nextCenterId)
         lastClusterCenterPotential (get potentialMap lastCenterId)]
     (if (< nextCenterPotential (* 0.15 lastClusterCenterPotential))
       centersMap
       (let [dMin (getMinDistanceToCenter nextCenterId centersMap distanceMap)
             isNextClusterCenter (or (> nextCenterPotential (* 0.5 lastClusterCenterPotential))
                                     (>= (+ (/ dMin A) (/ nextCenterPotential lastClusterCenterPotential)) 1))
             centersMap (if isNextClusterCenter
                              (assoc centersMap nextCenterId (get revisedPotentials nextCenterId))
                              centersMap)
             revisedPotentials (if isNextClusterCenter
                                 revisedPotentials
                                 (assoc revisedPotentials nextCenterId 0))
             lastCenterId (if isNextClusterCenter nextCenterId lastCenterId)
             revisedPotentials (revicePotentials lastCenterId
             (get potentialMap lastCenterId)
             (dissoc revisedPotentials lastCenterId) distanceMap)]
  (recur lastCenterId centersMap revisedPotentials)))))
)

(defn getClusterCenters []
  (let [linesText (readTextFile filePath)
        pointsMap (convertLinesToMap linesText)
        distancesMap (getDistances pointsMap)
        potentialMap (getPotentialsMap pointsMap distancesMap)
        centersMap (getCenters potentialMap distancesMap)
        ]
    (println "end getClusterCenters")
    (print "Centers >> " (keys centersMap))
  ))
