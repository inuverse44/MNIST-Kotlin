package inuverse.mnist.server.service

import inuverse.mnist.constants.MnistConst
import inuverse.mnist.neural.Network
import inuverse.mnist.neural.layer.Dense
import inuverse.mnist.neural.layer.ReLU
import inuverse.mnist.neural.layer.Softmax
import inuverse.mnist.neural.loss.CrossEntropy
import inuverse.mnist.neural.optimizer.StochasticGradientDescent
import inuverse.mnist.neural.spec.LayerFactory
import inuverse.mnist.neural.spec.ModelSpec
import inuverse.mnist.repository.DataLoadContext
import inuverse.mnist.repository.MnistImageLoadStrategyImpl
import inuverse.mnist.repository.MnistLabelLoadStrategyImpl
import inuverse.mnist.service.MnistDatasetService
import inuverse.mnist.service.MnistTrainer
import inuverse.mnist.service.ModelSaver
import inuverse.mnist.server.dto.TrainJobStatus
import inuverse.mnist.server.dto.TrainStartRequest
import org.slf4j.LoggerFactory
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class TrainingManager(
    private val modelPath: String,
    private val onModelReady: (Network) -> Unit
) {
    private val logger = LoggerFactory.getLogger(TrainingManager::class.java)
    private val executor = Executors.newSingleThreadExecutor()
    private val jobs = ConcurrentHashMap<String, TrainJobStatus>()

    fun startTraining(req: TrainStartRequest): String {
        val jobId = UUID.randomUUID().toString()
        jobs[jobId] = TrainJobStatus(jobId, state = "queued")
        executor.submit {
            try {
                jobs[jobId] = TrainJobStatus(jobId, state = "running")
                val network = runTrainingJob(req) { epoch, loss, acc ->
                    jobs[jobId] = TrainJobStatus(jobId, state = "running", epoch = epoch, loss = loss, accuracy = acc)
                }
                // Save and hot-swap
                ModelSaver().save(network, req.save?.path ?: modelPath)
                onModelReady(network)
                jobs[jobId] = TrainJobStatus(jobId, state = "completed")
            } catch (e: Exception) {
                logger.error("Training job failed: ${e.message}", e)
                jobs[jobId] = TrainJobStatus(jobId, state = "failed", message = e.message)
            }
        }
        return jobId
    }

    fun getStatus(jobId: String): TrainJobStatus? = jobs[jobId]

    private fun runTrainingJob(
        req: TrainStartRequest,
        onProgress: (epoch: Int, loss: Double, accuracy: Double) -> Unit
    ): Network {
        // 1. Load dataset
        val baseDir = if (File("app").exists()) "app/" else ""
        val images = DataLoadContext(MnistImageLoadStrategyImpl()).load("${baseDir}t10k-images.idx3-ubyte")
        val labels = DataLoadContext(MnistLabelLoadStrategyImpl()).load("${baseDir}t10k-labels.idx1-ubyte")
        val datasetService = MnistDatasetService(images, labels)

        // 2. Build network
        val network = if (!req.layers.isNullOrEmpty()) {
            LayerFactory.buildNetwork(ModelSpec(version = "1", layers = req.layers), learningRate = req.config.learningRate)
        } else {
            val net = Network(CrossEntropy(), StochasticGradientDescent(req.config.learningRate))
            net.add(Dense(MnistConst.MNIST_INPUT_SIZE, req.config.hiddenLayerSize ?: 100))
            net.add(ReLU())
            net.add(Dense(req.config.hiddenLayerSize ?: 100, 10))
            net.add(Softmax())
            net
        }

        // 3. Train
        val all = datasetService.getAllDataset().shuffled()
        val train = all.take(req.config.trainSize)
        val test = all.drop(req.config.trainSize).take(req.config.testSize)
        val trainer = MnistTrainer(network, train, test)
        trainer.trainWithCallback(req.config.epochs) { epoch, loss, acc ->
            onProgress(epoch, loss, acc)
        }
        return network
    }
}
