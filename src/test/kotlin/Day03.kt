import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.lang.Math.abs
import kotlin.coroutines.experimental.buildSequence

/*
--- Day 3: Spiral Memory ---

You come across an experimental new kind of memory stored on an infinite two-dimensional grid.

Each square on the grid is allocated in a spiral pattern starting at a location marked 1 and
then counting up while spiraling outward.
For example, the first few squares are allocated like this:

17  16  15  14  13
18   5   4   3  12
19   6   1   2  11
20   7   8   9  10
21  22  23---> ...
While this is very space-efficient (no squares are skipped),
requested data must be carried back to square 1 (the location of the only access port for this memory system)
by programs that can only move up, down, left, or right.
They always take the shortest path: the Manhattan Distance between the location of the data and square 1.

For example:

Data from square 1 is carried 0 steps, since it's at the access port.
Data from square 12 is carried 3 steps, such as: down, left, left.
Data from square 23 is carried only 2 steps: up twice.
Data from square 1024 must be carried 31 steps.
How many steps are required to carry the data from the square identified in your puzzle input all the way to the access port?

Your puzzle answer was 430.

--- Part Two ---

As a stress test on the system, the programs here clear the grid and then store the value 1 in square 1.
Then, in the same allocation order as shown above, they store the sum of the values in all adjacent squares,
including diagonals.

So, the first few squares' values are chosen as follows:

Square 1 starts with the value 1.
Square 2 has only one adjacent filled square (with value 1), so it also stores 1.
Square 3 has both of the above squares as neighbors and stores the sum of their values, 2.
Square 4 has all three of the aforementioned squares as neighbors and stores the sum of their values, 4.
Square 5 only has the first and fourth squares as neighbors, so it gets the value 5.

Once a square is written, its value does not change.
Therefore, the first few squares would receive the following values:

147  142  133  122   59
304    5    4    2   57
330   10    1    1   54
351   11   23   25   26
362  747  806--->   ...

What is the first value written that is larger than your puzzle input?

Your puzzle input was 312051.

*/


fun distance(point1: Pair<Int, Int>, point2: Pair<Int, Int> = Pair(0, 0)) = abs(point1.first - point2.first) + abs(point1.second - point2.second)

fun calculateGrid(calculateValue: (Int, Pair<Int,Int>) -> Int = { i, _ -> i }) = buildSequence {
    fun calculateResult(i: Int, x: Int, y: Int) = with(Pair(x, y)) {
        Pair(calculateValue(i, this), this)
    }
    fun incr(invers: Boolean) = if (invers) -1 else 1

    var x = 0; var y = 0; var i = 1
    var incr = 1
    var invers = false
    yield(calculateResult(i, x, y))
    while (true) {
        for (i2 in 1..incr) {
            i++
            x += incr(invers)
            yield(calculateResult(i, x, y))
        }
        for (i2 in 1..incr) {
            i++
            y += incr(invers)
            yield(calculateResult(i, x, y))
        }
        incr += 1
        invers = !invers
    }
}

fun calculateGrid(i: Int) = calculateGrid().elementAt(i - 1)


fun calculateGrid2(): Sequence<Pair<Int, Pair<Int, Int>>> {
    val grid = mutableMapOf<Pair<Int, Int>, Int>()

    fun calculateValue2(i: Int, xy: Pair<Int, Int>): Int {
        val result =
                if (i <= 1) 1
                else sumNeighbors(xy, grid)
        grid[xy] = result
        return result
    }
    return calculateGrid(::calculateValue2)
}

fun sumNeighbors(xy: Pair<Int, Int>, grid: Map<Pair<Int, Int>, Int>) =
        neigbours(xy, grid).toList().sum()

fun neigbours(xy: Pair<Int, Int>, grid: Map<Pair<Int, Int>, Int>) =
        buildSequence {
            for (dx in -1..1)
                for (dy in -1..1)
                    if(! (dx == 0 && dy == 0))
                        yield(grid[Pair(xy.first+dx, xy.second+dy)])
        }.filterNotNull()

class Day3Spec : Spek({
    describe("Manhattan distance") {
        on("same point") {
            val point = Pair(1, 1)

            it("manhattan distance should be 0") {
                distance(point, point) `should equal` 0
            }
        }
        on("point differ 1 on x axis") {
            val point1 = Pair(1, 1)
            val point2 = Pair(2, 1)

            it("manhattan distance should be 1") {
                distance(point1, point2) `should equal` 1
            }
        }
        on("point differ 2 on x axis and 3 on y axis") {
            val point1 = Pair(1, 1)
            val point2 = Pair(3, -2)

            it("manhattan distance should be 5") {
                distance(point1, point2) `should equal` 5
            }
        }
    }

    describe("grid") {
        on("grid") {
            val grid = calculateGrid()

            it("starts with <0,0>") {
                grid.elementAt(0) `should equal` Pair(1, Pair(0, 0))
            }
            it("should have correct next elements") {
                grid.elementAt(1) `should equal` Pair(2, Pair(1, 0))
                grid.elementAt(2) `should equal` Pair(3, Pair(1, 1))
                grid.elementAt(3) `should equal` Pair(4, Pair(0, 1))
                grid.elementAt(4) `should equal` Pair(5, Pair(-1, 1))
            }
            it("should have more correct next elements") {
                grid.elementAt(19) `should equal` Pair(20, Pair(-2, -1))
                grid.elementAt(22) `should equal` Pair(23, Pair(0, -2))
            }
        }
    }

    describe("manhattan distance in grid") {
        on("grid") {
            it("1 should be 0") {
                distance(calculateGrid(1).second) `should equal` 0
            }
            it("12 should be 3") {
                distance(calculateGrid(12).second) `should equal` 3
            }
            it("23 should be 2") {
                distance(calculateGrid(23).second) `should equal` 2
            }
            it("1024 should be 31") {
                distance(calculateGrid(1024).second) `should equal` 31
            }
        }
    }

    describe("manhattan distance for") {
        on("312051") {
            println("Steps: ${distance(calculateGrid(312051).second)}")
        }
    }

    describe("grid - part2") {
        it("starts with <0,0>") {
            calculateGrid2().elementAt(0) `should equal` Pair(1, Pair(0, 0))
        }
        it("should have correct next elements") {
            calculateGrid2().elementAt(1) `should equal` Pair(1, Pair(1, 0))
            calculateGrid2().elementAt(2) `should equal` Pair(2, Pair(1, 1))
            calculateGrid2().elementAt(3) `should equal` Pair(4, Pair(0, 1))
            calculateGrid2().elementAt(4) `should equal` Pair(5, Pair(-1, 1))
        }
        it("should have more correct next elements") {
            calculateGrid2().elementAt(19) `should equal` Pair(351, Pair(-2, -1))
            calculateGrid2().elementAt(22) `should equal` Pair(806, Pair(0, -2))
        }
    }
    describe("find grid value larger than puzzle input") {
        on("grid2") {
            val found = calculateGrid2().first { it.first > 312051 }.first
            println("First value lager than input: $found")
        }
    }


})


