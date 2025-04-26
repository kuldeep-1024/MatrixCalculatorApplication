package com.example.matrixcalculator

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView


class MatrixAdapter(
    private val cols: Int,
    val values: MutableList<String>,
    private val onValueChanged: (position: Int, text: String) -> Unit
) : RecyclerView.Adapter<MatrixAdapter.CellHolder>() {

    inner class CellHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val editText: EditText = view.findViewById(R.id.etCell)
        private var watcher: TextWatcher? = null

        fun bind(position: Int) {
            watcher?.let { editText.removeTextChangedListener(it) }
            editText.setText(values[position])

            watcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    onValueChanged(position, s.toString())
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
            editText.addTextChangedListener(watcher)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.matrix_cell, parent, false)
        return CellHolder(view)
    }

    override fun onBindViewHolder(holder: CellHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = values.size
}
