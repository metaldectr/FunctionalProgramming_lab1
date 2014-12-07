(ns com.romario.fplab1.main.main
  (:require [com.romario.fplab1.file.file-manager :as logic])
  (:gen-class))

(def filePath "./src/com/romario/fplab1/resources/glass.data.txt")

(defn doubler [f] (fn [& args] (* 2 (apply f args))))

(def doubler-+ (doubler +))

(defn -main [& args]
  (prn "Start lab!")
  (prn "Hello bro")
  (logic/getClusterCenters)
  #_(logic/readTextFile filePath)
  #_(print (logic/readTextFile filePath))
  (prn "End lab!!")
)


