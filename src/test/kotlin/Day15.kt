import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData
import kotlin.coroutines.experimental.buildSequence

/*
--- Day 15: Dueling Generators ---

Here, you encounter a pair of dueling generators.
The generators, called generator A and generator B, are trying to agree on a sequence of numbers.
However, one of them is malfunctioning, and so the sequences don't always match.

As they do this, a judge waits for each of them to generate its next value,
compares the lowest 16 bits of both values, and keeps track of the number of times those parts of the values match.

The generators both work on the same principle.
To create its next value, a generator will take the previous value it produced,
multiply it by a factor (generator A uses 16807; generator B uses 48271),
and then keep the remainder of dividing that resulting product by 2147483647.
That final remainder is the value it produces next.

To calculate each generator's first value,
it instead uses a specific starting value as its "previous value" (as listed in your puzzle input).

For example, suppose that for starting values, generator A uses 65, while generator B uses 8921.
Then, the first five pairs of generated values are:

--Gen. A--  --Gen. B--
   1092455   430625591
1181022009  1233683848
 245556042  1431495498
1744312007   137874439
1352636452   285222916

In binary, these pairs are (with generator A's value first in each pair):

00000000000100001010101101100111
00011001101010101101001100110111

01000110011001001111011100111001
01001001100010001000010110001000

00001110101000101110001101001010
01010101010100101110001101001010

01100111111110000001011011000111
00001000001101111100110000000111

01010000100111111001100000100100
00010001000000000010100000000100

Here, you can see that the lowest (here, rightmost) 16 bits of the third value match: 1110001101001010.
Because of this one match, after processing these five pairs, the judge would have added only 1 to its total.

To get a significant sample, the judge would like to consider 40 million pairs.
(In the example above, the judge would eventually find a total of 588 pairs that match in their lowest 16 bits.)

After 40 million pairs, what is the judge's final count?

Your puzzle answer was 573.


 */

class Day15Spec : Spek({
    describe("count matching pairs") {
        on("example generators") {
            val generatorA = Generator(65L, 16807L)
            val generatorB = Generator(8921L, 48271L)

            it("should be the right value") {
                countMatchingPairs(generatorA, generatorB) `should equal` 588
            }
        }
    }
    describe("two example generators") {
        val testData = arrayOf(
                //     seed      factor|    beginning of generated numbers
                //--|-------|----------|---------------------------------------------------------
                data(65L,   16807L, listOf(1092455L, 1181022009L, 245556042L, 1744312007L, 1352636452L)),
                data(8921L, 48271L, listOf(430625591L, 1233683848L, 1431495498L, 137874439L, 285222916L))

        )
        onData("input %s", with = *testData) { seed, factor, expected ->
            it("returns $expected") {
                val generator = Generator(seed, factor)
                buildSequence {
                    repeat(expected.size) { yield(generator.next()) }
                }.toList() `should equal`expected
            }
        }
    }
    describe("lowest 16 bits") {
        val testData = arrayOf(
                //     value          lowest 16 bit
                //--|---------------|-----------------------------------------------
                data(1092455L,     0b1010101101100111),
                data(430625591L,   0b1101001100110111),
                data(1181022009L,  0b1111011100111001),
                data(1233683848L, 0b1000010110001000),
                data(245556042L,   0b1110001101001010),
                data(1431495498L,  0b1110001101001010)

        )
        onData("input %s", with = *testData) { value, expected ->
            it("returns $expected") {
                lowest16Bits(value) `should equal`expected
            }
        }
    }
    describe("count matching pairs for exercise") {
        on("example generators") {
            val generatorA = Generator(634L, 16807L)
            val generatorB = Generator(301L, 48271L)

            println("Matching pairs: ${countMatchingPairs(generatorA, generatorB)}")
        }
    }

})

fun countMatchingPairs(generatorA: Generator, generatorB: Generator) =
        buildSequence {
            repeat(40_000_000) { yield(Pair(generatorA.next(), generatorB.next())) }
        }
        .filter { lowest16Bits(it.first) == lowest16Bits(it.second)}.count()

fun lowest16Bits(value: Long) = value.toChar().toInt()

class Generator(seed: Long, val factor:  Long) {
    var current: Long
    init {
        current = seed
    }
    fun next(): Long {
        current = current * factor % 2147483647
        return current
    }
}
