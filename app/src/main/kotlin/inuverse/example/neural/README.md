## Neural Network Package
このパッケージにはな、ニューラルネットワークに責務を置くんや。
線形代数とは分離。


### ディレクトリ構成

```
./neural/
    layer/
        Layer.kt # インターフェイス
        Dense.kt # 全結合層
    loss/
        Loss.kt # インターフェイス
        MeanSquaredError.kt # 二乗平均誤差
        CrossEntropy.kt # 交差エントロピー誤差
    optimizer/
        Optimizer.kt # インターフェイス
        StochasticGradientDecent.kt # 確率的勾配降下法。だるそう。
        Adam.kt # 今回は無理ぽ 
```