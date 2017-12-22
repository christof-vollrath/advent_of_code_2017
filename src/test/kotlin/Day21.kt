import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

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


 */

class Day21Spec : Spek({
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
    }


})

fun splitPattern(n: Int, input: List<List<Char>>) =
    input.flatten()
            .withIndex()
            .groupBy { it.index / (input.size / n) }
            .map { it.value.map { it.value }}
            .withIndex()
            .groupBy { it.index % (n*n) }
            .map { it.value.map { it.value }}


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
