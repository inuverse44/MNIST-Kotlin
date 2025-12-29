package inuverse.mnist.example

import kotlin.math.sqrt

/**
 * zipとmapの合わせ技挙動がわからないので試してみた例
 */
class ZipBehaviourExample {
    fun run() {
        val a = doubleArrayOf(1.0, 2.0, 3.0)
        val b = a.map {
            val t = sqrt(it)
            println(t)
            t * t * t
        }.toDoubleArray()
        println("a: ${a.contentToString()}")
        println("b: ${b.contentToString()}")

        val c = doubleArrayOf(1.0, 0.5, 1.0/3.0)
        val d = a.zip(c) // 型がList<Pair<Double, Double>>になっている
        println("d: $d") // [(1.0, 1.0), (2.0, 0.5), (3.0, 0.3333333333333333)]
        // リスト型なので、今回の設計思想的には使わない

        // クソ命名変数を使っている。実際には使うな
        val e: DoubleArray = a.zip(c).map { (it, ti) ->
            it * ti
        }.toDoubleArray()
        println("e: ${e.contentToString()}")
    }
}