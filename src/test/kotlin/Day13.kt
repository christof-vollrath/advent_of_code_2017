import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 13: Packet Scanners ---

You need to cross a vast firewall.
The firewall consists of several layers, each with a security scanner that moves back and forth across the layer.
To succeed, you must not be detected by a scanner.

By studying the firewall briefly, you are able to record (in your puzzle input)
the depth of each layer and the range of the scanning area for the scanner within it,
written as depth: range. Each layer has a thickness of exactly 1.
A layer at depth 0 begins immediately inside the firewall; a layer at depth 1 would start immediately after that.

For example, suppose you've recorded the following:

0: 3
1: 2
4: 4
6: 4

This means that there is a layer immediately inside the firewall (with range 3),
a second layer immediately after that (with range 2),
a third layer which begins at depth 4 (with range 4),
and a fourth layer which begins at depth 6 (also with range 4).
Visually, it might look like this:

 0   1   2   3   4   5   6
[ ] [ ] ... ... [ ] ... [ ]
[ ] [ ]         [ ]     [ ]
[ ]             [ ]     [ ]
                [ ]     [ ]
Within each layer, a security scanner moves back and forth within its range.
Each security scanner starts at the top and moves down until it reaches the bottom,
then moves up until it reaches the top, and repeats.
A security scanner takes one picosecond to move one step.
Drawing scanners as S, the first few picoseconds look like this:


Picosecond 0:
 0   1   2   3   4   5   6
[S] [S] ... ... [S] ... [S]
[ ] [ ]         [ ]     [ ]
[ ]             [ ]     [ ]
                [ ]     [ ]

Picosecond 1:
 0   1   2   3   4   5   6
[ ] [ ] ... ... [ ] ... [ ]
[S] [S]         [S]     [S]
[ ]             [ ]     [ ]
                [ ]     [ ]

Picosecond 2:
 0   1   2   3   4   5   6
[ ] [S] ... ... [ ] ... [ ]
[ ] [ ]         [ ]     [ ]
[S]             [S]     [S]
                [ ]     [ ]

Picosecond 3:
 0   1   2   3   4   5   6
[ ] [ ] ... ... [ ] ... [ ]
[S] [S]         [ ]     [ ]
[ ]             [ ]     [ ]
                [S]     [S]

Your plan is to hitch a ride on a packet about to move through the firewall.
The packet will travel along the top of each layer, and it moves at one layer per picosecond.
Each picosecond, the packet moves one layer forward (its first move takes it into layer 0),
and then the scanners move one step.
If there is a scanner at the top of the layer as your packet enters it, you are caught.
(If a scanner moves into the top of its layer while you are there, you are not caught:
it doesn't have time to notice you before you leave.) If you were to do this in the configuration above,
marking your current position with parentheses,
your passage through the firewall would look like this:

Initial state:
 0   1   2   3   4   5   6
[S] [S] ... ... [S] ... [S]
[ ] [ ]         [ ]     [ ]
[ ]             [ ]     [ ]
                [ ]     [ ]

Picosecond 0:
 0   1   2   3   4   5   6
(S) [S] ... ... [S] ... [S]
[ ] [ ]         [ ]     [ ]
[ ]             [ ]     [ ]
                [ ]     [ ]

 0   1   2   3   4   5   6
( ) [ ] ... ... [ ] ... [ ]
[S] [S]         [S]     [S]
[ ]             [ ]     [ ]
                [ ]     [ ]


Picosecond 1:
 0   1   2   3   4   5   6
[ ] ( ) ... ... [ ] ... [ ]
[S] [S]         [S]     [S]
[ ]             [ ]     [ ]
                [ ]     [ ]

 0   1   2   3   4   5   6
[ ] (S) ... ... [ ] ... [ ]
[ ] [ ]         [ ]     [ ]
[S]             [S]     [S]
                [ ]     [ ]


Picosecond 2:
 0   1   2   3   4   5   6
[ ] [S] (.) ... [ ] ... [ ]
[ ] [ ]         [ ]     [ ]
[S]             [S]     [S]
                [ ]     [ ]

 0   1   2   3   4   5   6
[ ] [ ] (.) ... [ ] ... [ ]
[S] [S]         [ ]     [ ]
[ ]             [ ]     [ ]
                [S]     [S]


Picosecond 3:
 0   1   2   3   4   5   6
[ ] [ ] ... (.) [ ] ... [ ]
[S] [S]         [ ]     [ ]
[ ]             [ ]     [ ]
                [S]     [S]

 0   1   2   3   4   5   6
[S] [S] ... (.) [ ] ... [ ]
[ ] [ ]         [ ]     [ ]
[ ]             [S]     [S]
                [ ]     [ ]


Picosecond 4:
 0   1   2   3   4   5   6
[S] [S] ... ... ( ) ... [ ]
[ ] [ ]         [ ]     [ ]
[ ]             [S]     [S]
                [ ]     [ ]

 0   1   2   3   4   5   6
[ ] [ ] ... ... ( ) ... [ ]
[S] [S]         [S]     [S]
[ ]             [ ]     [ ]
                [ ]     [ ]


Picosecond 5:
 0   1   2   3   4   5   6
[ ] [ ] ... ... [ ] (.) [ ]
[S] [S]         [S]     [S]
[ ]             [ ]     [ ]
                [ ]     [ ]

 0   1   2   3   4   5   6
[ ] [S] ... ... [S] (.) [S]
[ ] [ ]         [ ]     [ ]
[S]             [ ]     [ ]
                [ ]     [ ]


Picosecond 6:
 0   1   2   3   4   5   6
[ ] [S] ... ... [S] ... (S)
[ ] [ ]         [ ]     [ ]
[S]             [ ]     [ ]
                [ ]     [ ]

 0   1   2   3   4   5   6
[ ] [ ] ... ... [ ] ... ( )
[S] [S]         [S]     [S]
[ ]             [ ]     [ ]
                [ ]     [ ]

In this situation, you are caught in layers 0 and 6,
because your packet entered the layer when its scanner was at the top when you entered it.
You are not caught in layer 1, since the scanner moved into the top of the layer once you were already there.

The severity of getting caught on a layer is equal to its depth multiplied by its range.
(Ignore layers in which you do not get caught.)
The severity of the whole trip is the sum of these values.
In the example above, the trip severity is 0*3 + 6*4 = 24.

Given the details of the firewall you've recorded, if you leave immediately, what is the severity of your whole trip?

Your puzzle answer was 1840.

--- Part Two ---

Now, you need to pass through the firewall without being caught - easier said than done.

You can't control the speed of the packet, but you can delay it any number of picoseconds.
For each picosecond you delay the packet before beginning your trip, all security scanners move one step.
You're not in the firewall during this time; you don't enter layer 0 until you stop delaying the packet.

In the example above, if you delay 10 picoseconds (picoseconds 0 - 9), you won't get caught:

State after delaying:
 0   1   2   3   4   5   6
[ ] [S] ... ... [ ] ... [ ]
[ ] [ ]         [ ]     [ ]
[S]             [S]     [S]
                [ ]     [ ]

Picosecond 10:
 0   1   2   3   4   5   6
( ) [S] ... ... [ ] ... [ ]
[ ] [ ]         [ ]     [ ]
[S]             [S]     [S]
                [ ]     [ ]

 0   1   2   3   4   5   6
( ) [ ] ... ... [ ] ... [ ]
[S] [S]         [S]     [S]
[ ]             [ ]     [ ]
                [ ]     [ ]


Picosecond 11:
 0   1   2   3   4   5   6
[ ] ( ) ... ... [ ] ... [ ]
[S] [S]         [S]     [S]
[ ]             [ ]     [ ]
                [ ]     [ ]

 0   1   2   3   4   5   6
[S] (S) ... ... [S] ... [S]
[ ] [ ]         [ ]     [ ]
[ ]             [ ]     [ ]
                [ ]     [ ]


Picosecond 12:
 0   1   2   3   4   5   6
[S] [S] (.) ... [S] ... [S]
[ ] [ ]         [ ]     [ ]
[ ]             [ ]     [ ]
                [ ]     [ ]

 0   1   2   3   4   5   6
[ ] [ ] (.) ... [ ] ... [ ]
[S] [S]         [S]     [S]
[ ]             [ ]     [ ]
                [ ]     [ ]


Picosecond 13:
 0   1   2   3   4   5   6
[ ] [ ] ... (.) [ ] ... [ ]
[S] [S]         [S]     [S]
[ ]             [ ]     [ ]
                [ ]     [ ]

 0   1   2   3   4   5   6
[ ] [S] ... (.) [ ] ... [ ]
[ ] [ ]         [ ]     [ ]
[S]             [S]     [S]
                [ ]     [ ]


Picosecond 14:
 0   1   2   3   4   5   6
[ ] [S] ... ... ( ) ... [ ]
[ ] [ ]         [ ]     [ ]
[S]             [S]     [S]
                [ ]     [ ]

 0   1   2   3   4   5   6
[ ] [ ] ... ... ( ) ... [ ]
[S] [S]         [ ]     [ ]
[ ]             [ ]     [ ]
                [S]     [S]


Picosecond 15:
 0   1   2   3   4   5   6
[ ] [ ] ... ... [ ] (.) [ ]
[S] [S]         [ ]     [ ]
[ ]             [ ]     [ ]
                [S]     [S]

 0   1   2   3   4   5   6
[S] [S] ... ... [ ] (.) [ ]
[ ] [ ]         [ ]     [ ]
[ ]             [S]     [S]
                [ ]     [ ]


Picosecond 16:
 0   1   2   3   4   5   6
[S] [S] ... ... [ ] ... ( )
[ ] [ ]         [ ]     [ ]
[ ]             [S]     [S]
                [ ]     [ ]

 0   1   2   3   4   5   6
[ ] [ ] ... ... [ ] ... ( )
[S] [S]         [S]     [S]
[ ]             [ ]     [ ]
                [ ]     [ ]

Because all smaller delays would get you caught,
the fewest number of picoseconds you would need to delay to get through safely is 10.

What is the fewest number of picoseconds that you need to delay the packet
to pass through the firewall without being caught?

Your puzzle answer was 3850260.

 */

class Firewall(layersDef: Map<Int, Int>) {
    val layerRanges: List<Int?>
    init {
        val max = layersDef.keys.max()?:0
        layerRanges = (0..max).map {
            layersDef[it]
        }
    }

    fun scannerPositions(psec: Int) =
            layerRanges.mapIndexed { i, range ->
                if (range != null) Pair(i, scannerPosition(psec, range))
                else null
            }
            .filterNotNull()
            .toMap()

    fun scannerPosition(psec: Int, range: Int): Int {
        // Range 3: 0, 1, 2, 1, 0 => 0, 1, 2, (4-3), (4-4)
        val hRange = (range - 1) * 2
        val modSec = psec % hRange
        if (modSec >= range - 1)
            return hRange - modSec
        else return modSec
    }

    fun severity(delay: Int) = layerRanges.mapIndexed { depth, range ->
        if (range != null && scannerPosition(depth + delay, range) == 0)
            depth * range
        else 0
    }.sum()

    fun caught(delay: Int) =
        layerRanges
        .mapIndexed { depth, range -> Pair(depth, range) }
        .any {
            val range = it.second
            range != null && scannerPosition(it.first + delay, range) == 0
        }

    fun optimalDelay(): Int {
        var i = 0
        while(caught(i)) i++
        return i
    }
}

fun parseFirewallDefinition(input: String) =
        input.split("\n")
                .filter { !it.isBlank() }
                .map {
                    val parts = it.split(":").map { it.trim() }
                    Pair(parts[0].toInt(), parts[1].toInt())
                }
                .toMap()

val day13ExampleInput = """
            0: 3
            1: 2
            4: 4
            6: 4
            """

class Day13Spec : Spek({
    describe("layers and scanners") {
        val layers = parseFirewallDefinition(day13ExampleInput)
        val firewall = Firewall(layers)
        val testData = arrayOf(
                //     picosec         scanner positions
                //--|-------------|-------------------------------------
                data(0, mapOf(0 to 0, 1 to 0, 4 to 0, 6 to 0)),
                data(1, mapOf(0 to 1, 1 to 1, 4 to 1, 6 to 1)),
                data(2, mapOf(0 to 2, 1 to 0, 4 to 2, 6 to 2)),
                data(3, mapOf(0 to 1, 1 to 1, 4 to 3, 6 to 3)),
                data(4, mapOf(0 to 0, 1 to 0, 4 to 2, 6 to 2)),
                data(5, mapOf(0 to 1, 1 to 1, 4 to 1, 6 to 1)),
                data(6, mapOf(0 to 2, 1 to 0, 4 to 0, 6 to 0))

        )
        onData("input %s", with = *testData) { psec, expected ->
            it("returns $expected") {
                firewall.scannerPositions(psec) `should equal` expected
            }
        }
    }
    describe("calculate severity") {
        on("only one layer") {
            val firewall = Firewall(parseFirewallDefinition("0: 2"))
            it("should be 0") {
                firewall.severity(0) `should equal` 0
            }
        }
        on("two layers one hiting") {
            val firewall = Firewall(parseFirewallDefinition("0: 2\n 2: 2"))
            it("should be 4") {
                firewall.severity(0) `should equal` 4
            }
        }
        on("example severity") {
            val firewall = Firewall(parseFirewallDefinition(day13ExampleInput))
            it("should be 24 for delay 0") {
                firewall.severity(0) `should equal` 24
            }
            it("should be 0 for delay 10") {
                firewall.severity(10) `should equal` 0
            }
            it("should be 0 for delay 4") {
                firewall.severity(4) `should equal` 0
            }
        }
        on("example caught") {
            val firewall = Firewall(parseFirewallDefinition(day13ExampleInput))
            it("should be true for delay 0") {
                firewall.caught(0) `should equal` true
            }
            it("should be false for delay 10") {
                firewall.caught(10) `should equal` false
            }
            it("should be true for delay 4") { // severity == 0 because caught at depth 0
                firewall.caught(4) `should equal` true
            }
        }
    }
    describe("find delay for severity 0 to get through the firewall") {
        on("example firewaoll") {
            val firewall = Firewall(parseFirewallDefinition(day13ExampleInput))
            it("should be 10") {
                val delay = firewall.optimalDelay()
                firewall.severity(delay) `should equal` 0
                firewall.caught(delay) `should equal` false
                delay `should equal` 10
            }
        }
    }
    describe("severity") {
        on("input") {
            val result = Firewall(parseFirewallDefinition(day13Input)).severity(0)
            println("Severity: $result")
        }
    }
    describe("delay") {
        on("input") {
            val result = Firewall(parseFirewallDefinition(day13Input)).optimalDelay()
            println("delay: $result")
        }
    }

})

val day13Input = """
0: 5
1: 2
2: 3
4: 4
6: 6
8: 4
10: 8
12: 6
14: 6
16: 8
18: 6
20: 9
22: 8
24: 10
26: 8
28: 8
30: 12
32: 8
34: 12
36: 10
38: 12
40: 12
42: 12
44: 12
46: 12
48: 14
50: 12
52: 14
54: 12
56: 14
58: 12
60: 14
62: 14
64: 14
66: 14
68: 14
70: 14
72: 14
76: 14
80: 18
84: 14
90: 18
92: 17
"""
