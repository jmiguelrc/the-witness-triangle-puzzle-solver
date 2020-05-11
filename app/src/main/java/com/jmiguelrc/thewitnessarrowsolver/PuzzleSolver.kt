package com.jmiguelrc.thewitnessarrowsolver

data class Point(val x: Int, val y: Int) // Notation: (0,0) is lower left corner

fun Point.right() = Point(this.x + 1, this.y)
fun Point.left() = Point(this.x - 1, this.y)
fun Point.up() = Point(this.x, this.y + 1)
fun Point.down() = Point(this.x, this.y - 1)

data class Edge(val from: Point, val to: Point)

fun Edge.invert() = Edge(this.to, this.from)

fun Edge.toArrow(): String =
    if (this.from.x > this.to.x) "←"
    else if (this.from.x < this.to.x) "→"
    else if (this.from.x > this.to.x) "↓"
    else if (this.from.y > this.to.y) "↓"
    else if (this.from.y < this.to.y) "↑"
    else throw RuntimeException("Invalid edge")

class ArrowBlock(val lowerLeftPoint: Point, val numArrows: Int) {
    private val edges: List<Edge>

    init {
        val lowHorizontalEdge = Edge(lowerLeftPoint, lowerLeftPoint.right())
        val highHorizontalEdge = Edge(lowerLeftPoint.up(), lowerLeftPoint.up().right())
        val leftVerticalEdge = Edge(lowerLeftPoint, lowerLeftPoint.up())
        val rightVerticalEdge = Edge(lowerLeftPoint.right(), lowerLeftPoint.right().up())
        this.edges = listOf(
            lowHorizontalEdge,
            highHorizontalEdge,
            leftVerticalEdge,
            rightVerticalEdge
        ).flatMap {
            listOf(
                it,
                it.invert()
            )
        }
    }

    fun isConditionValid(visitedEdges: Set<Edge>) =
        edges.filter { it in visitedEdges }.size == numArrows
}

class PuzzleSolver(
    private val arrowBlocks: List<ArrowBlock>,
    private val numLines: Int,
    private val numColumns: Int
) {
    private val visitedPoints: HashSet<Point> = HashSet()
    private val visitedEdges: HashSet<Edge> = HashSet()

    private val originPoint = Point(0, 0)
    private val finishPoint = Point(numColumns - 1, numLines - 1)

    fun findSolution(): Boolean {
        visitedPoints.clear()
        visitedEdges.clear()

        return visit(originPoint, finishPoint)
    }

    fun getSolution(): String {
        var currentPoint = originPoint
        val solutions = StringBuilder()

        while (currentPoint != finishPoint) {
            val edge = getEdgeStartingInPoint(currentPoint)!!
            solutions.append(edge.toArrow())
            currentPoint = edge.to
        }
        return solutions.toString()
    }

    private fun getEdgeStartingInPoint(p: Point) = visitedEdges.firstOrNull { it.from == p }

    private fun visit(current: Point, finish: Point): Boolean {
        if (current == finish) {
            return arrowBlocks.all { it.isConditionValid(visitedEdges) }
        }

        for (pointToVisit in pointsThatCanBeVisitedFrom(current)) {
            val newEdge = Edge(current, pointToVisit)
            visitedEdges.add(newEdge)
            visitedPoints.add(pointToVisit)
            if (visit(pointToVisit, finish)) {
                return true
            }
            visitedEdges.remove(newEdge)
            visitedPoints.remove(pointToVisit)
        }
        return false;
    }

    private fun pointsThatCanBeVisitedFrom(point: Point) =
        point.neighbours().filter(this::canBeVisited)

    private fun Point.neighbours() =
        listOf(this.up(), this.right(), this.left(), this.down())
            .filter { it.x >= 0 && it.y >= 0 && it.x < numColumns && it.y < numLines }


    private fun canBeVisited(point: Point) = point !in visitedPoints
}