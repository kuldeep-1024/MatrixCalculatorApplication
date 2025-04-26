package com.example.matrixcalculator

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    // UI elements
    private lateinit var etMatrixARows: EditText
    private lateinit var etMatrixACols: EditText
    private lateinit var etMatrixBRows: EditText
    private lateinit var etMatrixBCols: EditText
    private lateinit var btnCreateMatrixA: Button
    private lateinit var btnCreateMatrixB: Button
    private lateinit var btnCalculate: Button

    //    private lateinit var matrixAContainer: LinearLayout
//    private lateinit var matrixBContainer: LinearLayout
    private lateinit var rvMatrixAContainer: RecyclerView
    private lateinit var rvMatrixBContainer: RecyclerView
    private lateinit var resultMatrixContainer: LinearLayout
    private lateinit var rgOperations: RadioGroup
    private lateinit var tvResultDimensions: TextView
    private lateinit var tvResultStatus: TextView

    //Adapters

    private lateinit var matrixAAdapter: MatrixAdapter
    private lateinit var matrixBAdapter: MatrixAdapter

//    // Matrix input fields
//    private var matrixAInputs: Array<Array<EditText>> = arrayOf()
//    private var matrixBInputs: Array<Array<EditText>> = arrayOf()

    // Matrix dimensions
    private var matrixARows = 0
    private var matrixACols = 0
    private var matrixBRows = 0
    private var matrixBCols = 0

    // Decimal formatter for output
    private val decimalFormat = DecimalFormat("#.####")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        etMatrixARows = findViewById(R.id.etMatrixARows)
        etMatrixACols = findViewById(R.id.etMatrixACols)
        etMatrixBRows = findViewById(R.id.etMatrixBRows)
        etMatrixBCols = findViewById(R.id.etMatrixBCols)
        btnCreateMatrixA = findViewById(R.id.btnCreateMatrixA)
        btnCreateMatrixB = findViewById(R.id.btnCreateMatrixB)
        btnCalculate = findViewById(R.id.btnCalculate)
        rvMatrixAContainer = findViewById(R.id.rvMatrixAContainer)
        rvMatrixBContainer = findViewById(R.id.rvMatrixBContainer)
        resultMatrixContainer = findViewById(R.id.resultMatrixContainer)
        rgOperations = findViewById(R.id.rgOperations)
        tvResultDimensions = findViewById(R.id.tvResultDimensions)
        tvResultStatus = findViewById(R.id.tvResultStatus)
    }

    private fun setupListeners() {
        btnCreateMatrixA.setOnClickListener {
            createMatrixInputs(true)
        }

        btnCreateMatrixB.setOnClickListener {
            createMatrixInputs(false)
        }

        btnCalculate.setOnClickListener {
            calculateResult()
        }
    }

    private fun createMatrixInputs(isMatrixA: Boolean) {
        try {
            val recycler = if (isMatrixA) rvMatrixAContainer else rvMatrixBContainer
            val rowsInput = if (isMatrixA) etMatrixARows else etMatrixBRows
            val colsInput = if (isMatrixA) etMatrixACols else etMatrixBCols

            val rows = rowsInput.text.toString().toIntOrNull() ?: 0
            val cols = colsInput.text.toString().toIntOrNull() ?: 0

//            val total = rows * cols

            if (rows <= 0 || cols <= 0 || rows > 10 || cols > 10) {
                showToast("Please enter valid dimensions (1-10)")
                return
            }

            // Update class variables
            if (isMatrixA) {
                matrixARows = rows
                matrixACols = cols
            } else {
                matrixBRows = rows
                matrixBCols = cols
            }


            val total = rows * cols
            val values = MutableList(total) { "" }

            val adapter = MatrixAdapter(cols, values) { pos, text ->
                values[pos] = text
            }

            recycler.layoutManager = GridLayoutManager(this, cols)
            recycler.adapter = adapter

            if (isMatrixA) matrixAAdapter = adapter else matrixBAdapter = adapter


//            // Clear container
//            container.removeAllViews()
//
//            // Create input matrix
//            val inputMatrix = Array(rows) { Array(cols) { EditText(this) } }
//
//            // Add rows of EditTexts
//            for (i in 0 until rows) {
//                val rowLayout = LinearLayout(this)
//                rowLayout.orientation = LinearLayout.HORIZONTAL
//                rowLayout.layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//
//                for (j in 0 until cols) {
//                    val editText = EditText(this)
//                    editText.layoutParams = LinearLayout.LayoutParams(
//                        120, // width in dp
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                    )
//                    editText.gravity = Gravity.CENTER
//                    editText.hint = "0"
//                    editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER or
//                            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
//                            android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
//
//                    rowLayout.addView(editText)
//                    inputMatrix[i][j] = editText
//                }
//
//                container.addView(rowLayout)
//            }
//
//            // Store the matrix inputs
//            if (isMatrixA) {
//                matrixAInputs = inputMatrix
//            } else {
//                matrixBInputs = inputMatrix
//            }

        } catch (e: Exception) {
            showToast("Error creating matrix: ${e.message}")
        }
    }

    private fun calculateResult() {
        try {
            // Ensure both adapters initialized
            if (!::matrixAAdapter.isInitialized || !::matrixBAdapter.isInitialized) {
                showToast("Create both matrices first")
                return
            }

            // Build 2D arrays from adapter values
            val matrixAValues = Array(matrixARows) { r ->
                DoubleArray(matrixACols) { c ->
                    matrixAAdapter.values[r * matrixACols + c].toDoubleOrNull() ?: 0.0
                }
            }
            val matrixBValues = Array(matrixBRows) { r ->
                DoubleArray(matrixBCols) { c ->
                    matrixBAdapter.values[r * matrixBCols + c].toDoubleOrNull() ?: 0.0
                }
            }

            val operationId = when (rgOperations.checkedRadioButtonId) {
                R.id.rbAdd -> MatrixUtils.OPERATION_ADD
                R.id.rbSubtract -> MatrixUtils.OPERATION_SUBTRACT
                R.id.rbMultiply -> MatrixUtils.OPERATION_MULTIPLY
                R.id.rbDivide -> MatrixUtils.OPERATION_DIVIDE
                else -> -1
            }
            if (operationId == -1) {
                showToast("Please select an operation"); return
            }

            val result = MatrixUtils.performOperation(
                operationId,
                matrixAValues, matrixARows, matrixACols,
                matrixBValues, matrixBRows, matrixBCols
            )
            displayResult(result)
        } catch (e: Exception) {
            showToast("Error calculating result: ${e.message}")
        }
    }

    private fun displayResult(result: MatrixUtils.OperationResult) {
        resultMatrixContainer.removeAllViews()
        if (result.success) {
            tvResultStatus.visibility = View.GONE
            tvResultDimensions.text = "Dimensions: ${result.rows} × ${result.cols}"
            for (i in 0 until result.rows) {
                val rowLayout = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                for (j in 0 until result.cols) {
                    val tv = TextView(this).apply {
                        layoutParams =
                            LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT)
                        gravity = Gravity.CENTER
                        text = decimalFormat.format(result.result!![i][j])
                        textSize = 16f; setPadding(8, 8, 8, 8)
                    }
                    rowLayout.addView(tv)
                }
                resultMatrixContainer.addView(rowLayout)
            }
        } else {
            tvResultStatus.visibility = View.VISIBLE
            tvResultStatus.text = result.message
            tvResultDimensions.text = "Dimensions: -"
        }
    }

    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}



//    private fun calculateResult() {
//        try {
//            // Check if matrices have been created
//            if (matrixAInputs.isEmpty() || matrixBInputs.isEmpty()) {
//                showToast("Create both matrices first")
//                return
//            }
//
//            // Collect matrix values
//            val matrixAValues = Array(matrixARows) { Array(matrixACols) { "" } }
//            val matrixBValues = Array(matrixBRows) { Array(matrixBCols) { "" } }
//
//            for (i in 0 until matrixARows) {
//                for (j in 0 until matrixACols) {
//                    matrixAValues[i][j] = matrixAInputs[i][j].text.toString().ifEmpty { "0" }
//                }
//            }
//
//            for (i in 0 until matrixBRows) {
//                for (j in 0 until matrixBCols) {
//                    matrixBValues[i][j] = matrixBInputs[i][j].text.toString().ifEmpty { "0" }
//                }
//            }
//
//            // Determine operation
//            val operationId = when (rgOperations.checkedRadioButtonId) {
//                R.id.rbAdd -> MatrixUtils.OPERATION_ADD
//                R.id.rbSubtract -> MatrixUtils.OPERATION_SUBTRACT
//                R.id.rbMultiply -> MatrixUtils.OPERATION_MULTIPLY
//                R.id.rbDivide -> MatrixUtils.OPERATION_DIVIDE
//                else -> -1
//            }
//
//            if (operationId == -1) {
//                showToast("Please select an operation")
//                return
//            }
//
//            // Perform operation
//            val result = MatrixUtils.performOperation(
//                operationId,
//                matrixAValues, matrixARows, matrixACols,
//                matrixBValues, matrixBRows, matrixBCols
//            )
//
//            // Display result
//            displayResult(result)
//
//        } catch (e: Exception) {
//            showToast("Error calculating result: ${e.message}")
//        }
//    }
//
//    private fun displayResult(result: MatrixUtils.OperationResult) {
//        // Clear previous result
//        resultMatrixContainer.removeAllViews()
//
//        // Update result status and dimensions
//        if (result.success) {
//            tvResultStatus.visibility = View.GONE
//            tvResultDimensions.text = "Dimensions: ${result.rows} × ${result.cols}"
//
//            // Create result matrix display
//            for (i in 0 until result.rows) {
//                val rowLayout = LinearLayout(this)
//                rowLayout.orientation = LinearLayout.HORIZONTAL
//                rowLayout.layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//
//                for (j in 0 until result.cols) {
//                    val textView = TextView(this)
//                    textView.layoutParams = LinearLayout.LayoutParams(
//                        120, // width in dp
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                    )
//                    textView.gravity = Gravity.CENTER
//                    textView.text = decimalFormat.format(result.result!![i][j])
//                    textView.textSize = 16f
//                    textView.setPadding(8, 8, 8, 8)
//
//                    rowLayout.addView(textView)
//                }
//
//                resultMatrixContainer.addView(rowLayout)
//            }
//        } else {
//            tvResultStatus.visibility = View.VISIBLE
//            tvResultStatus.text = result.message
//            tvResultDimensions.text = "Dimensions: -"
//        }
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
//}