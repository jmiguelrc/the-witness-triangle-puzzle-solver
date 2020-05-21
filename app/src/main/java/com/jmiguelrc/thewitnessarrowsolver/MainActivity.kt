package com.jmiguelrc.thewitnessarrowsolver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.children

class MainActivity : AppCompatActivity() {

    private val numTrianglesInBlock: MutableMap<Point, Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gridLayout = findViewById<GridLayout>(R.id.gridPuzzle)

        gridLayout.children.forEachIndexed { index, view ->
            run {
                view.setOnClickListener {
                    val buttonPoint = buttonIndexToPoint(index)
                    Log.i(
                        "MainActivity",
                        "Pressed $index button, point=${buttonIndexToPoint(index)}"
                    )
                    (view as Button).text = when (view.text) {
                        "" -> "▲"
                        "▲" -> "▲▲"
                        "▲▲" -> "▲▲▲"
                        else -> ""
                    }

                    if (view.text.isEmpty()) {
                        numTrianglesInBlock.remove(buttonPoint)
                    } else {
                        numTrianglesInBlock[buttonPoint] = view.text.length
                    }
                }
            }
        }

        val textViewSolution = findViewById<TextView>(R.id.tvSolution);
        val solveButton = findViewById<Button>(R.id.solveButton)
        solveButton.setOnClickListener {
            val listTriangleBlocks =
                numTrianglesInBlock.map { entry -> TriangleBlock(entry.key, entry.value) }
            val puzzleSolver = PuzzleSolver(listTriangleBlocks, 5, 5)
            if (puzzleSolver.findSolution()) {
                textViewSolution.text = puzzleSolver.getSolution()
            } else {
                textViewSolution.text = "No solution found!"
            }
        }

        val clearButton = findViewById<Button>(R.id.clearButton)
        clearButton.setOnClickListener {
            gridLayout.children.forEach { (it as Button).text = "" }
            numTrianglesInBlock.clear()
            textViewSolution.text = ""
        }

    }

    private fun buttonIndexToPoint(index: Int): Point {
        val x = index % 4
        val y = index / 4
        val yCorrected = 3 - y
        return Point(x, yCorrected)
    }
}
