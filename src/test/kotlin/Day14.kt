import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should start with`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.api.dsl.xdescribe

/*
--- Day 14: Disk Defragmentation ---

Suddenly, a scheduled job activates the system's disk defragmenter.
Were the situation different, you might sit and watch it for a while, but today,
you just don't have that kind of time.
It's soaking up valuable system resources that are needed elsewhere,
and so the only option is to help it finish its task as soon as possible.

The disk in question consists of a 128x128 grid; each square of the grid is either free or used.
On this disk, the state of the grid is tracked by the bits in a sequence of knot hashes.

A total of 128 knot hashes are calculated, each corresponding to a single row in the grid;
each hash contains 128 bits which correspond to individual grid squares.
Each bit of a hash indicates whether that square is free (0) or used (1).

The hash inputs are a key string (your puzzle input), a dash, and a number from 0 to 127 corresponding to the row.
For example, if your key string were flqrgnkx,
then the first row would be given by the bits of the knot hash of flqrgnkx-0,
the second row from the bits of the knot hash of flqrgnkx-1,
and so on until the last row, flqrgnkx-127.

The output of a knot hash is traditionally represented by 32 hexadecimal digits;
each of these digits correspond to 4 bits, for a total of 4 * 32 = 128 bits.
To convert to bits, turn each hexadecimal digit to its equivalent binary value, high-bit first:
0 becomes 0000, 1 becomes 0001, e becomes 1110, f becomes 1111, and so on; a hash that begins with a0c2017...
in hexadecimal would begin with 10100000110000100000000101110000... in binary.

Continuing this process, the first 8 rows and columns for key flqrgnkx appear as follows,
using # to denote used squares, and . to denote free ones:

##.#.#..-->
.#.#.#.#
....#.#.
#.#.##.#
.##.#...
##..#..#
.#...#..
##.#.##.-->
|      |
V      V

In this example, 8108 squares are used across the entire 128x128 grid.

Given your actual key string, how many squares are used?

Your puzzle input is vbqugkhl.

Your puzzle answer was 8148.

--- Part Two ---

Now, all the defragmenter needs to know is the number of regions.
A region is a group of used squares that are all adjacent, not including diagonals.
Every used square is in exactly one region:
lone used squares form their own isolated regions,
while several adjacent squares all count as a single region.

In the example above, the following nine regions are visible, each marked with a distinct digit:

11.2.3..-->
.1.2.3.4
....5.6.
7.8.55.9
.88.5...
88..5..8
.8...8..
88.8.88.-->
|      |
V      V

Of particular interest is the region marked 8; while it does not appear contiguous in this small view,
all of the squares marked 8 are connected when considering the whole 128x128 grid.
In total, in this example, 1242 regions are present.

How many regions are present given your key string?

Your puzzle answer was 1180.

 */

fun countRegions(binHashes: List<String>) = countRegions(convert2Matrix(binHashes))
fun countRegions(regions: Array<IntArray>) = findRegions(regions).first

fun findRegions(regions: Array<IntArray>): Pair<Int, Array<IntArray>> {
    var i = 1
    regions.forEachIndexed { y, row ->
        row.forEachIndexed { x, _ ->
            if (findRegion(i, x, y, regions)) i++
        }
    }
    return Pair(i-1, regions)
}

fun findRegion(i: Int, x: Int, y: Int, regions: Array<IntArray>): Boolean =
        if (x < 0 || x >= regions.size || y < 0 || y >= regions[x].size) false
        else if (regions[y][x] == -1) {
            regions[y][x] = i // New regions found
            findRegion(i, x-1, y, regions) // Find adjacent neighbours
            findRegion(i, x+1, y, regions)
            findRegion(i, x, y-1, regions)
            findRegion(i, x, y+1, regions)
            true
        } else false

fun convert2Matrix(binHashes: List<String>): Array<IntArray> =
        binHashes.map {
            it.toCharArray().toList().map { if (it == '1') -1 else 0 }.toIntArray()
        }.toTypedArray()

fun createRowKeys(key: String) = (0..127).map { "$key-$it"}
fun createHashs(rowKeys: List<String>) = rowKeys.map { hash(it, 256)}
fun toBinList(hashes: List<List<Int>>) = hashes.map { it.toBin() }


fun List<Int>.toBin() =
        this.map {
            it.toString(2).padStart(8, '0')
        }
                .joinToString("")

class Day14Spec : Spek({
    describe("list<Int> to bin") {
        on("example binary code") {
            val input = listOf(0xa0, 0xc2, 0x01, 0x70)

            it("should be the right binary") {
                input.toBin() `should equal` "10100000110000100000000101110000"
            }
        }
    }
    describe("example disk") {
        on("flqrgnkx") {
            val key = "flqrgnkx"
            val rowKeys = createRowKeys(key)
            val disk = createHashs(rowKeys)
            val binHashes = toBinList(disk)
            it("should have the correct starting values") {
                binHashes[0] `should start with` "11010100"
                binHashes[1] `should start with` "01010101"
                binHashes[2] `should start with` "00001010"
                binHashes[3] `should start with` "10101101"
                binHashes[4] `should start with` "01101000"
                binHashes[5] `should start with` "11001001"
                binHashes[6] `should start with` "01000100"
                binHashes[7] `should start with` "11010110"
            }
        }
    }
    describe("createRowKeys") {
        on("flqrgnkx") {
            val key = "flqrgnkx"
            val rowKeys = createRowKeys(key)
            it("should start with flqrgnkx-0 and end with flqrgnkx-127 and have size 128") {
                rowKeys.size `should equal` 128
                rowKeys[0] `should equal` "flqrgnkx-0"
                rowKeys[127] `should equal` "flqrgnkx-127"
            }
        }
    }
    describe("createHashs") {
        on("list with flqrgnkx-0") {
            val disk = createHashs(listOf("flqrgnkx-0"))
            val binHashes = toBinList(disk)
            it("should start with ##.#.#..") {
                binHashes[0] `should start with` "11010100"
            }
        }
    }
    describe("exercise") {
        on("input") {
            val key = "vbqugkhl"
            val rowKeys = createRowKeys(key)
            val disk = createHashs(rowKeys)
            val binHashes = toBinList(disk)
            val numberOfOnes = binHashes.flatMap { it.toCharArray().toList()}.filter { it == '1'}.size
            println("Used: $numberOfOnes")
        }
    }

    describe("find regions") {
        on("matrix with marked used regions") {
            val matrix = arrayOf(
                    intArrayOf(-1,-1, 0,-1, 0,-1, 0, 0),
                    intArrayOf( 0,-1, 0,-1, 0,-1, 0,-1),
                    intArrayOf( 0, 0, 0, 0,-1, 0,-1, 0),
                    intArrayOf(-1, 0,-1, 0,-1,-1, 0,-1),
                    intArrayOf( 0,-1,-1, 0,-1, 0, 0, 0),
                    intArrayOf(-1,-1, 0, 0,-1, 0, 0, 0),
                    intArrayOf(-1, 0, 0, 0, 0, 0, 0, 0),
                    intArrayOf(-1,-1, 0, 0, 0, 0, 0, 0)
            )

            val regions = findRegions(matrix)
            it("should have 8 regions") {
                //println(regions.second.map {it.joinToString(", ")}.joinToString("\n"))
                regions.first `should equal` 9
                regions.second `should equal` arrayOf(
                        intArrayOf( 1, 1, 0, 2, 0, 3, 0, 0),
                        intArrayOf( 0, 1, 0, 2, 0, 3, 0, 4),
                        intArrayOf( 0, 0, 0, 0, 5, 0, 6, 0),
                        intArrayOf( 7, 0, 8, 0, 5, 5, 0, 9),
                        intArrayOf( 0, 8, 8, 0, 5, 0, 0, 0),
                        intArrayOf( 8, 8, 0, 0, 5, 0, 0, 0),
                        intArrayOf( 8, 0, 0, 0, 0, 0, 0, 0),
                        intArrayOf( 8, 8, 0, 0, 0, 0, 0, 0)
                )

            }
        }
    }
    describe("convert to matrix") {
        on("some row keys") {
            val rowKeys = listOf(
                "11010100",
                "01010101",
                "00001010",
                "10101101",
                "01101000",
                "11001000",
                "10000000",
                "11000000"
            )
            val matrix = convert2Matrix(rowKeys)
            it("should become a matrix") {
                matrix `should equal`     arrayOf(
                        intArrayOf(-1,-1, 0,-1, 0,-1, 0, 0),
                        intArrayOf( 0,-1, 0,-1, 0,-1, 0,-1),
                        intArrayOf( 0, 0, 0, 0,-1, 0,-1, 0),
                        intArrayOf(-1, 0,-1, 0,-1,-1, 0,-1),
                        intArrayOf( 0,-1,-1, 0,-1, 0, 0, 0),
                        intArrayOf(-1,-1, 0, 0,-1, 0, 0, 0),
                        intArrayOf(-1, 0, 0, 0, 0, 0, 0, 0),
                        intArrayOf(-1,-1, 0, 0, 0, 0, 0, 0)
                )
            }
        }
    }
    describe("regions in example disk") {
        on("flqrgnkx") {
            val key = "flqrgnkx"
            val binHashes = toBinList(createHashs(createRowKeys(key)))
            val regions = countRegions(binHashes)
            it("should have 1242 regions") {
                regions `should equal` 1242
            }
        }
    }
    describe("regions in exercise") {
        on("input") {
            val key = "vbqugkhl"
            val binHashes = toBinList(createHashs(createRowKeys(key)))
            val regions = countRegions(binHashes)
            println("Regions: $regions")
        }
    }


})
