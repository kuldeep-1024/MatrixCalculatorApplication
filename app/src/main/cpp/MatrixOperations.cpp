
#include "MatrixOperations.h"
#include "stdexcept"


Eigen::MatrixXd MatrixOperations::add(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2) {
    if (!canAddSubtract(matrix1, matrix2)) {
        throw std::invalid_argument("Matrices must have the same dimensions for addition");
    }
    return matrix1 + matrix2;
}

Eigen::MatrixXd MatrixOperations::subtract(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2) {
    if (!canAddSubtract(matrix1, matrix2)) {
        throw std::invalid_argument("Matrices must have the same dimensions for subtraction");
    }
    return matrix1 - matrix2;
}

Eigen::MatrixXd MatrixOperations::multiply(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2) {
    if (!canMultiply(matrix1, matrix2)) {
        throw std::invalid_argument("Number of columns in first matrix must equal number of rows in second matrix for multiplication");
    }
    return matrix1 * matrix2;
}

Eigen::MatrixXd MatrixOperations::divide(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2) {
    if (!canDivide(matrix2)) {
        throw std::invalid_argument("Second matrix must be square and invertible for division");
    }

    Eigen::MatrixXd inverse = matrix2.inverse();

    if (!canMultiply(matrix1, inverse)) {
        throw std::invalid_argument("Matrix dimensions not compatible for division");
    }

    return matrix1 * inverse;
}

bool MatrixOperations::canAddSubtract(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2) {
    return (matrix1.rows() == matrix2.rows()) && (matrix1.cols() == matrix2.cols());
}

bool MatrixOperations::canMultiply(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2) {
    return matrix1.cols() == matrix2.rows();
}

bool MatrixOperations::canDivide(const Eigen::MatrixXd& matrix2) {
    // For division, matrix2 must be square and invertible
    if (matrix2.rows() != matrix2.cols()) {
        return false;
    }

    // Check if determinant is close to zero
    double det = matrix2.determinant();
    return std::abs(det) > 1e-10;  // Threshold for numerical stability
}
