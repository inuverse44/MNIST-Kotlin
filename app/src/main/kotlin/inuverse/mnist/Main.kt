package inuverse.mnist

import inuverse.mnist.repository.MnistImageLoadStrategyImpl
import inuverse.mnist.repository.MnistLabelLoadStrategyImpl
import inuverse.mnist.repository.DataLoadContext
import inuverse.mnist.service.MnistDatasetService
import inuverse.mnist.service.MnistTrainer
import inuverse.mnist.neural.Network
import inuverse.mnist.neural.layer.Dense
import inuverse.mnist.neural.layer.ReLU
import inuverse.mnist.neural.layer.Softmax
import inuverse.mnist.neural.loss.CrossEntropy
import inuverse.mnist.neural.optimizer.StochasticGradientDescent
import inuverse.mnist.presentation.LossPlotter
import kotlin.random.Random

fun main() {
    println("🐶 Inuverse: MNIST Learning System 🐶")

    // --- 1. Configuration ---
    val trainSize = 5000    // 学習データ数
    val testSize = 1000      // テストデータ数
    val epochs = 50         // エポック数
    val learningRate = 0.01 // 学習率
    val hiddenSize = 100    // 隠れ層のニューロン数

    // --- 2. Load Data ---
    println("Loading Dataset...")
    val imageContext = DataLoadContext(MnistImageLoadStrategyImpl())
    val labelContext = DataLoadContext(MnistLabelLoadStrategyImpl())
    
    val mnistImages = imageContext.load("t10k-images.idx3-ubyte")
    val mnistLabels = labelContext.load("t10k-labels.idx1-ubyte")

    val service = MnistDatasetService(mnistImages, mnistLabels)
    val allData = service.getAllDataset().shuffled(Random(123))

    // データを分割
    val trainData = allData.take(trainSize)
    val testData = allData.drop(trainSize).take(testSize)

    // --- 3. Build Network ---
    println("Building Network...")
    val network = Network(
        loss = CrossEntropy(),
        optimizer = StochasticGradientDescent(learningRate)
    )

    // Layer構成: 784 -> 100 (ReLU) -> 10 (Softmax)
    network.add(Dense(784, hiddenSize))
    network.add(ReLU())
    network.add(Dense(hiddenSize, 10))
    network.add(Softmax())

    // --- 4. Training ---
    val trainer = MnistTrainer(network, trainData, testData)
    val history = trainer.train(epochs)
    
    // --- 5. Visualization ---
    println("\nGenerating Training Graphs...")
    LossPlotter().plot(history)
    
    // --- 6. Final Evaluation ---
    println("\nFinal Evaluation on Test Data...")
    val finalAccuracy = trainer.evaluate(testData)
    println("Test Accuracy: %.2f%%\n".format(finalAccuracy * 100))
}
