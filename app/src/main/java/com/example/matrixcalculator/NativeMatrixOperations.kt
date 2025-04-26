package com.example.matrixcalculator

class NativeMatrixOperations {
    companion object {
        // loading the native library using project package name
        init {
            System.loadLibrary("matrixcalculator")
        }

        // Native method declarations
        @JvmStatic external fun addMatrices(matrix1: DoubleArray, rows1: Int, cols1: Int,
                                             matrix2: DoubleArray, rows2: Int, cols2: Int): DoubleArray

        @JvmStatic external fun subtractMatrices(matrix1: DoubleArray, rows1: Int, cols1: Int,
                                      matrix2: DoubleArray, rows2: Int, cols2: Int): DoubleArray

        @JvmStatic external fun multiplyMatrices(matrix1: DoubleArray, rows1: Int, cols1: Int,
                                      matrix2: DoubleArray, rows2: Int, cols2: Int): DoubleArray

        @JvmStatic external fun divideMatrices(matrix1: DoubleArray, rows1: Int, cols1: Int,
                                    matrix2: DoubleArray, rows2: Int, cols2: Int): DoubleArray

        // checker methods
        @JvmStatic external fun canAddSubtract(rows1: Int, cols1: Int, rows2: Int, cols2: Int): Boolean
        @JvmStatic external fun canMultiply(rows1: Int, cols1: Int, rows2: Int, cols2: Int): Boolean
        @JvmStatic external fun canDivide(matrix2: DoubleArray, rows2: Int, cols2: Int): Boolean

        // result dimensions method
        @JvmStatic external fun getResultDimensions(operation: Int, rows1: Int, cols1: Int,
                                         rows2: Int, cols2: Int): IntArray
    }
}