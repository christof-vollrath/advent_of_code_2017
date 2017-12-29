
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 10: Knot Hash ---

You come across some programs that are trying to implement a software emulation of a hashStep based on knot-tying.
The hashStep these programs are implementing isn't very strong,
but you decide to help them anyway.
You make a mental note to remind the Elves later not to invent their own cryptographic functions.

This hashStep function simulates tying a knot in a circle of string with 256 marks on it.
Based on the input to be hashed, the function repeatedly selects a span of string,
brings the ends together, and gives the span a half-twist to reverse the order of the marks within it.
After doing this many times, the order of the marks is used to build the resulting hashStep.

  4--5   pinch   4  5           4   1
 /    \  5,0,1  / \/ \  twist  / \ / \
3      0  -->  3      0  -->  3   X   0
 \    /         \ /\ /         \ / \ /
  2--1           2  1           2   5

To achieve this, begin with a list of numbers from 0 to 255,
a current position which begins at 0 (the first element in the list),
a skip size (which starts at 0), and a sequence of lengths (your puzzle input).

Then, for each length:

Reverse the order of that length of elements in the list, starting with the element at the current position.
Move the current position forward by that length plus the skip size.
Increase the skip size by one.
The list is circular; if the current position and the length try to reverse elements beyond the end of the list,
the operation reverses using as many extra elements as it needs from the front of the list.
If the current position moves past the end of the list,
it wraps around to the front. Lengths larger than the size of the list are invalid.

Here's an example using a smaller list:

Suppose we instead only had a circular list containing five elements,
0, 1, 2, 3, 4, and were given input lengths of 3, 4, 1, 5.

The list begins as [0] 1 2 3 4 (where square brackets indicate the current position).
The first length, 3, selects ([0] 1 2) 3 4 (where parentheses indicate the sublist to be reversed).
After reversing that section (0 1 2 into 2 1 0), we get ([2] 1 0) 3 4.
Then, the current position moves forward by the length, 3, plus the skip size, 0: 2 1 0 [3] 4.
Finally, the skip size increases to 1.
The second length, 4, selects a section which wraps: 2 1) 0 ([3] 4.
The sublist 3 4 2 1 is reversed to form 1 2 4 3: 4 3) 0 ([1] 2.
The current position moves forward by the length plus the skip size, a total of 5,
causing it not to move because it wraps around: 4 3 0 [1] 2. The skip size increases to 2.
The third length, 1, selects a sublist of a single element, and so reversing it has no effect.
The current position moves forward by the length (1) plus the skip size (2): 4 [3] 0 1 2. The skip size increases to 3.
The fourth length, 5, selects every element starting with the second: 4) ([3] 0 1 2.
Reversing this sublist (3 0 1 2 4 into 4 2 1 0 3) produces: 3) ([4] 2 1 0.
Finally, the current position moves forward by 8: 3 4 2 1 [0]. The skip size increases to 4.
In this example, the first two numbers in the list end up being 3 and 4; to check the process,
you can multiply them together to produce 12.

However, you should instead use the standard list size of 256 (with values 0 to 255)
and the sequence of lengths in your puzzle input.
Once this process is complete, what is the result of multiplying the first two numbers in the list?

Your puzzle answer was 4480.


--- Part Two ---

The logic you've constructed forms a single round of the Knot Hash algorithm;
running the full thing requires many of these rounds. Some input and output processing is also required.

First, from now on, your input should be taken not as a list of numbers,
but as a string of bytes instead.
Unless otherwise specified, convert characters to bytes using their ASCII codes.
This will allow you to handle arbitrary ASCII strings,
and it also ensures that your input lengths are never larger than 255.
For example, if you are given 1,2,3, you should convert it to the ASCII codes for each character: 49,44,50,44,51.

Once you have determined the sequence of lengths to use,
add the following lengths to the end of the sequence: 17, 31, 73, 47, 23.
For example, if you are given 1,2,3, your final sequence of lengths should be 49,44,50,44,51,17,31,73,47,23
(the ASCII codes from the input string combined with the standard length suffix values).

Second, instead of merely running one round like you did above,
run a total of 64 rounds, using the same length sequence in each round.
The current position and skip size should be preserved between rounds.
For example, if the previous example was your first round,
you would start your second round with the same length sequence (3, 4, 1, 5, 17, 31, 73, 47, 23,
now assuming they came from ASCII codes and include the suffix),
but start with the previous round's current position (4) and skip size (4).

Once the rounds are complete, you will be left with the numbers from 0 to 255 in some order,
called the sparse hashStep.
Your next task is to reduce these to a list of only 16 numbers called the dense hashStep.
To do this, use numeric bitwise XOR to combine each consecutive block of 16 numbers in the sparse hashStep
(there are 16 such blocks in a list of 256 numbers).
So, the first element in the dense hashStep is the first sixteen elements of the sparse hashStep XOR'd together,
the second element in the dense hashStep is the second sixteen elements of the sparse hashStep XOR'd together, etc.

For example, if the first sixteen elements of your sparse hashStep are as shown below,
and the XOR operator is ^, you would calculate the first output number like this:

65 ^ 27 ^ 9 ^ 1 ^ 4 ^ 3 ^ 40 ^ 50 ^ 91 ^ 7 ^ 6 ^ 0 ^ 2 ^ 5 ^ 68 ^ 22 = 64

Perform this operation on each of the sixteen blocks of sixteen numbers
in your sparse hashStep to determine the sixteen numbers in your dense hashStep.

Finally, the standard way to represent a Knot Hash is as a single hexadecimal string;
the final output is the dense hashStep in hexadecimal notation.
Because each number in your dense hashStep will be between 0 and 255 (inclusive),
always represent each number as two hexadecimal digits (including a leading zero as necessary).
So, if your first three numbers are 64, 7, 255,
they correspond to the hexadecimal numbers 40, 07, ff, and so the first six characters of the hashStep would be 4007ff.
Because every Knot Hash is sixteen such numbers,
the hexadecimal representation is always 32 hexadecimal digits (0-f) long.

Here are some example hashes:

The empty string becomes a2582a3a0e66e6e86e3812dcb672a272.
AoC 2017 becomes 33efeb34ea91902bb2f59c9920caa6cd.
1,2,3 becomes 3efbe78a8d82f29979031a4aa0b16a9d.
1,2,4 becomes 63960835bcdc130f0b66d7ff4f6a5a8e.

Treating your puzzle input as a string of ASCII characters, what is the Knot Hash of your puzzle input?
Ignore any leading or trailing whitespace you might encounter.

Your puzzle answer was c500ffe015c83b60fad2e4b7d59dabc4.

 */

fun indexCircula(index: Int, length: Int) = index % length

fun <T> List<T>.getCircula(i: Int) = this[indexCircula(i, this.size)]
fun <T> MutableList<T>.setCircula(i: Int, value: T) = this.set(indexCircula(i, this.size), value)
fun <T> List<T>.subListCircula(from: Int, to: Int) = (from until to).map { this.getCircula(it) }
fun <T> MutableList<T>.setCircula(start: Int, values: List<T>) = this.apply {
    values.forEachIndexed { i, v -> this.setCircula(i + start, v) }
}

fun incrCircula(pos: Int, incr: Int, length: Int) = (pos + incr) % length

fun hashStep(list: List<Int>, lengths: List<Int>, repeat: Int = 1): List<Int> {
    var pos = 0
    var skip = 0
    val result = list.toMutableList()
    (1..repeat).forEach {
        lengths.forEach { length ->
            val toBeReversed = result.subListCircula(pos, pos + length)
            result.setCircula(pos, toBeReversed.reversed())
            pos = incrCircula(pos, skip + length,  list.size)
            skip++
        }
    }
    return result
}
fun hashStep(range: IntRange, lengths: List<Int>, repeat: Int = 1) = hashStep(range.toList(), lengths, repeat)

fun asciiToList(string: String) = string.toList().map { it.toInt() }

fun reduceBlock(block: List<Int>) = block.reduce { x, y -> x xor y }

fun denseHash(hash: List<Int>) =
    (0..(hash.size / 16 -1))
            .map { reduceBlock(hash.subList(it * 16, it * 16 + 16)) }

fun List<Int>.toHex() =
        this.joinToString("") {
            it.toString(16).padStart(2, '0')
        }

fun hash(input: String, length: Int = 256): List<Int> {
    val inputList = asciiToList(input)
    val list = inputList + listOf(17, 31, 73, 47, 23) // add seed
    val hash = hashStep(0..(length-1), list, 64)
    return denseHash(hash)
}

class Day10Spec : Spek({
    describe("incrCircula") {
        on("list of length 4, current pos 0, incr 2") {
            it("2") {
                incrCircula(0, 2, 4) `should equal` 2
            }
        }
        on("list of length 4, current pos 2, incr 2") {
            it("2") {
                incrCircula(2, 2, 4) `should equal` 0
            }
        }
        on("list of length 4, current pos 3, incr 2") {
            it("2") {
                incrCircula(3, 2, 4) `should equal` 1
            }
        }
    }
    describe("getCircula") {
        on("list of length 4, get 0") {
            it("0") {
                listOf(0, 1, 2, 3).getCircula(0) `should equal` 0
            }
        }
        on("list of length 4, get 5") {
            it("0") {
                listOf(0, 1, 2, 3).getCircula(5) `should equal` 1
            }
        }
    }
    describe("setCircula") {
        on("list of length 4, set 1 to 2") {
            it("0") {
                val list = mutableListOf(0, 1, 2, 3)
                list.setCircula(1, 2)
                list `should equal` listOf(0, 2, 2, 3)
            }
        }
        on("list of length 4, set 10 to 9") {
            it("0") {
                val list = mutableListOf(0, 1, 2, 3)
                list.setCircula(10, 9)
                list `should equal` listOf(0, 1, 9, 3)
            }
        }
    }
    describe("subListCircula") {
        on("list of length 4, get 1 to 2") {
            val list = mutableListOf(0, 1, 2, 3)
            it("1, 2") {
                list.subListCircula(1, 2) `should equal` listOf(1)
                list.subListCircula(1, 2) `should equal` list.subList(1, 2)
            }
        }
        on("list of length 4, get 10 to 13") {
            val list = mutableListOf(0, 1, 2, 3)
            it("2, 3, 0, 1") {
                list.subListCircula(10, 13) `should equal` listOf(2, 3, 0)
            }
        }
    }
    describe("setCircula List") {
        on("list of length 4, set from 1 to (12, 13)") {
            it("0, 12, 13, 3") {
                val list = mutableListOf(0, 1, 2, 3)
                list.setCircula(1, listOf(12, 13))
                list `should equal` listOf(0, 12, 13, 3)
            }
        }
        on("list of length 4, set from 10 to (19, 20, 21)") {
            it("0") {
                val list = mutableListOf(0, 1, 2, 3)
                list.setCircula(10, listOf(19, 20, 21))
                list `should equal` listOf(21, 1, 19, 20)
            }
        }
    }
    describe("hashStep") {
        on("list 0, 1, 2, 3, 4 and length 3") {
            it("2, 1, 0, 3, 4") {
                hashStep(listOf(0, 1, 2, 3, 4), listOf(3)) `should equal` listOf(2, 1, 0, 3, 4)
            }
        }
        on("list 0, 1, 2, 3, 4 and length 3, 4") {
            it("4, 3, 0, 1, 2") {
                hashStep(listOf(0, 1, 2, 3, 4), listOf(3, 4)) `should equal` listOf(4, 3, 0, 1, 2)
            }
        }
        on("range 0..4 and length 3, 4") {
            it("4, 3, 0, 1, 2") {
                hashStep(0..4, listOf(3, 4)) `should equal` listOf(4, 3, 0, 1, 2)
            }
        }
    }

    describe("hashStep from input") {
        on("input lengths") {
            val result = hashStep(0..255, day10Input.split(",").map { it.toInt() })
            println(result)
            println(result[0] * result[1])
        }
    }

    describe("ascii string to list"){
        on("empty string") {
            it("should become empty list") {
                asciiToList("") `should equal` listOf()
            }
        }
        //
        on("""string "1,2,3" """) {
            it("should become 49,44,50,44,51") {
                asciiToList("1,2,3") `should equal` listOf(49,44,50,44,51)
            }
        }
    }
    describe("reduce block") {
        on("block 65, 27, 9, 1, 4, 3, 40, 50, 91, 7, 6, 0, 2, 5, 68, 22") {
            val block = listOf(65, 27, 9, 1, 4, 3, 40, 50, 91, 7, 6, 0, 2, 5, 68, 22)
            it("should be reduced to 64") {
                reduceBlock(block) `should equal` 64
            }
        }
    }
    describe("dense hash") {
        on("16 blocks") {
            val block = listOf(65, 27, 9, 1, 4, 3, 40, 50, 91, 7, 6, 0, 2, 5, 68, 22)
            val hash = block.toMutableList()
            for (i in 1..15) hash += block
            hash.size `should equal` 256
            it("should get dense hash") {
                denseHash(hash) `should equal` listOf(64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64)
            }
        }
    }
    describe("to hex") {
        on("64, 7, 255") {
            val list = listOf(64, 7, 255)
            it("should become 4007ff") {
                list.toHex() `should equal` "4007ff"
            }
        }
    }

    describe("hash") {
        val testData = arrayOf(
                //         string               result
                //--|-------------|----------------------------------------------
                data("",         "a2582a3a0e66e6e86e3812dcb672a272"),
                data("AoC 2017", "33efeb34ea91902bb2f59c9920caa6cd"),
                data("1,2,3",    "3efbe78a8d82f29979031a4aa0b16a9d"),
                data("1,2,4",    "63960835bcdc130f0b66d7ff4f6a5a8e")
        )
        onData("input %s", with = *testData) { input, expected ->
            it("returns $expected") {
                hash(input).toHex() `should equal` expected
            }
        }
    }

    describe("dense hash from input") {
        on("input") {
            println(hash(day10Input).toHex())
        }
    }

})


val day10Input = "63,144,180,149,1,255,167,84,125,65,188,0,2,254,229,24"