package com.example.matrixcalculator

import android.util.Log
import java.lang.Exception

class MatrixUtils {
    companion object {
        private const val TAG = "MatrixUtils"
        const val OPERATION_ADD = 0
        const val OPERATION_SUBTRACT = 1
        const val OPERATION_MULTIPLY = 2
        const val OPERATION_DIVIDE = 3

        // Convert a 2D array of strings to a flattened DoubleArray
        private fun flatten(matrix: Array<DoubleArray>, rows: Int, cols: Int): DoubleArray {
            val result = DoubleArray(rows * cols)
            try {
                for (i in 0 until rows) {
                    for (j in 0 until cols) {

                        result[i * cols + j] = matrix[i][j]
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error converting to double array: ${e.message}")
            }
            return result
        }

//        // Convert a flattened DoubleArray back to a 2D array
//        fun convertToMatrix(array: DoubleArray, rows: Int, cols: Int): Array<Array<Double>> {
//            val result = Array(rows) { Array(cols) { 0.0 } }
//            try {
//                for (i in 0 until rows) {
//                    for (j in 0 until cols) {
//                        result[i][j] = array[i * cols + j]
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Error converting to matrix: ${e.message}")
//            }
//            return result
//        }

        // Perform matrix operation
        fun performOperation(
            operation: Int,
            matrix1: Array<DoubleArray>, rows1: Int, cols1: Int,
            matrix2: Array<DoubleArray>, rows2: Int, cols2: Int
        ): OperationResult {
            try {
                // Check if operation is valid
                val resultDimensions = NativeMatrixOperations.getResultDimensions(
                    operation, rows1, cols1, rows2, cols2
                )

                if (resultDimensions[0] < 0 || resultDimensions[1] < 0) {
                    return OperationResult(false, "Invalid dimensions for the selected operation", null, 0, 0)
                }

//                // Convert input matrices to flattened arrays
//                val matrix1 = convertToDoubleArray(matrix1Values, rows1, cols1)
//                val matrix2 = convertToDoubleArray(matrix2Values, rows2, cols2)

                // Additional check for division
                if (operation == OPERATION_DIVIDE) {
                    val flatB = flatten(matrix2, rows2, cols2)
                    if (!NativeMatrixOperations.canDivide(flatB, rows2, cols2)) {
                        return OperationResult(
                            false,
                            "Matrix B is not invertible",
                            null, 0, 0
                        )
                    }
                }

                // Perform the operation

                val flatA = flatten(matrix1, rows1, cols1)
                val flatB = flatten(matrix2, rows2, cols2)

                val resultArray = when (operation) {
                    OPERATION_ADD -> NativeMatrixOperations.addMatrices(
                        flatA, rows1, cols1, flatB, rows2, cols2
                    )
                    OPERATION_SUBTRACT -> NativeMatrixOperations.subtractMatrices(
                        flatA, rows1, cols1, flatB, rows2, cols2
                    )
                    OPERATION_MULTIPLY -> NativeMatrixOperations.multiplyMatrices(
                        flatA, rows1, cols1, flatB, rows2, cols2
                    )
                    OPERATION_DIVIDE -> NativeMatrixOperations.divideMatrices(
                        flatA, rows1, cols1, flatB, rows2, cols2
                    )
                    else -> return OperationResult(
                        false,
                        "Unknown operation",
                        null, 0, 0
                    )
                }

                val resultRows = resultDimensions[0]
                val resultCols = resultDimensions[1]
//                val resultMatrix = convertToMatrix(resultArray, resultRows, resultCols)
                val resultMatrix = Array(resultRows) { DoubleArray(resultCols) }
                for (i in 0 until resultRows) {
                    for (j in 0 until resultCols) {
                        resultMatrix[i][j] = resultArray[i * resultCols + j]
                    }
                }

                return OperationResult(
                    true,
                    "Operation successful",
                    resultMatrix,
                    resultRows,
                    resultCols
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error performing operation: ${e.message}")
                return OperationResult(false, "Error: ${e.message}", null, 0, 0)
            }
        }
    }

    // data class to hold operation results
    data class OperationResult(
        val success: Boolean,
        val message: String,
        val result: Array<DoubleArray>?,
        val rows: Int,
        val cols: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as OperationResult

            if (success != other.success) return false
            if (rows != other.rows) return false
            if (cols != other.cols) return false
            if (message != other.message) return false
            if (!result.contentDeepEquals(other.result)) return false

            return true
        }

        override fun hashCode(): Int {
            var result1 = success.hashCode()
            result1 = 31 * result1 + rows
            result1 = 31 * result1 + cols
            result1 = 31 * result1 + message.hashCode()
            result1 = 31 * result1 + (result?.contentDeepHashCode() ?: 0)
            return result1
        }
    }
}