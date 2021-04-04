(ns multisorter
  "Map Multisorter. Sort criteria are provided as a collection of maps of the form
  {:key   <fld>
   :dir   <int>
   :txfn  <fn to apply to field prior to comparison>}
  Where multiples are provided, they will be processed in order until the first non-colliding sort result.")

(defn- build-sort-fn
  "Builds a sorting function using the provided configuration."
  [s]
  (fn [x y]
    (loop [s s]
      (let [curr    (first s)
            rst     (rest s)
            txf     (:txfn curr)
            x       (x (:key curr))
            y       (y (:key curr))
            x       (if txf (txf x) x)
            y       (if txf (txf y) y)
            cmp-res (* (:dir curr) (compare x y))]
        (if (or (empty? rst) (not (zero? cmp-res)))
          cmp-res
          (recur rst))))))

(defn map-multisort
  "Builds and executes a multi-parameter sort against the provided collection of maps."
  [s coll]
  (sort (build-sort-fn s) coll))
