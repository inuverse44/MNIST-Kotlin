## テスト（model）

### DenseVector (DV)

| TEST ID | KIND | FUNCTION NAME         | EXPLANATION                  |
|---------|------|-----------------------|------------------------------|
| DV001   | 正常系  | testVectorSize        | 入力したサイズとインスタンスのサイズのプロパティが等しい |
| DV002   | 正常系  | testVectorAdd         | ベクトルの和の計算が正しい                |
| DV003   | 正常系  | testVectorSubtract    | ベクトルの引き算が正しい                 |
| DV004   | 正常系  | testVectorScale       | ベクトルの定数倍が正しい                 |
| DV005   | 正常系  | testVectorDot         | ベクトルの内積が等しい                  |
| DV006   | 正常系  | testDimensionMismatch | サイズが等しくないベクトル同士の足し算が失敗する     |
| DV007   | 正常系  | testNormAndNormalize  | ベクトルのノルムと規格化が正しい             |
 
### DenseMatrix (DM)

| TEST ID | KIND | FUNCTION NAME              | EXPLANATION                             |
|---------|------|----------------------------|-----------------------------------------|
| DM001   | 正常系  | testMatrixRowsAndCols      | 入力した行数と列数に対し、インスタンスの行数・列数のプロパティがそれぞれ等しい |
| DM002   | 正常系  | testMatrixGet              | 行列のインデックスによる要素の取得が正しい                   |
| DM003   | 正常系  | testMatrixApply            | 行列のベクトルへの作用が等しい                         |
| DM004   | 正常系  | testMatrixApplyMismatch    | 行列の列数とベクトルの数が不整合である                     |
| DM005   | 正常系  | testMatrixAdd              | 行列の和の計算が正しい                             |
| DM006   | 正常系  | testMatrixAddMismatch      | サイズが等しくない行列同士の和が失敗する                    |
| DM007   | 正常系  | testMatrixSubtract         | 行列の差の計算が正しい                             |
| DM008   | 正常系  | testMatrixSubtractMismatch | サイズが等しくない行列同士の差が失敗する                    |
| DM009   | 正常系  | testMatrixMul              | 行列の積の計算が正しい                             |
| DM010   | 正常系  | testMatrixMulMismatch      | サイズが等しくない行列同士の積が失敗する                    |
| DM011   | 正常系  | testMatrixTranspose        | 行列の転置が正しい                               |
