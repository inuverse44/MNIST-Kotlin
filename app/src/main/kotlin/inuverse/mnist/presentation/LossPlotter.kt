package inuverse.mnist.presentation

import inuverse.mnist.service.MnistTrainer
import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.scale.scaleYContinuous

class LossPlotter {
    fun plot(history: List<MnistTrainer.TrainingHistory>, filename: String = "training_history.png") {
        val epochs = history.map { it.epoch }
        val losses = history.map { it.loss }
        val accuracies = history.map { it.accuracy }

        // Loss Plot
        val lossData = mapOf(
            "epoch" to epochs,
            "loss" to losses
        )
        
        val lossPlot = letsPlot(lossData) +
                geomLine(color = "red", size = 1.0) { x = "epoch"; y = "loss" } +
                ggtitle("Training Loss") +
                ggsize(600, 400)

        // Accuracy Plot
        val accData = mapOf(
            "epoch" to epochs,
            "accuracy" to accuracies
        )
        
        val accPlot = letsPlot(accData) +
                geomLine(color = "blue", size = 1.0) { x = "epoch"; y = "accuracy" } +
                ggtitle("Test Accuracy") +
                scaleYContinuous(limits = 0.0 to 1.0) +
                ggsize(600, 400)

        // 別々に保存
        ggsave(lossPlot, "loss_plot.png")
        ggsave(accPlot, "accuracy_plot.png")
        
        println("📊 Graphs saved: loss_plot.png, accuracy_plot.png")
    }
}
