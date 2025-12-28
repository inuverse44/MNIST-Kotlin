package inuverse.example.model

class DenseMatrix(
    override val rows: Int,
    override val cols: Int,
    private val data: DoubleArray
): Matrix {

    override val inputDim: Int get() = cols
    override val outputDim: Int get() = rows

    /**
     * 📝
     * getメソッドをoverrideする次のような書き方が推奨されるようになる
     *
     * cols=2, rows=2, dataA=doubleArrayOf(1.0, 2.0, 3.0, 4.0)
     * A = DenseMatrix(cols, rows, dataA)
     * とすると、
     * A[0, 0] = 1.0
     * A[1, 0] = 3.0
     * A[0, 1] = 2.0
     * A[1, 1] = 4.0
     * のように書ける
     */
    override fun get(i: Int, j: Int): Double {
        return data[i * cols + j]
    }

    override fun apply(x: Vector): Vector {
        require(x.size == cols) {
            "Dimension mismatch: matrix cols=$cols, vector size=${x.size}"
        }
        TODO("Not yet implemented")
    }

    override fun add(other: Matrix): Matrix {
        require(cols == other.rows && cols == other.cols) {
            "Dimension mismatch: ($rows x $cols) + (${other.rows} x ${other.cols})"
        }
        val result = DoubleArray(cols * rows)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val index = i * cols + j
                result[index] = this[i, j] + other[i, j]
            }
        }
        return DenseMatrix(rows, other.cols, result)
    }

    /**
     * 行列AとBの積
     * 成分で考え、Einsteinの縮約規則を用いると
     * (AB)_ij = A_ik B_kj
     * となる。
     * なので、
     *      行列のサイズはthis.row x other.col
     *      行列のインデックスはi * other.cols * j
     * となる。
     */
    override fun mul(other: Matrix): Matrix {
        require(cols == other.rows) {
            "Dimension mismatch: ($rows x $cols) x (${other.rows} x ${other.cols})"
        }
        val result = DoubleArray(rows * other.cols)
        for (i in 0 until rows) {
            for (j in 0 until other.cols) {
                var sum=0.0
                val index = i * other.cols + j
                for (k in 0 until cols) {
                     sum += this[i, k] * other[k, j]
                }
                result[index] = sum
            }
        }
        return DenseMatrix(rows, other.cols, result)
    }

    /**
     * 転置は
     * (A_ij)^T = A_ji
     * である。サイズがmxn (rows x cols)であれば、
     * 出力のサイズはnxm (cols x rows)になることに注意する。
     * 基本的に新しい行列の形でループを回すことを心がける。
     */
    override fun transpose(): Matrix {
        val result = DoubleArray(cols * rows)
        for(i in 0 until cols) {
            for(j in 0 until rows) {
                val index = i * rows + j
                result[index] = this[j, i]
            }
        }
        return DenseMatrix(cols, rows, result)
    }

    /**
     * printlnするときに、行列の形に整形して表示したいぬ🐶
     */
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("\n")
        for (i in 0 until rows) {
            sb.append("[")
            for (j in 0 until cols) {
                sb.append(this[i, j])
                if (j < cols - 1) sb.append(", ")
            }
            sb.append("]\n")
        }
        return sb.toString()
    }

}