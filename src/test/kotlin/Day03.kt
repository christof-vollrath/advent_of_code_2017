import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.lang.Math.abs
import kotlin.coroutines.experimental.buildSequence

/*
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

 */


fun distance(point1: Pair<Int, Int>, point2: Pair<Int, Int> = Pair(0, 0)) = abs(point1.first - point2.first) + abs(point1.second - point2.second)

fun grid() = buildSequence {
    var x = 0
    var y = 0
    var i = 1
    var incr = 1
    var invers = false
    yield(Pair(x, y))
    while (true) {
        for (i2 in 1..incr) {
            i++
            x += if (invers) -1 else 1
            yield(Pair(x, y))
        }
        for (i2 in 1..incr) {
            i++
            y += if (invers) -1 else 1
            yield(Pair(x, y))
        }
        incr += 1
        invers = !invers
    }
}

fun grid(i: Int) = grid().elementAt(i - 1)

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
            val grid = grid()

            it("starts with <0,0>") {
                grid.elementAt(0) `should equal` Pair(0, 0)
            }
            it("should have correct next elements") {
                grid.elementAt(1) `should equal` Pair(1, 0)
                grid.elementAt(2) `should equal` Pair(1, 1)
                grid.elementAt(3) `should equal` Pair(0, 1)
                grid.elementAt(4) `should equal` Pair(-1, 1)
            }
            it("should have more correct next elements") {
                grid.elementAt(19) `should equal` Pair(-2, -1)
                grid.elementAt(22) `should equal` Pair(0, -2)
            }
        }
    }

    describe("manhattan distance in grid") {
        on("grid") {
            it("1 should be 0") {
                distance(grid(1)) `should equal` 0
            }
            it("12 should be 3") {
                distance(grid(12)) `should equal` 3
            }
            it("23 should be 2") {
                distance(grid(23)) `should equal` 2
            }
            it("1024 should be 31") {
                distance(grid(1024)) `should equal` 31
            }
        }
    }

    describe("manhattan distance for") {
        on("312051") {
            println(distance(grid(312051)))
        }
    }
})


