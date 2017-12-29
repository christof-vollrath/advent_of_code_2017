
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.coroutines.experimental.buildSequence

/*
--- Day 24: Electromagnetic Moat ---

The CPU itself is a large, black building surrounded by a bottomless pit.
Enormous metal tubes extend outward from the side of the building at regular intervals and descend down into the void.
There's no way to cross, but you need to get inside.

No way, of course, other than building a bridge out of the magnetic components strewn about nearby.

Each component has two ports, one on each end. The ports come in all different types,
and only matching types can be connected.
You take an inventory of the components by their port types (your puzzle input).
Each port is identified by the number of pins it uses;
more pins mean a stronger connection for your bridge.
A 3/7 component, for example, has a type-3 port on one side, and a type-7 port on the other.

Your side of the pit is metallic; a perfect surface to connect a magnetic, zero-pin port.
Because of this, the first port you use must be of type 0.
It doesn't matter what type of port you end with; your goal is just to make the bridge as strong as possible.

The strength of a bridge is the sum of the port types in each component.
For example, if your bridge is made of components 0/3, 3/7, and 7/4,
your bridge has a strength of 0+3 + 3+7 + 7+4 = 24.

For example, suppose you had the following components:

0/2
2/2
2/3
3/4
3/5
0/1
10/1
9/10

With them, you could make the following valid bridges:

0/1
0/1--10/1
0/1--10/1--9/10
0/2
0/2--2/3
0/2--2/3--3/4
0/2--2/3--3/5
0/2--2/2
0/2--2/2--2/3
0/2--2/2--2/3--3/4
0/2--2/2--2/3--3/5

(Note how, as shown by 10/1, order of ports within a component doesn't matter.
However, you may only use each port on a component once.)

Of these bridges, the strongest one is 0/1--10/1--9/10; it has a strength of 0+1 + 1+10 + 10+9 = 31.

What is the strength of the strongest bridge you can make with the components you have available?

Your puzzle answer was 2006.

--- Part Two ---

The bridge you've built isn't long enough; you can't jump the rest of the way.

In the example above, there are two longest bridges:

0/2--2/2--2/3--3/4
0/2--2/2--2/3--3/5

Of them, the one which uses the 3/5 component is stronger; its strength is 0+2 + 2+2 + 2+3 + 3+5 = 19.

What is the strength of the longest bridge you can make?
If you can make multiple bridges of the longest length, pick the strongest one.

Your puzzle answer was 1994.


 */

class Day24Spec : Spek({
    describe("example part1") {
        on("example input") {
            val inputString = day24ExampleInput
            it("should calculate correct strength") {
                val input = parsePorts(inputString)
                val allBridges = constructBridges(input)
                val strongestBridge = findStrongest(allBridges)
                strength(strongestBridge!!) `should equal` 31
            }
        }
    }
    describe("strength calculation") {
        given("bridge") {
            val bridge = listOf(Pair(1,2), Pair(2,5), Pair(4,5))
            it("should have strength as sum of all ports") {
                strength(bridge) `should equal` 19
            }
        }
    }
    describe("construct bridges") {
        on("empty remaining bridges") {
            val port = 9
            val remaining = listOf<Pair<Int, Int>>()
            it("should return empty solutions") {
                constructBridges(port, remaining) `should equal` setOf()
            }
        }
        on("no fitting remaining bridges") {
            val port = 0
            val remaining = listOf(Pair(1, 2))
            it("should return beginnning as the solution") {
                constructBridges(port, remaining) `should equal` setOf()
            }
        }
        on("simple case with only one solution") {
            val port = 9
            val remaining = listOf(Pair(9, 10))
            it("should find that solution") {
                constructBridges(port, remaining) `should equal` setOf(listOf(Pair(9, 10)))
            }
        }
        on("simple case with only another solution") {
            val port = 10
            val remaining = listOf(Pair(8, 10))
            it("should find that solution") {
                constructBridges(port, remaining) `should equal` setOf(listOf(Pair(8, 10)))
            }
        }
        on("two fitting remaining parts") {
            val port = 9
            val remaining = listOf(Pair(9, 10), Pair(7, 9))
            it("should return to solutions") {
                constructBridges(port, remaining) `should equal` setOf(
                        listOf(Pair(9, 10)),
                        listOf(Pair(7, 9))
                )
            }
        }
        on("fitting remaining bridge") {
            val port = 8
            val remaining = listOf(Pair(8, 9), Pair(9, 10), Pair(10, 11))
            it("should return to solutions") {
                constructBridges(port, remaining) `should equal` setOf(
                        listOf(Pair(8, 9)),
                        listOf(Pair(8, 9), Pair(9, 10)),
                        listOf(Pair(8, 9), Pair(9, 10), Pair(10, 11))
                )
            }
        }
        on("example input") {
            val port = 0
            val remaining = listOf(Pair(0,2), Pair(2,2), Pair(2,3), Pair(3,4), Pair(3,5), Pair(0,1), Pair(10,1), Pair(9,10))
            it("should return the example solutions") {
                constructBridges(port, remaining) `should equal` setOf(
                        listOf(Pair(0,1)),
                        listOf(Pair(0,1), Pair(10,1)),
                        listOf(Pair(0,1), Pair(10,1), Pair(9, 10)),
                        listOf(Pair(0,2)),
                        listOf(Pair(0,2), Pair(2,3)),
                        listOf(Pair(0,2), Pair(2,3), Pair(3,4)),
                        listOf(Pair(0,2), Pair(2,3), Pair(3,5)),
                        listOf(Pair(0,2), Pair(2,2)),
                        listOf(Pair(0,2), Pair(2,2), Pair(2,3)),
                        listOf(Pair(0,2), Pair(2,2), Pair(2,3), Pair(3,4)),
                        listOf(Pair(0,2), Pair(2,2), Pair(2,3), Pair(3,5))
                )
            }
        }
    }
    describe("parse ports" ) {
        on("example input string") {
            val input = day24ExampleInput
            it("should get all ports") {
                parsePorts(input) `should equal` listOf(Pair(0,2), Pair(2,2), Pair(2,3), Pair(3,4), Pair(3,5), Pair(0,1), Pair(10,1), Pair(9,10))
            }
        }
    }
    describe("exercise part 1 and 2") {
        on("exercise input") {
            val inputString = day24Input
            val input = parsePorts(inputString)
            val allBridges = constructBridges(input)
            val strongestBridge = findStrongest(allBridges)
            println("strongest bridge: ${strength(strongestBridge!!)} $strongestBridge")
            it("should find the correct strength for the strongestt") {
                strength(strongestBridge) `should equal` 2006
            }
            val longestBridges = findLongestBridges(allBridges)
            val strongestOfTheLongest = findStrongest(longestBridges)
            println("strongest of the longest bridges: ${strength(strongestOfTheLongest!!)} $strongestOfTheLongest")
            it("should find the correct strength for the strongest of the longest") {
                strength(strongestOfTheLongest) `should equal` 1994
            }
        }
    }
    describe("example part2") {
        on("example input") {
            val inputString = day24ExampleInput
            val input = parsePorts(inputString)
            val allBridges = constructBridges(input)
            val longestBridges = findLongestBridges(allBridges)
            it("should find two longest briges") {
                longestBridges `should equal` setOf(
                        listOf(Pair(0, 2), Pair(2, 2), Pair(2, 3), Pair(3, 4)),
                        listOf(Pair(0, 2), Pair(2, 2), Pair(2, 3), Pair(3, 5))
                )
            }
            it("should contains the strongest, longest bridge") {
                val strongestOfTheLongest = findStrongest(longestBridges)
                strength(strongestOfTheLongest!!) `should equal` 19
            }
        }
    }

})

fun strength(bridge: List<Pair<Int, Int>>) =
        bridge.fold(0) { sum, pair ->
            sum + pair.first + pair.second
        }

fun findStrongest(bridges: Set<List<Pair<Int, Int>>>) =
        bridges.maxBy { strength(it) }

fun findLongestBridges(bridges: Set<List<Pair<Int, Int>>>): Set<List<Pair<Int, Int>>> =
        with(bridges.map { it.size }.max()) {
            bridges.filter { it.size == this }
        }.toSet()


fun constructBridges(input: List<Pair<Int, Int>>)= constructBridges(0, input)

fun constructBridges(port: Int, remainingParts: List<Pair<Int,Int>>): Set<List<Pair<Int, Int>>> =
        findAllFittingParts(port, remainingParts).flatMap { currentFitting ->
                setOf(listOf(currentFitting.first)) +
                constructBridges(currentFitting.second, remainingParts - currentFitting.first).map {
                    listOf(currentFitting.first) + it
                }
            }.toSet()

fun findAllFittingParts(port: Int, remainingParts: List<Pair<Int, Int>>) = buildSequence {
    remainingParts.forEach {
        when (port) {
            it.first -> yield(Pair(it, it.second))
            it.second -> yield(Pair(it, it.first))
            else -> {}
        }
    }
}.toList()

fun parsePorts(inputString: String) =
        inputString.split("\n").filter { ! it.isBlank() }
                .map {
                    val parts = it.split("/")
                    Pair(parts[0].trim().toInt(), parts[1].trim().toInt())
                }

val day24ExampleInput = """
0/2
2/2
2/3
3/4
3/5
0/1
10/1
9/10
"""

val day24Input = """
24/14
30/24
29/44
47/37
6/14
20/37
14/45
5/5
26/44
2/31
19/40
47/11
0/45
36/31
3/32
30/35
32/41
39/30
46/50
33/33
0/39
44/30
49/4
41/50
50/36
5/31
49/41
20/24
38/23
4/30
40/44
44/5
0/43
38/20
20/16
34/38
5/37
40/24
22/17
17/3
9/11
41/35
42/7
22/48
47/45
6/28
23/40
15/15
29/12
45/11
21/31
27/8
18/44
2/17
46/17
29/29
45/50
"""