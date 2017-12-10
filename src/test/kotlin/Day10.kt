import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/*
--- Day 10: Knot Hash ---

You come across some programs that are trying to implement a software emulation of a hash based on knot-tying.
The hash these programs are implementing isn't very strong,
but you decide to help them anyway.
You make a mental note to remind the Elves later not to invent their own cryptographic functions.

This hash function simulates tying a knot in a circle of string with 256 marks on it.
Based on the input to be hashed, the function repeatedly selects a span of string,
brings the ends together, and gives the span a half-twist to reverse the order of the marks within it.
After doing this many times, the order of the marks is used to build the resulting hash.

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

 */

fun indexCircula(index: Int, length: Int) = index % length

fun <T> List<T>.getCircula(i: Int) = this.get(indexCircula(i, this.size))
fun <T> MutableList<T>.setCircula(i: Int, value: T) = this.set(indexCircula(i, this.size), value)
fun <T> List<T>.subListCircula(from: Int, to: Int) = (from..to-1).map { this.getCircula(it) }
fun <T> MutableList<T>.setCircula(start: Int, values: List<T>) = this.apply {
    values.forEachIndexed { i, v -> this.setCircula(i + start, v) }
}

fun incrCircula(pos: Int, incr: Int, length: Int) = (pos + incr) % length

fun hash(list: List<Int>, lengths: List<Int>): List<Int> {
    var pos = 0
    val result = list.toMutableList()
    lengths.forEachIndexed { skip, length ->
        val toBeReversed = result.subListCircula(pos, pos + length)
        result.setCircula(pos, toBeReversed.reversed())
        pos = incrCircula(pos, skip + length,  list.size)
    }
    return result
}
fun hash(range: IntRange, lengths: List<Int>) = hash(range.toList(), lengths)

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
    describe("hash") {
        on("list 0, 1, 2, 3, 4 and length 3") {
            it("2, 1, 0, 3, 4") {
                hash(listOf(0, 1, 2, 3, 4), listOf(3)) `should equal` listOf(2, 1, 0, 3, 4)
            }
        }
        on("list 0, 1, 2, 3, 4 and length 3, 4") {
            it("4, 3, 0, 1, 2") {
                hash(listOf(0, 1, 2, 3, 4), listOf(3, 4)) `should equal` listOf(4, 3, 0, 1, 2)
            }
        }
        on("range 0..4 and length 3, 4") {
            it("4, 3, 0, 1, 2") {
                hash(0..4, listOf(3, 4)) `should equal` listOf(4, 3, 0, 1, 2)
            }
        }
    }
    describe("hash from input lengths") {
        on("input lengths") {
            val result = hash(0..255, day10Lengths)
            println(result)
            println(result[0] * result[1])
        }

    }
})

val day10Lengths = listOf(63,144,180,149,1,255,167,84,125,65,188,0,2,254,229,24)