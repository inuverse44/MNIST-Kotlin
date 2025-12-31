package inuverse.mnist

import inuverse.mnist.model.TrainingConfig
import inuverse.mnist.repository.DataLoadContext
import inuverse.mnist.repository.MnistImageLoadStrategyImpl
import inuverse.mnist.repository.MnistLabelLoadStrategyImpl
import inuverse.mnist.service.MnistDatasetService
import inuverse.mnist.service.MnistLearningService
import inuverse.mnist.server.MnistServer
import java.io.File
import java.util.Scanner
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Main")

fun main(args: Array<String>) {
    logger.info("🐶 Inuverse: MNIST Learning System 🐶")
    val modelPath = System.getenv("MODEL_PATH") ?: "mnist_model.json"
    
    // コマンドライン引数があればそれを使う
    val modeFromArgs = args.firstOrNull()

    val input = if (modeFromArgs != null) {
        logger.info("Mode selected via arguments: $modeFromArgs")
        modeFromArgs
    } else {
        val scanner = Scanner(System.`in`)
        logger.info("Select mode:")
        logger.info("1. Train Model")
        logger.info("2. Start Web Server (UI)")
        logger.info("3. Train & Start Server")
        print("> ")
        
        // 入力がある場合はそれを使い、ない場合はデフォルト挙動
        if (scanner.hasNextLine()) {
            scanner.nextLine().trim()
        } else {
            logger.warn("No input detected. Falling back to default...")
            if (File(modelPath).exists()) "2" else "3"
        }
    }

    when (input) {
        "1" -> train(modelPath)
        "2" -> startServer(modelPath)
        "3" -> {
            train(modelPath)
            startServer(modelPath)
        }
        else -> {
            logger.warn("Invalid option. Starting server by default...")
            startServer(modelPath)
        }
    }
}

fun train(modelPath: String) {
    logger.info("--- Starting Training ---")
    // --- 1. Load Data Source ---
    logger.info("Loading Raw Dataset...")
    val imageContext = DataLoadContext(MnistImageLoadStrategyImpl())
    val labelContext = DataLoadContext(MnistLabelLoadStrategyImpl())
    
    val baseDir = if (File("app").exists()) "app/" else ""
    val mnistImages = imageContext.load("${baseDir}t10k-images.idx3-ubyte")
    val mnistLabels = labelContext.load("${baseDir}t10k-labels.idx1-ubyte")
    
    // --- 2. Setup Services ---
    val datasetService = MnistDatasetService(mnistImages, mnistLabels)
    val learningService = MnistLearningService(datasetService)

    // --- 3. Configuration ---
    val config = TrainingConfig(
        trainSize = 5000, // Reduced for quick demo if needed, but keeping original default or slightly smaller?
        testSize = 1000,
        epochs = 20,      // Reduced from 20 to 10 for faster startup in demo
        learningRate = 0.01,
        hiddenLayerSize = 100
    )

    // --- 4. Execute Training ---
    learningService.runTraining(config)
    learningService.runInferenceDemo(config, modelPath)
}

fun startServer(modelPath: String) {
    logger.info("--- Starting Server ---")
    val server = MnistServer(modelPath)
    server.start()
}
