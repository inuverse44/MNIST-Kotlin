package inuverse.example.service

import inuverse.example.model.Mnist1DImage
import inuverse.example.model.MnistLabel


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