## テスト（損失関数）

### MeanSquaredError (MSE)
二乗平均誤差関数のテスト

| TEST ID | KIND | FUNCTION NAME | EXPLANATION |
|---|---|---|---|
| MSE001 | 正常系 | testForward | 予測値と正解値から、二乗誤差平均 $E = \frac{1}{2N} \sum (y-t)^2$ が正しく計算される |
| MSE002 | 正常系 | testBackward | 逆伝播時の勾配 $\frac{\partial E}{\partial y} = \frac{1}{N} (y-t)$ が正しく計算される |
| MSE003 | 正常系 | testZeroLoss | 予測値と正解値が完全一致する場合、損失値および勾配が共に 0 になる |
