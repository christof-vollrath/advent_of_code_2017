import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 2: Corruption Checksum ---

As you walk through the door, a glowing humanoid shape yells in your direction.
"You there! Your state appears to be idle.
Come help us repair the corruption in this spreadsheet - if we take another millisecond,
we'll have to display an hourglass cursor!"

The spreadsheet consists of rows of apparently-random numbers.
To make sure the recovery process is on the right track,
they need you to calculate the spreadsheet's checksum.
For each row, determine the difference between the largest value and the smallest value;
the checksum is the sum of all of these differences.

For example, given the following spreadsheet:

5 1 9 5
7 5 3
2 4 6 8
The first row's largest and smallest values are 9 and 1, and their difference is 8.
The second row's largest and smallest values are 7 and 3, and their difference is 4.
The third row's difference is 6.
In this example, the spreadsheet's checksum would be 8 + 4 + 6 = 18.

What is the checksum for the spreadsheet in your puzzle input?

Your puzzle answer was 44887.


--- Part Two ---

"Great work; looks like we're on the right track after all. Here's a star for your effort."
However, the program seems a little worried. Can programs be worried?

"Based on what we're seeing, it looks like all the User wanted is some information
about the evenly divisible values in the spreadsheet.
Unfortunately, none of us are equipped for that kind of calculation - most of us specialize in bitwise operations."

It sounds like the goal is to find the only two numbers in each row where one evenly divides the other
- that is, where the result of the division operation is a whole number.
They would like you to find those numbers on each line, divide them, and add up each line's result.

For example, given the following spreadsheet:

5 9 2 8
9 4 7 3
3 8 6 5

In the first row, the only two numbers that evenly divide are 8 and 2; the result of this division is 4.
In the second row, the two numbers are 9 and 3; the result is 3.
In the third row, the result is 2.
In this example, the sum of the results would be 4 + 3 + 2 = 9.

What is the sum of each row's result in your puzzle input?

Your puzzle answer was 242.

 */

fun parse(string: String) =
    if (string.isEmpty()) listOf()
    else string.split("\n")
        .filter { ! it.isBlank() }
        .map {
            it.split("""\s+""".toRegex())
                .filter { ! it.isBlank() }
                .map { Integer.parseInt(it) }
        }


fun checksum(spreadsheet: String, algo: (List<Int>) -> Int = { (it.max()?:0) - (it.min()?:0) }) = parse(spreadsheet)
        .map(algo)
        .sum()

class Day2Spec : Spek({
    describe("parse spreadsheet to list of lists") {
        on("spreadsheet as empty string") {
            val spreadsheet = ""

            it("should be empty list of lists") {
                parse(spreadsheet) `should equal` listOf()
            }
        }
        on("spreadsheet of one line with one integer") {
            val spreadsheet = "1"

            it("should be list of a list with that integer") {
                parse(spreadsheet) `should equal` listOf(listOf(1))
            }
        }
        on("spreadsheet of one line with two integers") {
            val spreadsheet = "1    2"

            it("should be list of a list with these integers") {
                parse(spreadsheet) `should equal` listOf(listOf(1, 2))
            }
        }
        on("spreadsheet of two lines with integers") {
            val spreadsheet = """
                1   2
                3
                """

            it("should be list of a list with these integers") {
                parse(spreadsheet) `should equal` listOf(listOf(1, 2), listOf(3))
            }
        }

        on("spreadsheet as string") {
            val spreadsheet = """
                5   1   9   5
                7   5   3
                2   4   6   8
                """

            it("should be ") {
                parse(spreadsheet) `should equal` listOf(
                        listOf(5, 1, 9, 5),
                        listOf(7, 5, 3),
                        listOf(2, 4, 6, 8)
                )
            }
        }
    }

    describe("checksum of spreadsheet") {
        on("empty spreadsheet") {
            it("should be 0") {
                checksum("") `should equal` 0
            }
        }
        on("spreadsheet with one cell") {
            it("should be 0 because max == min") {
                checksum("") `should equal` 0
            }
        }
        on("spreadsheet with one line") {
            it("should be max - min") {
                checksum("1 2   3") `should equal` 2
            }
        }
        on("spreadsheet example") {
            val spreadsheet = """
                5   1   9   5
                7   5   3
                2   4   6   8
                """
            it("should be max - min") {
                checksum(spreadsheet) `should equal` 18
            }
        }
    }

    describe("checksum") {
        println("Checksum: ${checksum(day2Input)}")
    }

    describe("evenly divides two numbers") {
        val testData = arrayOf(
                //       x      y   result
                //--|-----|-------|--------------
                data( 4,  2, true),
                data( 4,  3, false),
                data( 9,  3, true),
                data( 3,  6, true),
                data( 2,  2, false)
        )
        onData("input %s", with = *testData) { x, y, expected ->
            it("returns $expected") {
                evenlyDivides(x, y) `should equal` expected
            }
        }
    }

    describe("evenly dividing two numbers") {
        val testData = arrayOf(
                //       list   result
                //--|-------------|--------------
                data(listOf(5, 9, 2, 8), Pair(2, 8)),
                data(listOf(9, 4, 7, 3), Pair(9, 3)),
                data(listOf(3, 8, 6, 5), Pair(3, 6))
        )
        onData("input %s", with = *testData) { list, expected ->
            it("returns $expected") {
                evenlyDividing(list) `should equal` expected
            }
        }
    }

    describe("checksum of spreadsheet evenly divide") {
        on("spreadsheet example") {
            val spreadsheet = """
                5 9 2 8
                9 4 7 3
                3 8 6 5
                """
            it("should be max - min") {
                checksum(spreadsheet) { evenlyDividingChecksum(it) } `should equal` 9
            }
        }
    }

    describe("evenly dividing checksum") {
        println("Evenly Dividing Checksum: ${checksum(day2Input){ evenlyDividingChecksum(it) }}")
    }


})

fun evenlyDividing(list: List<Int>): Pair<Int, Int>? =
    if (list.isNotEmpty()) {
        val first = list.first()
        val tail = list.drop(1)
        tail.forEach {
            if (evenlyDivides(first, it))
                return Pair(first, it)
        }
        evenlyDividing(tail)
    } else null


fun evenlyDivides(x: Int, y: Int) =
    when {
        (x > y) -> x % y == 0
        (x < y) -> y % x == 0
        else -> false
    }

fun evenlyDividingChecksum(list: List<Int>) = with(evenlyDividing(list)) {
    if (this != null) {
        val x = this.first
        val y = this.second
        if (x > y)  x/y else y/x
    } else 0
}

val day2Input =
        """
409	194	207	470	178	454	235	333	511	103	474	293	525	372	408	428
4321	2786	6683	3921	265	262	6206	2207	5712	214	6750	2742	777	5297	3764	167
3536	2675	1298	1069	175	145	706	2614	4067	4377	146	134	1930	3850	213	4151
2169	1050	3705	2424	614	3253	222	3287	3340	2637	61	216	2894	247	3905	214
99	797	80	683	789	92	736	318	103	153	749	631	626	367	110	805
2922	1764	178	3420	3246	3456	73	2668	3518	1524	273	2237	228	1826	182	2312
2304	2058	286	2258	1607	2492	2479	164	171	663	62	144	1195	116	2172	1839
114	170	82	50	158	111	165	164	106	70	178	87	182	101	86	168
121	110	51	122	92	146	13	53	34	112	44	160	56	93	82	98
4682	642	397	5208	136	4766	180	1673	1263	4757	4680	141	4430	1098	188	1451
158	712	1382	170	550	913	191	163	459	1197	1488	1337	900	1182	1018	337
4232	236	3835	3847	3881	4180	4204	4030	220	1268	251	4739	246	3798	1885	3244
169	1928	3305	167	194	3080	2164	192	3073	1848	426	2270	3572	3456	217	3269
140	1005	2063	3048	3742	3361	117	93	2695	1529	120	3480	3061	150	3383	190
489	732	57	75	61	797	266	593	324	475	733	737	113	68	267	141
3858	202	1141	3458	2507	239	199	4400	3713	3980	4170	227	3968	1688	4352	4168
"""

