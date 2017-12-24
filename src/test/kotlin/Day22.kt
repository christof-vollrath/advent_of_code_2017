import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import java.lang.Integer.max
import kotlin.coroutines.experimental.buildSequence
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 22: Sporifica Virus ---

Diagnostics indicate that the local grid computing cluster has been contaminated with the Sporifica Virus.
The grid computing cluster is a seemingly-infinite two-dimensional grid of compute nodes.
Each node is either clean or infected by the virus.

To prevent overloading the nodes (which would render them useless to the virus) or detection by system administrators,
exactly one virus carrier moves through the network, infecting or cleaning nodes as it moves.
The virus carrier is always located on a single node in the network (the current node)
and keeps track of the direction it is facing.

To avoid detection, the virus carrier works in bursts; in each burst,
it wakes up, does some work, and goes back to sleep.
The following steps are all executed in order one time each burst:

If the current node is infected, it turns to its right. Otherwise, it turns to its left.
(Turning is done in-place; the current node does not change.)
If the current node is clean, it becomes infected. Otherwise, it becomes cleaned.
(This is done after the node is considered for the purposes of changing direction.)
The virus carrier moves forward one node in the direction it is facing.
Diagnostics have also provided a map of the node infection status (your puzzle input).
Clean nodes are shown as .; infected nodes are shown as #.
This map only shows the center of the grid;
there are many more nodes beyond those shown, but none of them are currently infected.

The virus carrier begins in the middle of the map facing up.

For example, suppose you are given a map like this:

..#
#..
...

Then, the middle of the infinite grid looks like this, with the virus carrier's position marked with [ ]:

. . . . . . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . # . . .
. . . #[.]. . . .
. . . . . . . . .
. . . . . . . . .
. . . . . . . . .

The virus carrier is on a clean node, so it turns left, infects the node, and moves left:

. . . . . . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . # . . .
. . .[#]# . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . . . . .

The virus carrier is on an infected node, so it turns right, cleans the node, and moves up:

. . . . . . . . .
. . . . . . . . .
. . . . . . . . .
. . .[.]. # . . .
. . . . # . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . . . . .

Four times in a row, the virus carrier finds a clean, infects it, turns left, and moves forward,
ending in the same place and still facing up:

. . . . . . . . .
. . . . . . . . .
. . . . . . . . .
. . #[#]. # . . .
. . # # # . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . . . . .

Now on the same node as before, it sees an infection, which causes it to turn right, clean the node,
and move forward:

. . . . . . . . .
. . . . . . . . .
. . . . . . . . .
. . # .[.]# . . .
. . # # # . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . . . . .

After the above actions, a total of 7 bursts of activity had taken place.
Of them, 5 bursts of activity caused an infection.

After a total of 70, the grid looks like this, with the virus carrier facing up:

. . . . . # # . .
. . . . # . . # .
. . . # . . . . #
. . # . #[.]. . #
. . # . # . . # .
. . . . . # # . .
. . . . . . . . .
. . . . . . . . .

By this time, 41 bursts of activity caused an infection (though most of those nodes have since been cleaned).

After a total of 10000 bursts of activity, 5587 bursts will have caused an infection.

Given your actual map, after 10000 bursts of activity, how many bursts cause a node to become infected?
(Do not count nodes that begin infected.)

Your puzzle answer was 5266.

--- Part Two ---

As you go to remove the virus from the infected nodes, it evolves to resist your attempt.

Now, before it infects a clean node, it will weaken it to disable your defenses.
If it encounters an infected node, it will instead flag the node to be cleaned in the future. So:

Clean nodes become weakened.
Weakened nodes become infected.
Infected nodes become flagged.
Flagged nodes become clean.
Every node is always in exactly one of the above states.

The virus carrier still functions in a similar way, but now uses the following logic during its bursts of action:

Decide which way to turn based on the current node:
If it is clean, it turns left.
If it is weakened, it does not turn, and will continue moving in the same direction.
If it is infected, it turns right.
If it is flagged, it reverses direction, and will go back the way it came.
Modify the state of the current node, as described above.
The virus carrier moves forward one node in the direction it is facing.
Start with the same map (still using . for clean and # for infected)
and still with the virus carrier starting in the middle and facing up.

Using the same initial state as the previous example, and drawing weakened as W and flagged as F,
the middle of the infinite grid looks like this, with the virus carrier's position again marked with [ ]:

. . . . . . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . # . . .
. . . #[.]. . . .
. . . . . . . . .
. . . . . . . . .
. . . . . . . . .

This is the same as before, since no initial nodes are weakened or flagged.
The virus carrier is on a clean node, so it still turns left, instead weakens the node, and moves left:

. . . . . . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . # . . .
. . .[#]W . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . . . . .

The virus carrier is on an infected node, so it still turns right,
instead flags the node, and moves up:

. . . . . . . . .
. . . . . . . . .
. . . . . . . . .
. . .[.]. # . . .
. . . F W . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . . . . .

This process repeats three more times,
ending on the previously-flagged node and facing right:

. . . . . . . . .
. . . . . . . . .
. . . . . . . . .
. . W W . # . . .
. . W[F]W . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . . . . .

Finding a flagged node, it reverses direction and cleans the node:

. . . . . . . . .
. . . . . . . . .
. . . . . . . . .
. . W W . # . . .
. .[W]. W . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . . . . .

The weakened node becomes infected, and it continues in the same direction:

. . . . . . . . .
. . . . . . . . .
. . . . . . . . .
. . W W . # . . .
.[.]# . W . . . .
. . . . . . . . .
. . . . . . . . .
. . . . . . . . .

Of the first 100 bursts, 26 will result in infection.
Unfortunately, another feature of this evolved virus is speed;
of the first 10000000 bursts, 2511944 will result in infection.

Given your actual map, after 10000000 bursts of activity,
how many bursts cause a node to become infected? (Do not count nodes that begin infected.)

Your puzzle answer was 2511895.

 */

class Day22Spec : Spek({
    describe("solution part 1") {
        given("day22 input") {
            val grid = parseGrid(day22ExerciseInput)
            on("10_000 step") {
                grid.moveSteps(10_000)
                println("Bursts in part 1: ${grid.bursts}")
            }
        }
    }
    describe("solution part 3") {
        given("day22 input") {
            val grid = parseGrid2(day22ExerciseInput)
            on("10_000_000 step") {
                grid.moveSteps(10_000_000)
                println("Bursts in part 2: ${grid.bursts}")
            }
        }
    }
    describe("parse grid") {
        on("parse example input") {
            val grid = parseGrid(day22ExampleInput)
            it("should contain two infected nodes at the correct positions") {
                grid.infected `should equal` mapOf(Pair(1, 1) to '#', Pair(-1, 0) to '#')
                grid.infected.size `should equal` 2
            }
            it("should be converted to the input string") {
                grid.toString() `should equal` day22ExampleInput
            }
        }
    }
    describe("start") {
        on("example input") {
            val grid = parseGrid(day22ExampleInput)
            it("should start in the middle") {
                grid.pos `should equal` Pair(0, 0)
            }
        }
    }
    describe("part 1") {
        describe("move some steps") {
            given("example input") {
                val grid = parseGrid(day22ExampleInput)
                on("first step") {
                    grid.moveOneStep()
                    it("should have changed") {
                        grid.pos `should equal` Pair(-1, 0)
                        grid.dir `should equal` Pair(-1, 0)
                        grid.toString() `should equal` """
..#
##.
...
"""
                    }
                }
                on("second step") {
                    grid.moveOneStep()
                    it("should have changed again") {
                        grid.pos `should equal` Pair(-1, 1)
                        grid.dir `should equal` Pair(0, 1)
                        grid.toString() `should equal` """
..#
.#.
...
"""
                    }
                }
                on("four more steps") {
                    grid.moveSteps(4)
                    it("should have changed again") {
                        grid.pos `should equal` Pair(-1, 1)
                        grid.dir `should equal` Pair(0, 1)
                        grid.toString() `should equal` """
##.#.
###..
.....
"""
                    }
                }
                on("one more step") {
                    grid.moveSteps(1)
                    it("should have changed again") {
                        grid.pos `should equal` Pair(0, 1)
                        grid.dir `should equal` Pair(1, 0)
                        grid.toString() `should equal` """
#..#.
###..
.....
"""
                    }
                }
            }
        }
        describe("move 70 steps") {
            given("example input") {
                val grid = parseGrid(day22ExampleInput)
                on("70 step") {
                    grid.moveSteps(70)
                    it("should have changed") {
                        grid.pos `should equal` Pair(1, 1)
                        grid.dir `should equal` Pair(0, 1)
                        grid.bursts `should equal` 41
                        grid.toString() `should equal` """
.....##..
....#..#.
...#....#
..#.#...#
..#.#..#.
.....##..
.........
.........
.........
"""
                    }
                }
            }
        }
        describe("move 10_000 steps") {
            given("example input") {
                val grid = parseGrid(day22ExampleInput)
                on("10_000 step") {
                    grid.moveSteps(10_000)
                    it("should have 5587 bursts") {
                        grid.bursts `should equal` 5587
                    }
                }
            }
        }
    }
    describe("part 2") {
        describe("move some steps") {
            given("example input") {
                val grid = parseGrid2(day22ExampleInput)
                on("first step") {
                    grid.moveOneStep()
                    it("should have changed") {
                        grid.pos `should equal` Pair(-1, 0)
                        grid.dir `should equal` Pair(-1, 0)
                        grid.toString() `should equal` """
..#
#W.
...
"""
                    }
                }
                on("second step") {
                    grid.moveOneStep()
                    it("should have changed again") {
                        grid.pos `should equal` Pair(-1, 1)
                        grid.dir `should equal` Pair(0, 1)
                        grid.toString() `should equal` """
..#
FW.
...
"""
                    }
                }
                on("three more steps") {
                    grid.moveSteps(3)
                    it("should have changed again") {
                        grid.pos `should equal` Pair(-1, 0)
                        grid.dir `should equal` Pair(1, 0)
                        grid.toString() `should equal` """
WW.#.
WFW..
.....
"""
                    }
                }
                on("one more step in flagged mode") {
                    grid.moveSteps(1)
                    it("should have changed again") {
                        grid.pos `should equal` Pair(-2, 0)
                        grid.dir `should equal` Pair(-1, 0)
                        grid.toString() `should equal` """
WW.#.
W.W..
.....
"""
                    }
                }
                on("one more step in weakened mode") {
                    grid.moveSteps(1)
                    it("should have changed again") {
                        grid.pos `should equal` Pair(-3, 0)
                        grid.dir `should equal` Pair(-1, 0)
                        grid.toString() `should equal` """
WW.#.
#.W..
.....
"""
                    }
                }
            }
        }
        describe("move 100 steps") {
            given("example input") {
                val grid = parseGrid2(day22ExampleInput)
                on("100 step") {
                    grid.moveSteps(100)
                    it("should have 26 bursts") {
                        grid.bursts `should equal` 26
                    }
                }
            }
        }
        describe("move 10_000_000 steps") {
            given("example input") {
                val grid = parseGrid2(day22ExampleInput)
                on("10_000_000 step") {
                    grid.moveSteps(10_000_000)
                    it("should have 2511944 bursts") {
                        grid.bursts `should equal` 2511944
                    }
                }
            }
        }
    }

    describe("turn dir to the left or right") {
        val testData = arrayOf(
                //     dir      |  turn  |  result
                //--|-----------|--------|--------------
                data(Pair( 1, 0),   right, Pair( 0,-1)),
                data(Pair( 0,-1),   right, Pair(-1, 0)),
                data(Pair(-1, 0),   right, Pair( 0, 1)),
                data(Pair( 0, 1),   right, Pair( 1, 0)),
                data(Pair( 1, 0),    left, Pair( 0, 1)),
                data(Pair( 0, 1),    left, Pair(-1, 0)),
                data(Pair(-1, 0),    left, Pair( 0,-1)),
                data(Pair( 0,-1),    left, Pair( 1, 0)),
                data(Pair( 1, 0), reverse, Pair(-1, 0)),
                data(Pair( 0, 1), reverse, Pair( 0,-1)),
                data(Pair(-1, 0), reverse, Pair( 1, 0)),
                data(Pair( 0,-1), reverse, Pair( 0, 1))
                )
        onData("input %s %s", with = *testData) { dir, turn, expected ->
            it("returns $expected") {
                rotate(dir,turn) `should equal`expected
            }
        }
    }
})

data class Grid(val infected: MutableMap<Pair<Int, Int>,Char>, var pos: Pair<Int, Int> = Pair(0, 0), var dir: Pair<Int, Int> = Pair(0, 1), var bursts: Int = 0) {
    fun moveOneStep() {
        if (infected[pos] == '#') {
            dir = rotate(dir, right)
            infected.remove(pos)
        } else {
            dir = rotate(dir, left)
            infected[pos] = '#'
            bursts++
        }
        pos = movePos(pos, dir)
    }
    fun moveSteps(n: Int) =(1..n).forEach { moveOneStep() }
    override fun toString() = gridToString(infected)
}

data class Grid2(val infected: MutableMap<Pair<Int, Int>,Char>, var pos: Pair<Int, Int> = Pair(0, 0), var dir: Pair<Int, Int> = Pair(0, 1), var bursts: Int = 0) {
    fun moveOneStep() {
        when(infected[pos]) {
            '#' -> {
                    dir = rotate(dir, right)
                    infected[pos] = 'F'
                }
            'W' -> {
                    infected[pos] = '#'
                    bursts++
                }
            'F' -> {
                    dir = rotate(dir, reverse)
                    infected.remove(pos)
                }
            else -> {
                    dir = rotate(dir, left)
                    infected[pos] = 'W'
                }
        }
        pos = movePos(pos, dir)
    }
    fun moveSteps(n: Int) =(1..n).forEach { moveOneStep() }
    override fun toString() = gridToString(infected)
}

fun rotate(dir: Pair<Int, Int>, turn: Array<IntArray>) =
        Pair(dir.first * turn[0][0] + dir.second * turn[0][1],
             dir.first * turn[1][0] + dir.second * turn[1][1])

fun movePos(pos: Pair<Int, Int>, dir: Pair<Int, Int>) = Pair(pos.first + dir.first, pos.second + dir.second)

val left    = arrayOf(intArrayOf(0,-1), intArrayOf( 1, 0))
val right   = arrayOf(intArrayOf(0, 1), intArrayOf(-1, 0))
val reverse = arrayOf(intArrayOf(-1, 0), intArrayOf(0, -1))

fun gridToString(infected: MutableMap<Pair<Int, Int>, Char>): String {
    val maxX = max(infected.keys.map { it.first }.max()?:0, infected.keys.map { -it.first }.max()?:0)
    val maxY = max(infected.keys.map { it.second }.max()?:0, infected.keys.map { -it.second }.max()?:0)
    return buildSequence {
        yield('\n')
        (maxY downTo -maxY).forEach { y ->
            (-maxX .. maxX).forEach { x ->
                val c = infected[Pair(x, y)]
                yield(c ?: '.')
            }
            yield('\n')
        }
    }.joinToString("")
}

fun parseGrid(input: String): Grid = Grid(fillNodeMap(parseGridToLists(input)))
fun parseGrid2(input: String): Grid2 = Grid2(fillNodeMap(parseGridToLists(input)))

fun fillNodeMap(gridAsList: List<List<Char>>): MutableMap<Pair<Int, Int>, Char> {
    val delta = gridAsList.size / 2
    return buildSequence {
        gridAsList.forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                if (c == '#') yield(Pair(x - delta, delta - y))
            }
        }
    }
    .map { Pair(it, '#') }.toMap().toMutableMap()
}

fun parseGridToLists(input: String) =
        input.split("\n")
                .filter { !it.isBlank() }
                .map { it.map { it } }

val day22ExampleInput = """
..#
#..
...
"""

val day22ExerciseInput = """
.########.....#...##.####
....#..#.#.##.###..#.##..
##.#.#..#.###.####.##.#..
####...#...####...#.##.##
..#...###.#####.....##.##
..#.##.######.#...###...#
.#....###..##....##...##.
##.##..####.#.######...##
#...#..##.....#..#...#..#
........#.##..###.#.....#
#.#..######.#.###..#...#.
.#.##.##..##.####.....##.
.....##..#....#####.#.#..
...#.#.#..####.#..###..#.
##.#..##..##....#####.#..
.#.#..##...#.#####....##.
.####.#.###.####...#####.
...#...######..#.##...#.#
#..######...#.####.#..#.#
...##..##.#.##.#.#.#....#
###..###.#..#.....#.##.##
..#....##...#..#..##..#..
.#.###.##.....#.###.#.###
####.##...#.#....#..##...
#.....#.#..#.##.#..###..#
"""
