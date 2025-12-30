## テスト（層）

### Denes (D)
全結合層のテスト

| TEST ID | KIND | FUNCTION NAME         | EXPLANATION                                                |
|---------|------|-----------------------|------------------------------------------------------------|
| DE001   | 正常系  | testInitialization        | 初期化時に重みとバイアスの型が正しい                                         |
| DE002   | 正常系  | testForwardWithFixedSeed         | シード値を固定して、2つの同じ順伝播の結果が正しい                                  |
| DE003   | 正常系  | testBackward    | シード値を固定して、入出力の勾配のサイズ、計算結果がnullでないこと、バイアスの勾配と出力の勾配が等しいことの確認 |

### Sigmoid (SG)
活性化関数であるsigmoid関数のテスト

| TEST ID | KIND | FUNCTION NAME         | EXPLANATION                      |
|---------|------|-----------------------|----------------------------------|
| SG001   | 正常系  | testForward        | シグモイド関数がベクトルの各要素に作用して、正しい値を出力している |
| SG002   | 正常系  | testBackward         | シグモイド関数の逆伝播が正しい                  |
| SG003   | 正常系  | testBackwardValues    | シグモイド関数の値のロジックが正しい               |
