package inuverse.mnist

import inuverse.mnist.model.TrainingConfig
import inuverse.mnist.repository.DataLoadContext
import inuverse.mnist.repository.MnistImageLoadStrategyImpl
import inuverse.mnist.repository.MnistLabelLoadStrategyImpl
import inuverse.mnist.service.MnistDatasetService
import inuverse.mnist.service.MnistLearningService

fun main() {
    println("🐶 Inuverse: MNIST Learning System 🐶")

    // --- 1. Load Data Source ---
    println("Loading Raw Dataset...")
    val imageContext = DataLoadContext(MnistImageLoadStrategyImpl())
    val labelContext = DataLoadContext(MnistLabelLoadStrategyImpl())
    
    val mnistImages = imageContext.load("t10k-images.idx3-ubyte")
    val mnistLabels = labelContext.load("t10k-labels.idx1-ubyte")
    
    // --- 2. Setup Services ---
    val datasetService = MnistDatasetService(mnistImages, mnistLabels)
    val learningService = MnistLearningService(datasetService)

    // --- 3. Configuration ---
    val config = TrainingConfig(
        trainSize = 5000,
        testSize = 1000,
        epochs = 20,
        learningRate = 0.01,
        hiddenLayerSize = 100
    )

    // --- 4. Execute Training ---
    learningService.runTraining(config)
    
    // --- 5. Execute Inference Demo (Loading from JSON) ---
    learningService.runInferenceDemo(config, "mnist_model.json")
}
