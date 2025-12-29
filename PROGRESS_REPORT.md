# MNIST Kotlin Project Progress Report

日付: 2025年12月29日
現状: ニューラルネットワークの主要な構成要素（データロード、数学演算、層、損失関数）の実装が完了し、学習ループの構築に向けた準備が整いつつある状態。

## ✅ 実装済み (Implemented)

### 1. 数学・モデル基盤 (`inuverse.example.model`)
ニューラルネットワークの計算に必要な線形代数ライブラリをフルスクラッチで実装。
- **Vector / DenseVector**:
  - 四則演算（加算 `add`、減算 `subtract`、スカラー倍 `scale`）
  - 内積 (`dot`)
  - **外積 (`outerProduct`)**: 誤差逆伝播法での重み勾配計算に必須。
  - ノルム (`norm`)、正規化 (`normalize`)
- **Matrix / DenseMatrix**:
  - 行列演算（加算 `add`、乗算 `mul`）
  - ベクトルへの適用 (`apply`): $Ax$ の計算
  - 転置 (`transpose`)

### 2. データセット管理 (`inuverse.example.service`, `repository`)
MNISTデータの読み込みと前処理パイプライン。
- **MnistImageLoadStrategy / MnistLabelLoadStrategy**: バイナリファイルからの生データ読み込み。
- **MnistDatasetService**:
  - データの正規化 (0-255 -> 0.0-1.0)
  - ラベルのOne-Hotエンコーディング (e.g. 5 -> `[0, 0, 0, 0, 0, 1, 0, 0, 0, 0]`)
  - `getAllDataset()` / `getSplitDatasets()`: 学習用/テスト用データの提供。

### 3. ニューラルネットワーク構成要素 (`inuverse.example.neural`)
- **Loss (損失関数)**:
  - `MeanSquaredError`: 二乗平均誤差。Forwardで損失値、Backwardで勾配 $\frac{1}{N}(y-t)$ を計算。
- **Layer (層)**:
  - `Dense` (全結合層):
    - Forward: $y = Wx + b$
    - Backward: 
      - 入力勾配: $W^T \delta$
      - 重み勾配: $\delta x^T$ (外積を使用)
      - バイアス勾配: $\delta$
    - 初期化: Randomを使用した重みのランダム初期化 (-1.0 ~ 1.0)。

---

## 🚀 次のステップ (TODO)

### 1. 活性化関数の実装 (`neural.layer`)
非線形性を導入するために必須。`Layer` インターフェースを実装する形で作成する。
- [ ] **Sigmoid**: $y = \frac{1}{1 + e^{-x}}$
- [ ] **ReLU**: $y = \max(0, x)$

### 2. 最適化手法の実装 (`neural.optimizer`)
計算された勾配を使って、実際に重みとバイアスを更新するロジック。
- [ ] **Optimizer インターフェース**: `update(layer: Layer)` のようなメソッドを定義。
- [ ] **SGD (Stochastic Gradient Descent)**: $W \leftarrow W - \eta \frac{\partial L}{\partial W}$

### 3. ネットワーク構築と学習ループ
- [ ] **Network クラス**: 複数の `Layer` をリストで保持し、順番に Forward/Backward を実行するコンテナ。
- [ ] **学習ループ (Main)**:
  1. ミニバッチを取り出す。
  2. NetworkにForwardさせる。
  3. Lossを計算する。
  4. NetworkにBackwardさせる。
  5. Optimizerでパラメータを更新する。
  6. 定期的に精度(Accuracy)を計測する。

### 4. 評価と可視化
- [ ] テストデータを使用した最終的な精度評価。
- [ ] 学習曲線のプロット（Lossの推移など）。
