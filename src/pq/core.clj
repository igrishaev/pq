(ns pq.core
  (:import
   (java.util List)
   (java.lang.invoke MethodType
                     MethodHandle)
   (java.lang.foreign SymbolLookup
                      Linker
                      Arena
                      Linker$Option
                      MemoryLayout
                      MemorySegment
                      ValueLayout
                      FunctionDescriptor))
  (:gen-class))


(defn to-seq ^MemorySegment [^Arena arena ^String string]
  (let [^MemorySegment buf
        (.allocate arena ValueLayout/JAVA_BYTE 128)]
    (.setString buf 0 ^String string)
    buf))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (System/load "/opt/homebrew/Cellar/libpq/18.1/lib/libpq.dylib")
  ;; (System/loadLibrary "pq")

  (let [lookup (SymbolLookup/loaderLookup)]
    (doseq [sym ["PQconnectdbParams"
                 "PQconnectdb"
                 "PQsetdbLogin"
                 "PQlibVersion"
                 "PQconnectdbParams"
                 "PQsetdbLogin"]]
      (println (.find lookup sym))))

  #_
  (let [^Linker linker
        (Linker/nativeLinker)

        lookup
        (SymbolLookup/loaderLookup)

        mem-seg
        (.get (.find lookup "PQlibVersion"))

        func-descriptor
        (FunctionDescriptor/of ValueLayout/JAVA_INT
                               (into-array MemoryLayout []))

        ^MethodHandle method
        (.downcallHandle linker
                         mem-seg
                         func-descriptor
                         (into-array Linker$Option []))

        ^List args []

        result
        (.invokeWithArguments method args)]

    (println result))

  (let [^Linker linker
        (Linker/nativeLinker)

        lookup
        (SymbolLookup/loaderLookup)

        mem-seg
        (.get (.find lookup "PQconnectdb" #_"PQsetdbLogin"))

        func-descriptor
        (FunctionDescriptor/of ValueLayout/ADDRESS
                               (into-array MemoryLayout
                                           [ValueLayout/ADDRESS]))

        #_
        (FunctionDescriptor/of ValueLayout/ADDRESS
                               (into-array MemoryLayout
                                           [ValueLayout/ADDRESS
                                            ValueLayout/ADDRESS
                                            ValueLayout/ADDRESS
                                            ValueLayout/ADDRESS
                                            ValueLayout/ADDRESS
                                            ValueLayout/ADDRESS
                                            ValueLayout/ADDRESS]))

        ^MethodHandle method
        (.downcallHandle linker
                         mem-seg
                         func-descriptor
                         (into-array Linker$Option []))

        arena
        (Arena/ofConfined)

        ^List args [
                    (to-seq arena "host=localhost port=15432 dbname=test user=test password=test")

                    ;; (to-seq arena "127.0.0.1")
                    ;; (to-seq arena "15432")
                    ;; (to-seq arena "")
                    ;; (to-seq arena "")
                    ;; (to-seq arena "test")
                    ;; (to-seq arena "test")
                    ;; (to-seq arena "test")
                    ]

        ^MemorySegment conn
        (.invokeWithArguments method args)

        mem-seg-2
        (.get (.find lookup "PQsslInUse" #_ "PQhost" #_ "PQstatus"))

        func-descriptor-2
        (FunctionDescriptor/of #_ValueLayout/ADDRESS ValueLayout/JAVA_INT
                               (into-array MemoryLayout
                                           [ValueLayout/ADDRESS]))

        ^MethodHandle method-2
        (.downcallHandle linker
                         mem-seg-2
                         func-descriptor-2
                         (into-array Linker$Option []))

        ^List args [conn]

        ^MemorySegment result
        (.invokeWithArguments method-2 args)


        ]

    (println conn)
    (println result)
    #_
    (println (new String ^chars (.toArray result ValueLayout/JAVA_CHAR))))

  #_
  (println "Hello, World!"))
