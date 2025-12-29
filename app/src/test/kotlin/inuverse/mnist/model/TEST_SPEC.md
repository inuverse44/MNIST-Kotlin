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
| TEST ID | KIND | FUNCTION NAME         | EXPLANATION                  |
|---------|------|-----------------------|------------------------------|
| DM001   | 正常系  | testVectorSize        | 入力したサイズとインスタンスのサイズのプロパティが等しい |
| DM002   | 正常系  | testVectorAdd         | ベクトルの和の計算が正しい                |
| DM003   | 正常系  | testVectorSubtract    | ベクトルの引き算が正しい                 |
| DM004   | 正常系  | testVectorScale       | ベクトルの定数倍が正しい                 |
| DM005   | 正常系  | testVectorDot         | ベクトルの内積が等しい                  |
| DM006   | 正常系  | testDimensionMismatch | サイズが等しくないベクトル同士の足し算が失敗する     |
| DM007   | 正常系  | testNormAndNormalize  | ベクトルのノルムと規格化が正しい             |
