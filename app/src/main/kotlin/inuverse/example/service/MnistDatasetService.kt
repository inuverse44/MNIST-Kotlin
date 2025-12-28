package inuverse.example.service

import inuverse.example.model.Mnist1DImage
import inuverse.example.model.MnistLabel


/**
 * TODO: Mnistのデータセットに依存させたくないなあ
 */
class MnistDatasetService(
    val mnistImages: Array<Mnist1DImage>,
    val mnistLabels: Array<MnistLabel>
) {

    /**
     * ラベルをone-shot表現へ変換する
     * e.g.,
     * 0 <--> [1, 0, 0, 0, 0, 0, 0, 0, 0, 0]
     * 1 <--> [0, 1, 0, 0, 0, 0, 0, 0, 0, 0]
     * 2 <--> [0, 0, 1, 0, 0, 0, 0, 0, 0, 0]
     * ...
     * 9 <--> [0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
     */
    private fun convertOnehot(label: Int): Array<Int> {
        TODO()
    }
}