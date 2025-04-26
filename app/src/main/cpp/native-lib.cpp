#include <jni.h>
#include <string>
#include "MatrixOperations.h"
#include "android/log.h"

#define LOG_TAG "NativeMatrixCalculator"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

// Convert Java double array to Eigen::MatrixXd
Eigen::MatrixXd jDoubleArrayToEigenMatrix(JNIEnv *env, jdoubleArray jArray, jint rows, jint cols) {
    jdouble *elements = env->GetDoubleArrayElements(jArray, NULL);
    Eigen::MatrixXd matrix(rows, cols);

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            matrix(i, j) = elements[i * cols + j];
        }
    }

    env->ReleaseDoubleArrayElements(jArray, elements, JNI_ABORT);
    return matrix;
}

// Convert Eigen::MatrixXd to Java double array
jdoubleArray eigenMatrixToJDoubleArray(JNIEnv *env, const Eigen::MatrixXd& matrix) {
    int rows = matrix.rows();
    int cols = matrix.cols();
    jdoubleArray result = env->NewDoubleArray(rows * cols);

    jdouble *elements = new jdouble[rows * cols];
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            elements[i * cols + j] = matrix(i, j);
        }
    }

    env->SetDoubleArrayRegion(result, 0, rows * cols, elements);
    delete[] elements;

    return result;
}

// JNI methods for matrix operations

JNIEXPORT jdoubleArray JNICALL
Java_com_example_matrixcalculator_NativeMatrixOperations_addMatrices(
        JNIEnv *env, jclass /* this */, jdoubleArray jMatrix1, jint rows1, jint cols1,
        jdoubleArray jMatrix2, jint rows2, jint cols2) {

    try {
        Eigen::MatrixXd matrix1 = jDoubleArrayToEigenMatrix(env, jMatrix1, rows1, cols1);
        Eigen::MatrixXd matrix2 = jDoubleArrayToEigenMatrix(env, jMatrix2, rows2, cols2);

        Eigen::MatrixXd result = MatrixOperations::add(matrix1, matrix2);
        return eigenMatrixToJDoubleArray(env, result);
    } catch (const std::exception& e) {
        LOGE("Error in addMatrices: %s", e.what());
        return NULL;
    }
}

JNIEXPORT jdoubleArray JNICALL
Java_com_example_matrixcalculator_NativeMatrixOperations_subtractMatrices(
        JNIEnv *env, jclass /* this */, jdoubleArray jMatrix1, jint rows1, jint cols1,
        jdoubleArray jMatrix2, jint rows2, jint cols2) {

    try {
        Eigen::MatrixXd matrix1 = jDoubleArrayToEigenMatrix(env, jMatrix1, rows1, cols1);
        Eigen::MatrixXd matrix2 = jDoubleArrayToEigenMatrix(env, jMatrix2, rows2, cols2);

        Eigen::MatrixXd result = MatrixOperations::subtract(matrix1, matrix2);
        return eigenMatrixToJDoubleArray(env, result);
    } catch (const std::exception& e) {
        LOGE("Error in subtractMatrices: %s", e.what());
        return NULL;
    }
}

JNIEXPORT jdoubleArray JNICALL
Java_com_example_matrixcalculator_NativeMatrixOperations_multiplyMatrices(
        JNIEnv *env, jclass /* this */, jdoubleArray jMatrix1, jint rows1, jint cols1,
        jdoubleArray jMatrix2, jint rows2, jint cols2) {

    try {
        Eigen::MatrixXd matrix1 = jDoubleArrayToEigenMatrix(env, jMatrix1, rows1, cols1);
        Eigen::MatrixXd matrix2 = jDoubleArrayToEigenMatrix(env, jMatrix2, rows2, cols2);

        Eigen::MatrixXd result = MatrixOperations::multiply(matrix1, matrix2);
        return eigenMatrixToJDoubleArray(env, result);
    } catch (const std::exception& e) {
        LOGE("Error in multiplyMatrices: %s", e.what());
        return NULL;
    }
}

JNIEXPORT jdoubleArray JNICALL
Java_com_example_matrixcalculator_NativeMatrixOperations_divideMatrices(
        JNIEnv *env, jclass /* this */, jdoubleArray jMatrix1, jint rows1, jint cols1,
        jdoubleArray jMatrix2, jint rows2, jint cols2) {

    try {
        Eigen::MatrixXd matrix1 = jDoubleArrayToEigenMatrix(env, jMatrix1, rows1, cols1);
        Eigen::MatrixXd matrix2 = jDoubleArrayToEigenMatrix(env, jMatrix2, rows2, cols2);

        Eigen::MatrixXd result = MatrixOperations::divide(matrix1, matrix2);
        return eigenMatrixToJDoubleArray(env, result);
    } catch (const std::exception& e) {
        LOGE("Error in divideMatrices: %s", e.what());
        return NULL;
    }
}

// Validation methods

JNIEXPORT jboolean JNICALL
Java_com_example_matrixcalculator_NativeMatrixOperations_canAddSubtract(
        JNIEnv *env, jclass /* this */, jint rows1, jint cols1, jint rows2, jint cols2) {

    return (rows1 == rows2) && (cols1 == cols2);
}

JNIEXPORT jboolean JNICALL
Java_com_example_matrixcalculator_NativeMatrixOperations_canMultiply(
        JNIEnv *env, jclass /* this */, jint rows1, jint cols1, jint rows2, jint cols2) {

    return cols1 == rows2;
}

JNIEXPORT jboolean JNICALL
Java_com_example_matrixcalculator_NativeMatrixOperations_canDivide(
        JNIEnv *env, jclass /* this */, jdoubleArray jMatrix2, jint rows2, jint cols2) {

    try {
        Eigen::MatrixXd matrix2 = jDoubleArrayToEigenMatrix(env, jMatrix2, rows2, cols2);
        return MatrixOperations::canDivide(matrix2);
    } catch (const std::exception& e) {
        LOGE("Error in canDivide: %s", e.what());
        return false;
    }
}

// Get result dimensions

JNIEXPORT jintArray JNICALL
Java_com_example_matrixcalculator_NativeMatrixOperations_getResultDimensions(
        JNIEnv *env, jclass /* this */, jint operation, jint rows1, jint cols1, jint rows2, jint cols2) {

    jintArray result = env->NewIntArray(2);
    jint dimensions[2];

    // Operation: 0=add, 1=subtract, 2=multiply, 3=divide
    switch (operation) {
        case 0:  // add
        case 1:  // subtract
            if ((rows1 == rows2) && (cols1 == cols2)) {
                dimensions[0] = rows1;
                dimensions[1] = cols1;
            } else {
                dimensions[0] = -1;
                dimensions[1] = -1;
            }
            break;
        case 2:  // multiply
            if (cols1 == rows2) {
                dimensions[0] = rows1;
                dimensions[1] = cols2;
            } else {
                dimensions[0] = -1;
                dimensions[1] = -1;
            }
            break;
        case 3:  // divide (matrix1 * inverse(matrix2))
            if (rows2 == cols2 && cols1 == rows2) {
                dimensions[0] = rows1;
                dimensions[1] = cols2;
            } else {
                dimensions[0] = -1;
                dimensions[1] = -1;
            }
            break;
        default:
            dimensions[0] = -1;
            dimensions[1] = -1;
    }

    env->SetIntArrayRegion(result, 0, 2, dimensions);
    return result;
}

} // extern "C"