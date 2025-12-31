package inuverse.mnist.service

import inuverse.mnist.model.Mnist1DImage
import inuverse.mnist.model.MnistLabel
import inuverse.mnist.model.Vector
import inuverse.mnist.model.DenseVector


/**
 * 📝
 * Service層の役割はアプリケーションの機能（ユースケース）を提供すること
 * e.g., データセットを読み込む、学習を開始する、推論を実行する
 * つまり、「手順」を管理するパッケージらしいんやで
 */

/**
 * TODO: Mnistのデータセットに依存させたくないなあ
 */
class MnistDatasetService(
    val mnistImages: Array<Mnist1DImage>,
    val mnistLabels: Array<MnistLabel>
) {
    /**
     * こいつはいま、784成分ベクトルと10成分ベクトルのペアなんやで
     */
    data class DataPair(val input: Vector, val label: Vector)

    /**
     * 📝
     * いまのモデルは数学関数に特化している。ニューラルネットワークのデータを入れたくないんだよね。
     * どっかでリファクタリングせんといかん。ムズカシ。
     */
    private val dataset: List<DataPair>

    /**
     * 📝
     * 最初に変換してメモリに載せておくことで、学習中はここから取り出すだけで済み高速になる。
     * もし、メモリに載せられないくらいになってきた場合にはその都度読み込む方式にしないといけん。
     */
    init {
        dataset = mnistImages.zip(mnistLabels).map { (img, lbl) ->
            DataPair(
                input = normalize(img),
                label = toOneHot(lbl.value)
            )
        }
    }

    /**
     * 📝
     * DataPairというオブジェクトに格納しているので、Arrayを使う旨みがない。
     * むしろここではListを使うことで、shuffleやミニバッチにすることが容易になる。
     */
    fun getAllDataset(): List<DataPair> {
        return dataset
    }

    fun getSplitDatasets(
        trainRatio: Double = 0.8 // 典型的には80%のデータを訓練データ、20%をテストデータにする
    ): Pair<List<DataPair>, List<DataPair>> {
        val trainSize = (dataset.size * trainRatio).toInt()
        val trainData = dataset.take(trainSize)
        val testData = dataset.drop(trainSize)
        return Pair(trainData, testData)
    }

    /**
     * Mnistの画像は輝度が0~255のIntで存在している。
     * これは学習するときに、expの引数に入れる瞬間があるので、計算機的に問題がある。
     * そこで、[0~255]を[0~1]の間にmapする
     * TODO: 255.0はハードコードされている。カス。
     */
    private fun normalize(mnist1DImage: Mnist1DImage): DenseVector {
        val doubleArray = mnist1DImage.image.map { it.toDouble() / 255.0 }.toDoubleArray()
        return DenseVector(doubleArray.size, doubleArray)
    }

    /**
     * ラベルをone-shot表現へ変換する
     * e.g.,
     * 0 <--> [1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
     * 1 <--> [0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
     * 2 <--> [0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
     * ...
     * 9 <--> [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0]
     */
    private fun toOneHot(label: Int): Vector {
        require(label in 0..9) {
            "Out of range: label: Input label is $label. label should be between 0 and 9"
        }
        val onehotLength = 10
        val result = DoubleArray(onehotLength)
        for(i in 0 until onehotLength) {
            result[i] = if (i == label) 1.0 else 0.0
        }
        return DenseVector(onehotLength, result)
    }
}