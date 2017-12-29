import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.lang.Math.sqrt
import kotlin.coroutines.experimental.buildSequence
import kotlin.math.roundToInt

/*
--- Day 21: Fractal Art ---

You find a program trying to generate some art.
It uses a strange process that involves repeatedly enhancing the detail of an image through a set of rules.

The image consists of a two-dimensional square grid of pixels that are either on (#) or off (.).
The program always begins with this pattern:

.#.
..#
###

Because the pattern is both 3 pixels wide and 3 pixels tall, it is said to have a size of 3.

Then, the program repeats the following process:

If the size is evenly divisible by 2, break the pixels up into 2x2 squares,
and convert each 2x2 square into a 3x3 square by following the corresponding enhancement rule.
Otherwise, the size is evenly divisible by 3; break the pixels up into 3x3 squares,
and convert each 3x3 square into a 4x4 square by following the corresponding enhancement rule.
Because each square of pixels is replaced by a larger one, the image gains pixels and so its size increases.

The artist's book of enhancement rules is nearby (your puzzle input); however, it seems to be missing rules.
The artist explains that sometimes, one must rotate or flip the input pattern to find a match.
(Never rotate or flip the output pattern, though.)
Each pattern is written concisely: rows are listed as single units, ordered top-down, and separated by slashes.
For example, the following rules correspond to the adjacent patterns:

../.#  =  ..
          .#

                .#.
.#./..#/###  =  ..#
                ###

                        #..#
#..#/..../#..#/.##.  =  ....
                        #..#
                        .##.

When searching for a rule to use, rotate and flip the pattern as necessary.
For example, all of the following patterns match the same rule:

.#.   .#.   #..   ###
..#   #..   #.#   ..#
###   ###   ##.   .#.

Suppose the book contained the following two rules:

../.# => ##./#../...
.#./..#/### => #..#/..../..../#..#
As before, the program begins with this pattern:

.#.
..#
###
The size of the grid (3) is not divisible by 2, but it is divisible by 3. It divides evenly into a single square; the square matches the second rule, which produces:

#..#
....
....
#..#
The size of this enhanced grid (4) is evenly divisible by 2, so that rule is used. It divides evenly into four squares:

#.|.#
..|..
--+--
..|..
#.|.#
Each of these squares matches the same rule (../.# => ##./#../...), three of which require some flipping and rotation to line up with the rule. The output for the rule is the same in all four cases:

##.|##.
#..|#..
...|...
---+---
##.|##.
#..|#..
...|...
Finally, the squares are joined into a new grid:

##.##.
#..#..
......
##.##.
#..#..
......
Thus, after 2 iterations, the grid contains 12 pixels that are on.

How many pixels stay on after 5 iterations?

Your puzzle answer was 158.


--- Part Two ---

How many pixels stay on after 18 iterations?

Your puzzle answer was 2301762.


 */

class Day21Spec : Spek({
    describe("part 1") {
        it("day21 input") {
            val result = iterations(5, day21Input, day21Rules)
            println(result)
            println("Number of pixels after 5 iterations: ${countPixels(result)}")
        }
    }
    describe("part 2") {
        it("day21 input") {
            val result = iterations(18, day21Input, day21Rules)
            println("Number of pixels after 18 iterations: ${countPixels(result)}")
        }
    }
    describe("find start pattern in example rules") {
        on("example rules") {
            val rules = parseRules(day21ExampleRulesString)
            it("should be found") {
                rules.flatMap { createVariants(it.first) } `should contain` day21Input
            }
        }
        on("exercise rules") {
            val rules = parseRules(day21RulesString)
            it("should be found") {
                rules.flatMap { createVariants(it.first) } `should contain` day21Input
            }
        }

    }
    describe("multiple iterations") {
        on("two iteration") {
            val result = iterations(2, day21Input, day21ExampleRules)
            it("should have the correct result") {
                result `should equal` parsePattern("##.##./#..#../....../##.##./#..#../......")
                countPixels(result) `should equal` 12
            }
        }
    }
    describe("single iterations") {
        lateinit var afterIteration1: List<List<Char>>
        on("first iteration") {
            afterIteration1 = iterate(day21Input, day21ExampleRules)
            it("should have the values of the first iteration") {
                afterIteration1 `should equal` parsePattern("#..#/..../..../#..#")
            }
        }
        on("second iteration") {
            val afterIteration2 = iterate(afterIteration1, day21ExampleRules)
            it("should have the values of the second iteration") {
                afterIteration2 `should equal` parsePattern("##.##./#..#../....../##.##./#..#../......")
                countPixels(afterIteration2) `should equal` 12
            }
        }
    }
    describe("join parts") {
        on("only one part") {
            val input = listOf(
                    listOf(listOf('#'))
            )
            it("should return the part") {
                joinParts(input) `should equal`listOf(listOf('#'))
            }
        }
        on("four parts") {
            val input = listOf(
                    listOf(listOf('#')), listOf(listOf('.')),
                    listOf(listOf('.')), listOf(listOf('#'))
            )
            it("should return the part") {
                joinParts(input) `should equal`listOf(listOf('#', '.'), listOf('.', '#'))
            }
        }
        on("more parts") {
            val input = listOf(
                    listOf(listOf('#', '.'), listOf('.', '.')),
                    listOf(listOf('.', '#'), listOf('.', '.')),
                    listOf(listOf('.', '.'), listOf('#', '.')),
                    listOf(listOf('.', '.'), listOf('.', '#'))
            )
            it("should return the part") {
                joinParts(input) `should equal`listOf(
                        listOf('#', '.', '.', '#'),
                        listOf('.', '.', '.', '.'),
                        listOf('.', '.', '.', '.'),
                        listOf('#', '.', '.', '#')
                )
            }
        }
    }
    describe("zip lists") {
        on("list of noting") {
            val lists = listOf<List<Char>>()
            it("should be this list") {
                zipLists(lists) `should equal` lists
            }
        }
        on("list of some lists") {
            val lists = listOf(listOf('a', 'b', 'c'), listOf('d', 'e', 'f'), listOf('g', 'h', 'i'))
            it("should zip alls lists") {
                zipLists(lists) `should equal` listOf(listOf('a', 'd', 'g'), listOf('b', 'e', 'h'), listOf('c', 'f', 'i'))
            }
        }
    }
    describe("create rules map") {
        on("some rules") {
            val rules = createRulesMap(parseRules("""
            .../.../.## => ##./#../...
            .#./..#/### => #..#/..../..../#..#
            """))
            it("should contain the correct mappings") {
                rules[parsePattern(".../.../.##")] `should equal` parsePattern("##./#../...")
                rules[parsePattern(".../.../##.")] `should equal` parsePattern("##./#../...")
                rules[parsePattern(".##/.../...")] `should equal` parsePattern("##./#../...")
                rules[parsePattern(".../#../#..")] `should equal` parsePattern("##./#../...")
                rules[parsePattern("##./.../...")] `should equal` parsePattern("##./#../...")
                rules[parsePattern("..#/..#/...")] `should equal` parsePattern("##./#../...")
                rules[parsePattern(".../..#/..#")] `should equal` parsePattern("##./#../...")
            }
        }
    }
    describe("pattern variants") {
        on("start pattern") {
            val variants = createVariants(day21Input)
            it("should contain all variants of the start pattern") {
                /*
                    .#.   .#.   #..   ###
                    ..#   #..   #.#   ..#
                    ###   ###   ##.   .#.
                 */
                variants `should contain` parsePattern(".#./..#/###")
                variants `should contain` parsePattern(".#./#../###")
                variants `should contain` parsePattern("#../#.#/##.")
                variants `should contain` parsePattern("###/..#/.#.")
                variants.size `should equal` 12
            }
        }
    }
    describe("split grid") {
        on("3x3 grid") {
            val input = parsePattern(".#./..#/###")
            it("should return the same grid") {
                splitPattern(input) `should equal` listOf(parsePattern(".#./..#/###"))
            }
        }
        on("4x4 grid") {
            val input = parsePattern("#..#/..../..../#..#")
            it("should return the same grid") {
                splitPattern(input) `should equal` listOf(
                        parsePattern("#./.."),
                        parsePattern(".#/.."),
                        parsePattern("../#."),
                        parsePattern("../.#")
                )
            }
        }
    }
    describe("rotate and flip") {
        on("example 3x3") {
            val input = listOf(
                    listOf('.', '#', '.'),
                    listOf('.', '.', '#'),
                    listOf('#', '#', '#')
            )
            it("flip vertical") {
                flipV(input) `should equal` listOf(
                        listOf('.', '#', '.'),
                        listOf('#', '.', '.'),
                        listOf('#', '#', '#')
                )
            }
            it("flip horizontal") {
                flipH(input) `should equal` listOf(
                        listOf('#', '#', '#'),
                        listOf('.', '.', '#'),
                        listOf('.', '#', '.')
                )
            }
            it("rotate 90 degrees") {
                rotate90(input) `should equal` listOf(
                        listOf('#', '.', '.'),
                        listOf('#', '.', '#'),
                        listOf('#', '#', '.')
                )
            }
            it("rotate 180 degrees") {
                rotate90(rotate90(input)) `should equal` listOf(
                        listOf('#', '#', '#'),
                        listOf('#', '.', '.'),
                        listOf('.', '#', '.')
                )
            }
            it("rotate 270 degrees") {
                rotate90(rotate90(rotate90(input))) `should equal` listOf(
                        listOf('.', '#', '#'),
                        listOf('#', '.', '#'),
                        listOf('.', '.', '#')
                )
            }
        }
        on("example 2x2") {
            val input = listOf(
                    listOf('.', '#'),
                    listOf('#', '#')
            )
            it("flip vertical") {
                flipV(input) `should equal` listOf(
                        listOf('#', '.'),
                        listOf('#', '#')
                )
            }
            it("flip horizontal") {
                flipH(input) `should equal` listOf(
                        listOf('#', '#'),
                        listOf('.', '#')
                )
            }
            it("rotate 90 degrees") {
                rotate90(input) `should equal` listOf(
                        listOf('#', '.'),
                        listOf('#', '#')
                )
            }
            it("rotate 180 degrees") {
                rotate90(rotate90(input)) `should equal` listOf(
                        listOf('#', '#'),
                        listOf('#', '.')
                )
            }
            it("rotate 270 degrees") {
                rotate90(rotate90(rotate90(input))) `should equal` listOf(
                        listOf('#', '#'),
                        listOf('.', '#')
                )
            }
        }
    }
    describe("parser") {
        on("example input for pattern parser") {
            val input = "../.#"
            it("should parse correctly") {
                parsePattern(input) `should equal` listOf(
                        listOf('.', '.'),
                        listOf('.', '#')
                )
            }
        }
        on("example input for rule parser") {
            val input = "../.# => ##./#../..."
            it("should parse correctly") {
                parseRule(input) `should equal` Pair(
                    listOf(
                        listOf('.', '.'),
                        listOf('.', '#')
                    ),
                    listOf(
                            listOf('#', '#', '.'),
                            listOf('#', '.', '.'),
                            listOf('.', '.', '.')
                    ))
            }
        }
        on("example input for rules parser") {
            val input = """
                ../.# => ##./#../...
                .#./..#/### => #..#/..../..../#..#
                """
            it("should parse correctly") {
                parseRules(input) `should equal` listOf(
                    Pair(
                        listOf(
                                listOf('.', '.'),
                                listOf('.', '#')
                        ),
                        listOf(
                                listOf('#', '#', '.'),
                                listOf('#', '.', '.'),
                                listOf('.', '.', '.')
                        )),
                    Pair(
                            listOf(
                                    listOf('.', '#', '.'),
                                    listOf('.', '.', '#'),
                                    listOf('#', '#', '#')
                            ),
                            listOf(
                                    listOf('#', '.', '.', '#'),
                                    listOf('.', '.', '.', '.'),
                                    listOf('.', '.', '.', '.'),
                                    listOf('#', '.', '.', '#')
                            ))
                    )
            }
        }
    }
    describe("split pattern") {
        on("split by 1") {
            val input = listOf(
                            listOf('.', '.'),
                            listOf('.', '#')
                        )
            it("should be original") {
                splitPattern(1, input) `should equal` listOf(input)
            }
        }
        on("split by 2") {
            val input = listOf(
                    listOf('#', '.', '.', '#'),
                    listOf('.', '.', '.', '.'),
                    listOf('.', '.', '.', '.'),
                    listOf('#', '.', '.', '#')
            )

            it("should 4 patterns") {
                splitPattern(2, input) `should equal` listOf(
                        listOf(
                            listOf('#', '.'),
                            listOf('.', '.')
                        ),
                        listOf(
                            listOf('.', '#'),
                            listOf('.', '.')
                        ),
                        listOf(
                            listOf('.', '.'),
                            listOf('#', '.')
                        ),
                        listOf(
                            listOf('.', '.'),
                            listOf('.', '#')
                        )
                )
            }
        }
        on("split by 3") {
            val input = listOf(
                    listOf('#', '.', '#', '#',  '.', '#'),
                    listOf('.', '.', '.', '.', '.', '.'),
                    listOf('#', '.', '#', '#',  '.', '#'),
                    listOf('#', '.', '#', '#',  '.', '#'),
                    listOf('.', '.', '.', '.', '.', '.'),
                    listOf('#', '.', '#', '#',  '.', '#')
            )

            it("should 9 patterns") {
                splitPattern(3, input) `should equal` listOf(
                        listOf(
                                listOf('#', '.'),
                                listOf('.', '.')
                        ),
                        listOf(
                                listOf('#', '#'),
                                listOf('.', '.')
                        ),
                        listOf(
                                listOf('.', '#'),
                                listOf('.', '.')
                        ),
                        listOf(
                                listOf('#', '.'),
                                listOf('#', '.')
                        ),
                        listOf(
                                listOf('#', '#'),
                                listOf('#', '#')
                        ),
                        listOf(
                                listOf('.', '#'),
                                listOf('.', '#')
                        ),
                        listOf(
                                listOf('.', '.'),
                                listOf('#', '.')
                        ),
                        listOf(
                                listOf('.', '.'),
                                listOf('#', '#')
                        ),
                        listOf(
                                listOf('.', '.'),
                                listOf('.', '#')
                        )
                )
            }
        }
    }

    describe("split list") {
        on("split list in two parts") {
            val input = listOf('a', 'b', 'c', 'd')
            it("should return list of two lists") {
                input.split(2) `should equal` listOf(listOf('a', 'b'), listOf('c', 'd'))
            }
        }
        on("split list in three parts with a more compex logic") {
            val input = listOf('a', 'b', 'c', 'd', 'e', 'f')
            it("should return list of three lists") {
                input.split { it % 3 } `should equal` listOf(listOf('a', 'd'), listOf('b', 'e'), listOf('c', 'f'))
            }
        }
    }
})

fun iterations(nr: Int, pattern: List<List<Char>>, rules: Map<List<List<Char>>, List<List<Char>>>) =
        (1..nr).fold(pattern) { r, _ ->
            val result = iterate(r, rules)
            result
        }

fun countPixels(pattern: List<List<Char>>) = pattern.flatten().filter { it == '#'}.count()

fun <T> zipLists(lists: List<List<T>>): List<List<T>> {
    val iterators = lists.map { it.iterator() }
    val result = ArrayList<List<T>>(lists.size)
    while(!iterators.isEmpty() && iterators.all { it.hasNext() } ) {
        val zippedElement = iterators.map { it.next() }
        result.add(zippedElement)
    }
    return result
}

fun iterate(input: List<List<Char>>, rules: Map<List<List<Char>>, List<List<Char>>>) =
        joinParts(splitPattern(input).map { applyRules(it, rules)})

fun joinParts(parts: List<List<List<Char>>>): List<List<Char>> { //= parts.first()
    val nrInGroup = sqrt(parts.size.toDouble()).roundToInt()
    val nrParts = parts.size / nrInGroup
    val splitted = parts.split(nrParts) // rows
    return splitted.flatMap { zipLists(it).map { it.flatten()} }
}

fun applyRules(pattern: List<List<Char>>, rules: Map<List<List<Char>>, List<List<Char>>>) = rules[pattern] ?: pattern

fun splitPattern(input: List<List<Char>>) =
        when {
            input.size % 2 == 0 -> splitPattern(input.size / 2, input)
            input.size % 3 == 0 -> splitPattern(input.size / 3, input)
            else -> listOf(input)
        }

fun <E> List<E>.split(nrParts: Int) = this.split { it / (this.size / nrParts) }
fun <E> List<E>.split(splitter: (Int) -> Int) =
        this.withIndex()
                .groupBy { splitter(it.index) }
                .map { it.value.map { it.value }}

fun splitPattern(n: Int, input: List<List<Char>>) =
    input.flatMap { it.split(n) } // rows
        .split { it %n + (it / input.size) * n } // rearranged

fun createRulesMap(rules: List<Pair<List<List<Char>>, List<List<Char>>>>) = buildSequence {
    rules.forEach { rule ->
        createVariants(rule.first).forEach { variant ->
            yield(Pair(variant, rule.second))
        }
    }
}.toMap()

fun createVariants(pattern: List<List<Char>>): List<List<List<Char>>> =
        listOf(pattern, flipV(pattern), flipH(pattern)).flatMap { createRotateVariants(it) }

fun createRotateVariants(pattern: List<List<Char>>): List<List<List<Char>>> = listOf(
        pattern, rotate90(pattern), rotate90(rotate90(pattern)), rotate90(rotate90(rotate90(pattern)))
)

fun parseRules(input: String) =
        input.split("\n")
                .filter { ! it.isBlank() }
                .map { parseRule(it) }

fun parseRule(input: String) = with(input.split("=>")) {
    Pair(parsePattern(this[0].trim()), parsePattern(this[1].trim()))
}

fun parsePattern(input: String) = input.trim().split("/").map { it.map { it } }

fun rotate90(input: List<List<Char>>) = with(input.size-1) {
    (0..this).map { x ->
        (0..this).map { y ->
            input[this-y][x]
        }
    }
}

fun flipH(input: List<List<Char>>) = input.reversed()
fun flipV(input: List<List<Char>>) = input.map { it.reversed() }

val day21Input = parsePattern(".#./..#/###")
val day21ExampleRulesString ="""
        ../.# => ##./#../...
        .#./..#/### => #..#/..../..../#..#
        """
val day21ExampleRules = createRulesMap(parseRules(day21ExampleRulesString))

val day21RulesString = """
../.. => .../.##/##.
#./.. => .##/.##/#..
##/.. => ..#/.../###
.#/#. => #.#/..#/##.
##/#. => .#./.#./..#
##/## => #.#/#../###
.../.../... => ..../#.../.##./..#.
#../.../... => ####/#.##/##.#/..#.
.#./.../... => ..##/..##/..##/..##
##./.../... => ..../..#./##../##.#
#.#/.../... => ##.#/..../####/...#
###/.../... => .#.#/.###/.#../.#.#
.#./#../... => .###/#.#./...#/##..
##./#../... => #.##/#.../####/###.
..#/#../... => ####/...#/...#/#.##
#.#/#../... => .#../##../..##/..#.
.##/#../... => .#../..##/..../.##.
###/#../... => #.../..#./.#.#/#..#
.../.#./... => #.#./.#.#/.###/...#
#../.#./... => ###./.#../...#/.#..
.#./.#./... => ##.#/.#../#..#/##..
##./.#./... => #..#/...#/.#.#/###.
#.#/.#./... => .##./#.../#..#/.###
###/.#./... => .#.#/##.#/..../##.#
.#./##./... => ##.#/#.##/.#.#/#.##
##./##./... => #.##/..#./..#./.##.
..#/##./... => ..../#.../..#./..##
#.#/##./... => .##./####/####/####
.##/##./... => #.##/####/#.##/#..#
###/##./... => .#../.###/##../...#
.../#.#/... => ...#/...#/#.##/####
#../#.#/... => ..#./..#./###./.##.
.#./#.#/... => .##./##../.###/.#.#
##./#.#/... => #.#./.#../.##./...#
#.#/#.#/... => ##.#/..##/#.../##.#
###/#.#/... => ..##/##../.#.#/..##
.../###/... => .#../#.../.##./....
#../###/... => ..##/..##/...#/.##.
.#./###/... => #..#/..#./#.#./..##
##./###/... => #.##/.#../##.#/##.#
#.#/###/... => ####/###./.##./...#
###/###/... => #..#/#.##/..../.##.
..#/.../#.. => #.#./.#../##../..#.
#.#/.../#.. => ##.#/####/##../.#.#
.##/.../#.. => ####/##../#..#/..#.
###/.../#.. => ##../..#./####/##.#
.##/#../#.. => ##../#.#./###./..##
###/#../#.. => ..../.#../#..#/...#
..#/.#./#.. => ..#./...#/.###/.#.#
#.#/.#./#.. => ###./..../#.#./###.
.##/.#./#.. => ####/#.##/.#.#/.#..
###/.#./#.. => ###./#.##/##../####
.##/##./#.. => ##.#/..##/..#./.#..
###/##./#.. => ##.#/.##./.###/.##.
#../..#/#.. => #.../###./##.#/#..#
.#./..#/#.. => ..##/.###/...#/..#.
##./..#/#.. => ##../#.#./...#/.#..
#.#/..#/#.. => ..#./###./##../.###
.##/..#/#.. => #.../.##./..../#.#.
###/..#/#.. => .#.#/#.##/#.##/..#.
#../#.#/#.. => ..##/..##/#.../####
.#./#.#/#.. => #.../...#/..../..##
##./#.#/#.. => ###./..##/.#../.##.
..#/#.#/#.. => ...#/..##/..#./.#..
#.#/#.#/#.. => #.#./.#../..../##..
.##/#.#/#.. => ..#./.###/##.#/....
###/#.#/#.. => #.##/..##/...#/##..
#../.##/#.. => #.#./##../###./.#.#
.#./.##/#.. => .###/#..#/.##./....
##./.##/#.. => .#.#/.#../.###/.##.
#.#/.##/#.. => .#../..##/###./#.##
.##/.##/#.. => ##../.##./..#./.#..
###/.##/#.. => .#.#/..#./#..#/.###
#../###/#.. => #.##/#..#/.#.#/#.#.
.#./###/#.. => #.../#..#/#.../.#.#
##./###/#.. => ##../####/##../.###
..#/###/#.. => #.../..../####/##.#
#.#/###/#.. => ...#/..../...#/..##
.##/###/#.. => .#../####/#.##/.#..
###/###/#.. => ###./.#.#/#.../##..
.#./#.#/.#. => ...#/##../####/...#
##./#.#/.#. => ####/#..#/###./#.##
#.#/#.#/.#. => .###/#..#/..#./...#
###/#.#/.#. => ###./.###/##.#/###.
.#./###/.#. => #..#/#.../..#./####
##./###/.#. => #.../..../#..#/..##
#.#/###/.#. => #..#/.#.#/#.../##..
###/###/.#. => .#.#/..../.#.#/#.##
#.#/..#/##. => .#../..##/...#/###.
###/..#/##. => .###/..#./##.#/##.#
.##/#.#/##. => ####/#.##/.##./##..
###/#.#/##. => #..#/#..#/####/#.##
#.#/.##/##. => .###/#.#./#..#/.#.#
###/.##/##. => #.#./#.#./#.##/..##
.##/###/##. => ####/###./##.#/##.#
###/###/##. => ##../..##/#.#./#...
#.#/.../#.# => .#../###./.###/##.#
###/.../#.# => ..../.#.#/#..#/##..
###/#../#.# => ..#./#.../.##./...#
#.#/.#./#.# => ...#/#.../##.#/.##.
###/.#./#.# => ..../..../#.#./##.#
###/##./#.# => .#../...#/...#/###.
#.#/#.#/#.# => ...#/#.../##../.###
###/#.#/#.# => #.../...#/.#../#.##
#.#/###/#.# => ..../.##./..../##..
###/###/#.# => .##./.#.#/#.##/.##.
###/#.#/### => #.#./####/.##./.##.
###/###/### => .#.#/..##/#.##/.##.
"""
val day21Rules = createRulesMap(parseRules(day21RulesString))

