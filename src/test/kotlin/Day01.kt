
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/*
--- Day 1: Inverse Captcha ---

You're standing in a room with "digitization quarantine" written in LEDs along one wall.
The only door is locked, but it includes a small interface.
"Restricted Area - Strictly No Digitized Users Allowed."

It goes on to explain that you may only leave by solving a captcha to prove you're not a human.
Apparently, you only get one millisecond to solve the captcha: too fast for a normal human,
but it feels like hours to you.

The captcha requires you to review a sequence of digits (your puzzle input)
and find the sum of all digits that match the next digit in the list.
The list is circular, so the digit after the last digit is the first digit in the list.

For example:

1122 produces a sum of 3 (1 + 2) because the first digit (1) matches the second digit and the third digit (2) matches the fourth digit.
1111 produces 4 because each digit (all 1) matches the next.
1234 produces 0 because no digit matches the next.
91212129 produces 9 because the only digit that matches the next one is the last digit, 9.

What is the solution to your captcha?

Your puzzle answer was 1171.


--- Part Two ---

You notice a progress bar that jumps to 50% completion.
Apparently, the door isn't yet satisfied, but it did emit a star as encouragement.
The instructions change:

Now, instead of considering the next digit, it wants you to consider the digit halfway around the circular list.
That is, if your list contains 10 items, only include a digit in your sum if the digit 10/2 = 5 steps forward matches it.
Fortunately, your list has an even number of elements.

For example:

1212 produces 6: the list contains 4 items, and all four digits match the digit 2 items ahead.
1221 produces 0, because every comparison is between a 1 and a 2.
123425 produces 4, because both 2s match each other, but no other digit has a match.
123123 produces 12.
12131415 produces 4.
What is the solution to your new captcha?

Your puzzle answer was 1024.


*/

fun captcha(list: List<Int>, n: Int = 1) =
        (list zip list.shiftLeftCircular(n))
        .filter { it.first == it.second}
        .map {it.first}
        .sum()

fun captcha(string: String, n: Int = 1) = captcha(string.splitToDigits(), n)

fun <E> List<E>.shiftLeftCircular(n: Int = 1) =
        if (this.isEmpty()) this
        else {
            val partSize = n % this.size
            this.drop(partSize) + this.subList(0, partSize)
        }

fun CharSequence.splitToDigits() =
        if (this.isEmpty()) listOf()
        else this.map { Character.getNumericValue(it) }


class Day1Spec : Spek({

    describe("part 1") {
        on("captcha with distance 1") {
            val input = readResource("day01Input.txt")
            val captcha = captcha(input)
            println("captcha=$captcha")
            it("should have the correct result") {
                captcha `should equal` 1171
            }
        }

        describe("captcha from list") {
            on("empty list") {
                val list = listOf<Int>()

                it("should be 0") {
                    captcha(list) `should equal` 0
                }
            }
            on("1") {
                val list = listOf(1)

                it("should be 1") {
                    captcha(list) `should equal` 1
                }
            }
            on("12") {
                val list = listOf(1, 2)

                it("should be 0") {
                    captcha(list) `should equal` 0
                }
            }
        }

        describe("captcha from String") {
            on("given examples") {
                it("should be as indicated") {
                    captcha("1122") `should equal` 3
                    captcha("1111") `should equal` 4
                    captcha("1234") `should equal` 0
                    captcha("91212129") `should equal` 9
                }
            }

        }
    }

    describe("part 2") {
        on("captcha with distance half the input") {
            val input = readResource("day01Input.txt")
            val captcha2 = captcha(input, input.length / 2)
            println("captcha2=$captcha2")
            it("should have the correct result") {
                captcha2 `should equal` 1024
            }
        }

        describe("captcha 2 from String") {
            on("given examples") {
                it("should be as indicated") {
                    captcha("1212", 2) `should equal` 6
                    captcha("1221", 2) `should equal` 0
                    captcha("123425", 3) `should equal` 4
                    captcha("123123", 3) `should equal` 12
                    captcha("12131415", 4) `should equal` 4
                }
            }

        }
    }

    describe("helper functions") {
        describe("shiftLeftCircular elements in a list") {
            on("shiftLeftCircular empty list") {
                val list = listOf<Int>()

                it("should stay empty") {
                    list.shiftLeftCircular() `should equal` list
                }
            }
            on("shiftLeftCircular of list with one element") {
                val list = listOf(1)

                it("should be the same list because of round shiftLeftCircular") {
                    list.shiftLeftCircular() `should equal` list
                }
            }
            on("shiftLeftCircular of list with 1, 2, 3") {
                val list = listOf(1, 2, 3)

                it("should be 2, 3, 1") {
                    list.shiftLeftCircular() `should equal` listOf(2, 3, 1)
                }
            }
        }
        describe("shiftLeftCircular n elements in a list") {
            on("shiftLeftCircular 3 of list with 1, 2, 3, 4") {
                val list = listOf(1, 2, 3, 4)

                it("should be 4, 1, 2, 3") {
                    list.shiftLeftCircular(3) `should equal` listOf(4, 1, 2, 3)
                }
            }
            on("shiftLeftCircular 4 of list with 1, 2, 3, 4") {
                val list = listOf(1, 2, 3, 4)

                it("should stay the same") {
                    list.shiftLeftCircular(4) `should equal` list
                }
            }
            on("shiftLeftCircular 5 of list with 1, 2, 3, 4") {
                val list = listOf(1, 2, 3, 4)

                it("should be the same as shift 1") {
                    list.shiftLeftCircular(5) `should equal` list.shiftLeftCircular(1)
                }
            }
        }

        describe("split string into digits") {
            on("empty string") {
                val string = ""
                it("should return empty list") {
                    string.splitToDigits() `should equal` listOf()
                }
            }
            on("string with one digit") {
                val string = "1"
                it("should return list with this digit") {
                    string.splitToDigits() `should equal` listOf(1)
                }
            }
            on("string with several digits") {
                val string = "123"
                it("should return list with these digits") {
                    string.splitToDigits() `should equal` listOf(1, 2, 3)
                }
            }
        }
    }
})

