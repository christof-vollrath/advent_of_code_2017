import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/*
--- Day 23: Coprocessor Conflagration ---

You decide to head directly to the CPU and fix the printer from there.
 As you get close, you find an experimental coprocessor doing so much work
 that the local programs are afraid it will halt and catch fire.
 This would cause serious issues for the rest of the computer,
 so you head in and see what you can do.

The code it's running seems to be a variant of the kind you saw recently on that tablet.
The general functionality seems very similar, but some of the instructions are different:

set X Y sets register X to the value of Y.
sub X Y decreases register X by the value of Y.
mul X Y sets register X to the result of multiplying the value contained in register X by the value of Y.
jnz X Y jumps with an offset of the value of Y, but only if the value of X is not zero.
(An offset of 2 skips the next instruction, an offset of -1 jumps to the previous instruction, and so on.)

Only the instructions listed above are used. The eight registers here, named a through h, all start at 0.

The coprocessor is currently set to some kind of debug mode,
which allows for testing, but prevents it from doing any meaningful work.

If you run the program (your puzzle input), how many times is the mul instruction invoked?

Your puzzle answer was 9409.


 */

class Day23Spec : Spek({
    describe("coprocessor instructions") {
        on("set") {
            val coprocessor = Coprocessor()
            it("should set register a from value") {
                Coprocessor.Set('a', Coprocessor.Const(2)).execute(coprocessor)
                coprocessor.registers['a'] `should equal` 2L
            }
            it("should set register b from register a") {
                Coprocessor.Set('b', Coprocessor.Register('a')).execute(coprocessor)
                coprocessor.registers['b'] `should equal` 2L
            }
            it("should set register b from empty register c") {
                Coprocessor.Set('b', Coprocessor.Register('c')).execute(coprocessor)
                coprocessor.registers['b'] `should equal` 0L
            }
        }
        on("sub") {
            val coprocessor = Coprocessor()
            it("should sub from empty register") {
                Coprocessor.Sub('a', Coprocessor.Const(3)).execute(coprocessor)
                coprocessor.registers['a'] `should equal` -3L
            }
            it("should sub") {
                Coprocessor.Sub('a', Coprocessor.Const(2)).execute(coprocessor)
                coprocessor.registers['a'] `should equal` -5L
            }
            it("should sub register a from register b") {
                Coprocessor.Sub('b', Coprocessor.Register('a')).execute(coprocessor)
                coprocessor.registers['b'] `should equal` 5L
            }
            it("should sub empty register c from register b") {
                Coprocessor.Sub('b', Coprocessor.Register('c')).execute(coprocessor)
                coprocessor.registers['b'] `should equal` 5L
            }
        }
        on("mul") {
            val coprocessor = Coprocessor()
            Coprocessor.Set('a', Coprocessor.Const(2)).execute(coprocessor)
            it("should multiply register a and value") {
                Coprocessor.Mul('a', Coprocessor.Const(3)).execute(coprocessor)
                coprocessor.registers['a'] `should equal` 6L
            }
        }
        on("jnz") {
            val coprocessor = Coprocessor()
            it("should not jump when zero value") {
                Coprocessor.Jnz(Coprocessor.Const(0), Coprocessor.Const(3)).execute(coprocessor)
                coprocessor.pc `should equal` 1
            }
            it("should jump when positive value") {
                Coprocessor.Jnz(Coprocessor.Const(1), Coprocessor.Const(3)).execute(coprocessor)
                coprocessor.pc `should equal` 4
            }
            it("should not jump when negative value") {
                Coprocessor.Jnz(Coprocessor.Register('b'), Coprocessor.Const(3)).execute(coprocessor)
                coprocessor.pc `should equal` 5
            }
        }
    }
    describe("parse instructions") {
        on("exercise input") {
            val input = day23Input
            it("should parse to the correct instructions") {
                parseCoprocessorInstructions(input).subList(0, 6) `should equal` listOf(
                        Coprocessor.Set('b', Coprocessor.Const(99)),
                        Coprocessor.Set('c', Coprocessor.Register('b')),
                        Coprocessor.Jnz(Coprocessor.Register('a'), Coprocessor.Const(2)),
                        Coprocessor.Jnz(Coprocessor.Const(1), Coprocessor.Const(5)),
                        Coprocessor.Mul('b', Coprocessor.Const(100)),
                        Coprocessor.Sub('b', Coprocessor.Const(-100000))
                )
            }
        }
    }
    describe("execute exercise instructions") {
        on("exercise input") {
            val input = day23Input
            var mulCounter = 0
            val debugger: Debugger = { instr, _, _ ->
                if (instr is Coprocessor.Mul) mulCounter++
            }
            Coprocessor(parseCoprocessorInstructions(input), debug = debugger).execute()
            println("Number mul invocations: $mulCounter")
            mulCounter `should equal` 9409
        }
    }

})

typealias Debugger = (Coprocessor.Instr, Map<Char, Long>, Int) -> Unit

data class Coprocessor(val instructions: List<Instr> = listOf(),
                       val registers: MutableMap<Char, Long> = mutableMapOf(),
                       var pc: Int = 0,
                       val debug: Debugger? = null) {

    fun execute(): Coprocessor {
        while (pc < instructions.size) {
            val instruction = instructions[pc]
            println("pc: ${pc} instr: $instruction registers: ${registers}")
            instruction.execute(this)
            if (debug != null) debug.invoke(instruction, registers, pc)
            if (instruction !is Jnz) pc++
        }
        return this
    }

    abstract class Instr() {
        abstract fun execute(coprocessor: Coprocessor)
    }
    data class Set(val r: Char, val i: Param) : Instr() {
        override fun execute(coprocessor: Coprocessor) {
            coprocessor.registers.set(r, i.value(coprocessor))
        }
    }
    data class Sub(val r: Char, val i: Param) : Instr() {
        override fun execute(coprocessor: Coprocessor) {
            coprocessor.registers[r] = (coprocessor.registers[r]?:0) - i.value(coprocessor)
        }
    }
    data class Mul(val r: Char, val i: Param) : Instr() {
        override fun execute(coprocessor: Coprocessor) {
            coprocessor.registers[r] = (coprocessor.registers[r]?:0) * i.value(coprocessor)
        }
    }
    data class Jnz(val r: Param, val i: Param) : Instr() {
        override fun execute(coprocessor: Coprocessor) {
            if (r.value(coprocessor) == 0L) coprocessor.pc++
            else coprocessor.pc = coprocessor.pc + i.value(coprocessor).toInt()
        }
    }

    abstract class Param() {
        abstract fun value(coprocessor: Coprocessor): Long
    }
    data class Const(val v: Long) : Param() {
        override fun value(coprocessor: Coprocessor) = v
    }
    data class Register(val r: Char) : Param() {
        override fun value(coprocessor: Coprocessor) = coprocessor.registers[r]?:0
    }

}

fun parseCoprocessorInstructions(input: String) =
        input.split("\n")
                .mapIndexed { index, s ->  Pair(index, s)}
                .filter { ! it.second.isBlank() }
                .map {
                    val parts = it.second.trim().split("""\s+""".toRegex())
                    val cmd = parts[0]
                    val par1 = parts[1]
                    val par2 = if (parts.size >= 3) parts[2] else null
                    when(cmd) {
                        "set" -> Coprocessor.Set(parseCoprocessorRegister(par1), parseCoprocessorParamenter(par2!!))
                        "sub" -> Coprocessor.Sub(parseCoprocessorRegister(par1), parseCoprocessorParamenter(par2!!))
                        "mul" -> Coprocessor.Mul(parseCoprocessorRegister(par1), parseCoprocessorParamenter(par2!!))
                        "jnz" -> Coprocessor.Jnz(parseCoprocessorParamenter(par1), parseCoprocessorParamenter(par2!!))
                        else -> throw IllegalArgumentException("Cmd: $cmd illegal, line ${it.first}")
                    }
                }

fun parseCoprocessorParamenter(par: String): Coprocessor.Param =
        if (par[0].isLetter()) Coprocessor.Register(par[0])
        else Coprocessor.Const(par.toLong())
fun parseCoprocessorRegister(par: String): Char = par[0]

val day23Input = """
set b 99
set c b
jnz a 2
jnz 1 5
mul b 100
sub b -100000
set c b
sub c -17000
set f 1
set d 2
set e 2
set g d
mul g e
sub g b
jnz g 2
set f 0
sub e -1
set g e
sub g b
jnz g -8
sub d -1
set g d
sub g b
jnz g -13
jnz f 2
sub h -1
set g b
sub g c
jnz g 2
jnz 1 3
sub b -17
jnz 1 -23
"""