
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import kotlin.coroutines.experimental.buildSequence
import kotlin.test.assertFailsWith
import org.jetbrains.spek.data_driven.on as onData

/*
Suddenly, whirling in the distance, you notice what looks like a massive, pixelated hurricane:
a deadly spinlock.
This spinlock isn't just consuming computing power, but memory, too;
vast, digital mountains are being ripped from the ground and consumed by the vortex.

If you don't move quickly, fixing that printer will be the least of your problems.

This spinlock's algorithm is simple but efficient, quickly consuming everything in its path.
It starts with a circular buffer containing only the value 0, which it marks as the current position.
It then steps forward through the circular buffer some number of steps (your puzzle input)
before inserting the first new value, 1, after the value it stopped on.
The inserted value becomes the current position.
Then, it steps forward from there the same number of steps, and wherever it stops,
inserts after it the second new value, 2, and uses that as the new current position again.

It repeats this process of stepping forward,
inserting a new value, and using the location of the inserted value as the new current position a total of 2017 times,
inserting 2017 as its final operation, and ending with a total of 2018 values (including 0) in the circular buffer.

For example, if the spinlock were to step 3 times per insert,
the circular buffer would begin to evolve like this
(using parentheses to mark the current position after each iteration of the algorithm):

(0), the initial state before any insertions.
0 (1): the spinlock steps forward three times (0, 0, 0), and then inserts the first value, 1, after it.
1 becomes the current position.
0 (2) 1: the spinlock steps forward three times (0, 1, 0), and then inserts the second value, 2, after it.
2 becomes the current position.
0  2 (3) 1: the spinlock steps forward three times (1, 0, 2), and then inserts the third value, 3, after it.
3 becomes the current position.

And so on:

0  2 (4) 3  1
0 (5) 2  4  3  1
0  5  2  4  3 (6) 1
0  5 (7) 2  4  3  6  1
0  5  7  2  4  3 (8) 6  1
0 (9) 5  7  2  4  3  8  6  1

Eventually, after 2017 insertions, the section of the circular buffer near the last insertion looks like this:

1512  1134  151 (2017) 638  1513  851

Perhaps, if you can identify the value that will ultimately be after the last value written (2017),
you can short-circuit the spinlock.
In this example, that would be 638.

What is the value after 2017 in your completed circular buffer?

Your puzzle input is 394.

Your puzzle answer was 926.

--- Part Two ---

The spinlock does not short-circuit. Instead, it gets more angry.
At least, you assume that's what happened; it's spinning significantly faster than it was a moment ago.

You have good news and bad news.

The good news is that you have improved calculations for how to stop the spinlock.
They indicate that you actually need to identify the value after 0 in the current state of the circular buffer.

The bad news is that while you were determining this,
the spinlock has just finished inserting its fifty millionth value (50000000).

What is the value after 0 the moment 50000000 is inserted?

Your puzzle input is still 394.

Your puzzle answer was 10150888.


 */

class Day17Spec : Spek({
    describe("circular buffer") {
        on("Circular buffer with one element") {
            val buffer = CircularBuffer()
            it("should have circular access to that element") {
                buffer.get() `should equal` 0
                buffer.incrPos(3).get() `should equal` 0
            }
            it("should allow insert at pos") {
                buffer.insert(7).list `should equal` listOf(0, 7)
                buffer.incrPos(9).insert(7).list `should equal` listOf(0, 7)
            }
            it ("should find that element") {
                buffer.find(0) `should equal` 0
            }
            it ("should throw exeception when searching for another element") {
                assertFailsWith<IllegalStateException> {
                    buffer.find(1)
                }
            }
        }
        on("Circular buffer with some elements") {
            val buffer = CircularBuffer().insert(1).insert(2).incrPos(1)
            it("should have circular access to these elements") {
                buffer.get() `should equal` 0
                buffer.incrPos(1).get() `should equal` 1
                buffer.incrPos(3).get() `should equal` 0
                buffer.incrPos(8).get() `should equal` 2
                buffer.incrPos(3).incrPos(5).get() `should equal` 2
            }
            it("should allow insert after these elements") {
                buffer.insert(7).list `should equal` listOf(0, 7, 1, 2)
                buffer.incrPos(1).insert(7).list `should equal` listOf(0, 1, 7, 2)
                buffer.incrPos(2).insert(7).list `should equal` listOf(0, 1, 2, 7)
                buffer.incrPos(3).insert(7).list `should equal` listOf(0, 7, 1, 2)
            }
            it ("should find elements") {
                buffer.find(0) `should equal` 0
                buffer.find(2) `should equal` 2
            }
            it ("should throw exeception when searching for other element") {
                assertFailsWith<IllegalStateException> {
                    buffer.find(9)
                }
            }
        }
    }
    describe("example spinlock first 9 steps") {
        val testData = arrayOf(
                //    times      result
                //--|----|--------------------------------------
                data(0, listOf(0)),
                data(1, listOf(0, 1)),
                data(2, listOf(0, 2, 1)),
                data(3, listOf(0, 2, 3, 1)),
                data(4, listOf(0, 2, 4, 3, 1)),
                data(5, listOf(0, 5, 2, 4, 3, 1)),
                data(6, listOf(0, 5, 2, 4, 3, 6, 1)),
                data(7, listOf(0, 5, 7, 2, 4, 3, 6, 1)),
                data(8, listOf(0, 5, 7, 2, 4, 3, 8, 6, 1)),
                data(9, listOf(0, 9, 5, 7, 2, 4, 3, 8, 6, 1))
        )
        onData("input %s", with = *testData) { times, expected ->
            it("returns $expected") {
                spinlock(3, times).list `should equal` expected
            }
        }
    }
    describe("spinlock") {
        on("example data") {
            it ("should have 638 after 2017") {
                val result = spinlock(3, 2017)
                val i = result.find(2017)
                result.get(i+1) `should equal` 638
            }
        }
        on("exercise") {
                val result = spinlock(394, 2017)
                val i = result.find(2017)
                println("After value 2017: ${result.get(i+1)}")
        }
    }
    describe("circular buffer 2") {
        on("Circular buffer 2 with one element") {
            val buffer = CircularBuffer2()
            it("should have circular access to that element 2") {
                buffer.get() `should equal` 0
                buffer.incrPos(3).get() `should equal` 0
            }
            it("should allow insert") {
                buffer.insert(7)
                buffer.get(0) `should equal` 0
                buffer.get(1) `should equal` 7
            }
        }
        on("Circular buffer 2 with some elements") {
            val buffer = CircularBuffer2().insert(1).insert(2).incrPos(1)
            it("should have circular access to these elements") {
                buffer.get() `should equal` 0
                buffer.get(0) `should equal` 0
                buffer.get(1) `should equal` 1
            }
            it("should allow insert after these elements") {
                buffer.insert(7)
                buffer.get(0) `should equal` 0
                buffer.get(1) `should equal` 7
                buffer.incrPos(3).insert(8)
                buffer.get(0) `should equal` 0
                buffer.get(1) `should equal` 8
            }
        }
    }
    describe("example spinlock 2 first 9 steps") {
        val testData = arrayOf(
                //    times      second element in list
                //--|----|--------------------------------------
                data(1, 1),
                data(2, 2),
                data(3, 2),
                data(4, 2),
                data(5, 5),
                data(6, 5),
                data(7, 5),
                data(8, 5),
                data(9, 9)
        )
        onData("input %s", with = *testData) { times, expected ->
            it("returns $expected") {
                spinlock2(3, times).get(1) `should equal` expected
            }
        }
    }
    describe("spinlock2") {
        on("exercise part 2") {
            val result = spinlock2(394, 50_000_000)
            println("After value 0: ${result.get(1)}")
        }
    }
})

fun spinlock(steps: Int, times: Int): CircularBuffer {
    var buffer = CircularBuffer()
    (1..times).map {
        buffer = buffer.incrPos(steps).insert(it)
    }
    return buffer
}

class CircularBuffer(val list: List<Int>, val pos: Int) {
    constructor() : this(listOf(0), 0)

    fun get() = list[pos]
    fun get(i: Int) = list[i % list.size]
    fun insert(value: Int) = CircularBuffer(
            buildSequence {
                list.forEachIndexed { index, curr ->
                    yield(curr)
                    if (index == pos) {
                        yield(value)
                    }
                }
            }.toList(), pos+1)

    fun find(searchFor: Int): Int {
        list.forEachIndexed { index, value ->
            if (value == searchFor) return index
        }
        throw (IllegalStateException("$searchFor not found"))
    }
    fun incrPos(i: Int): CircularBuffer = CircularBuffer(list, (pos + i) % list.size)
}

class CircularBuffer2(var second: Int?, var size: Int, var pos: Int) {
    // Keeps track only of the first two elements and uses a mutable data structure for efficiency
    constructor() : this(null, 1, 0)

    fun get() = get(pos)
    fun get(i: Int) = when {
        i == 0 -> 0 // first element is always 0
        i == 1 && size > 1 -> second
        else -> IllegalArgumentException()
    }
    fun insert(value: Int): CircularBuffer2 {
        size++
        if (pos == 0) second = value
        pos += 1
        return this
    }
    fun incrPos(i: Int): CircularBuffer2 {
        pos = (pos + i) % size
        return this
    }
}
fun spinlock2(steps: Int, times: Int): CircularBuffer2 {
    val buffer = CircularBuffer2()
    (1..times).map {
        buffer.incrPos(steps).insert(it)
    }
    return buffer
}



