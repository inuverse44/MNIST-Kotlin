package inuverse.mnist.example

import inuverse.mnist.presentation.Plotter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class PlotDataExample {
    fun examplePlotLines() {
        val xs: List<Double> = (-100..100).map { it * 0.01 * PI } // -PI ~ PIを100等分
        val ys: List<Double> = xs.map { sin(it) }
        val ys2: List<Double> = xs.map { cos(it) }
        val ys3: List<Double> = xs.map { 2.0 * sin(it) * cos(it) }

        val plottedPoints = mapOf(
            "x" to xs,
            "y" to ys,
            "y2" to ys2,
            "y3" to ys3
        )

        Plotter().plotLines(xs, plottedPoints, "Neon Genesis Evangelion")
    }
}