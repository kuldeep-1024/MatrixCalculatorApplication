
#ifndef MATRIXCALCULATOR_MATRIXOPERATIONS_H
#define MATRIXCALCULATOR_MATRIXOPERATIONS_H

#include "vector"
#include "string"
#include "eigen/Eigen/Dense"

class MatrixOperations {
public:
    // Matrix addition
    static Eigen::MatrixXd add(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2);

    // Matrix subtraction
    static Eigen::MatrixXd subtract(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2);

    // Matrix multiplication
    static Eigen::MatrixXd multiply(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2);

    // Matrix division (matrix1 * inverse(matrix2))
    static Eigen::MatrixXd divide(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2);

    // Check if matrices can be added/subtracted
    static bool canAddSubtract(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2);

    // Check if matrices can be multiplied
    static bool canMultiply(const Eigen::MatrixXd& matrix1, const Eigen::MatrixXd& matrix2);

    // To check if matrix2 is invertible (needed for division)
    static bool canDivide(const Eigen::MatrixXd& matrix2);
};


#endif //MATRIXCALCULATOR_MATRIXOPERATIONS_H
