package inuverse.mnist.presentation

import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomLine
import kotlin.collections.iterator
class Plotter {
    fun plotLines(
        xValues: List<Double>, 
        yMap: Map<String, List<Double>>,
        title: String = "title"
    ) {
        val colors = listOf("red", "blue", "green", "yellow", "black", "orange", "purple", "pink", "brown", "gray")
        var p = letsPlot(yMap)
        var i = 0
        for (entry in yMap) {
            if (entry.key == "x") continue
            
            p += geomLine(color = colors[i % colors.size]) {
                x = "x"
                y = entry.key
            }
            i++
        }
        p += ggtitle(title)
        ggsave(p, "sin_cos_plot.png")
    }

}