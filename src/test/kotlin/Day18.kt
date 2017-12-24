import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/*
--- Day 18: Duet ---

You discover a tablet containing some strange assembly code labeled simply "Duet".
Rather than bother the sound card with it, you decide to run the code yourself.
Unfortunately, you don't see any documentation, so you're left to figure out what the instructions mean on your own.

It seems like the assembly is meant to operate on a set of registers
that are each named with a single letter and that can each hold a single integer.
You suppose each register should start with a value of 0.

There aren't that many instructions, so it shouldn't be hard to figure out what they do.
Here's what you determine:

snd X plays a sound with a frequency equal to the value of X.
set X Y sets register X to the value of Y.
add X Y increases register X by the value of Y.
mul X Y sets register X to the result of multiplying the value contained in register X by the value of Y.
mod X Y sets register X to the remainder of dividing the value contained in register X by the value of Y
(that is, it sets X to the result of X modulo Y).
rcv X recovers the frequency of the last sound played, but only when the value of X is not zero.
(If it is zero, the command does nothing.)
jgz X Y jumps with an offset of the value of Y, but only if the value of X is greater than zero.
(An offset of 2 skips the next instruction, an offset of -1 jumps to the previous instruction, and so on.)
Many of the instructions can take either a register (a single letter) or a number.
The value of a register is the integer it contains; the value of a number is that number.

After each jump instruction, the program continues with the instruction to which the jump jumped.
After any other instruction, the program continues with the next instruction.
Continuing (or jumping) off either end of the program terminates it.

For example:

set a 1
add a 2
mul a a
mod a 5
snd a
set a 0
rcv a
jgz a -1
set a 1
jgz a -2

The first four instructions set a to 1, add 2 to it, square it, and then set it to itself modulo 5,
resulting in a value of 4.
Then, a sound with frequency 4 (the value of a) is played.
After that, a is set to 0, causing the subsequent rcv and jgz instructions to both be skipped
(rcv because a is 0, and jgz because a is not greater than 0).
Finally, a is set to 1, causing the next jgz instruction to activate,
jumping back two instructions to another jump, which jumps again to the rcv,
which ultimately triggers the recover operation.
At the time the recover operation is executed, the frequency of the last sound played is 4.

What is the value of the recovered frequency (the value of the most recently played sound)
the first time a rcv instruction is executed with a non-zero value?

Your puzzle answer was 8600.

--- Part Two ---

As you congratulate yourself for a job well done,
you notice that the documentation has been on the back of the tablet this entire time.
While you actually got most of the instructions correct, there are a few key differences.
This assembly code isn't about sound at all - it's meant to be run twice at the same time.

Each running copy of the program has its own set of registers and follows the code independently
- in fact, the programs don't even necessarily run at the same speed.
To coordinate, they use the send (snd) and receive (rcv) instructions:

snd X sends the value of X to the other program.
These values wait in a queue until that program is ready to receive them.
Each program has its own message queue, so a program can never receive a message it sent.
rcv X receives the next value and stores it in register X.
If no values are in the queue, the program waits for a value to be sent to it.
Programs do not continue to the next instruction until they have received a value.
Values are received in the order they are sent.
Each program also has its own program ID (one 0 and the other 1);
the register p should begin with this value.

For example:

snd 1
snd 2
snd p
rcv a
rcv b
rcv c
rcv d

Both programs begin by sending three values to the other.
Program 0 sends 1, 2, 0; program 1 sends 1, 2, 1.
Then, each program receives a value (both 1) and stores it in a,
receives another value (both 2) and stores it in b, and then each receives the program ID of the other program
(program 0 receives 1; program 1 receives 0) and stores it in c.
Each program now sees a different value in its own copy of register c.

Finally, both programs try to rcv a fourth time, but no data is waiting for either of them,
and they reach a deadlock.
When this happens, both programs terminate.

It should be noted that it would be equally valid for the programs to run at different speeds;
for example, program 0 might have sent all three values
and then stopped at the first rcv before program 1 executed even its first instruction.

Once both of your programs have terminated (regardless of what caused them to do so),
how many times did program 1 send a value?

 */

class Day18Spec : Spek({
    describe("duet") {
        on("example input") {
            it("should have last sound played 4") {
                val instructions = parseDuetInstructions(day18ExampleInput)
                Duet(instructions).execute().sound `should equal` 4L
            }
        }
    }

    describe("instruction parameter") {
        on("const parameter") {
            val duet = Duet()
            it("should have value") {
                Duet.Const(7).value(duet) `should equal` 7L
            }
        }
        on("register parameter") {
            val duet = Duet(registers = mutableMapOf('a' to 5L))
            it("should have value of register") {
                Duet.Register('a').value(duet) `should equal` 5L
            }
        }
    }
    describe("duet instructions") {
        on("set") {
            val duet = Duet()
            it("should set register a from value") {
                Duet.Set('a', Duet.Const(2)).execute(duet)
                duet.registers['a'] `should equal` 2L
            }
            it("should set register b from register a") {
                Duet.Set('b', Duet.Register('a')).execute(duet)
                duet.registers['b'] `should equal` 2L
            }
            it("should set register b from empty register c") {
                Duet.Set('b', Duet.Register('c')).execute(duet)
                duet.registers['b'] `should equal` 0L
            }
        }
        on("add") {
            val duet = Duet()
            it("should add to register which is empty") {
                Duet.Add('a', Duet.Const(3)).execute(duet)
                duet.registers['a'] `should equal` 3L
            }
            it("should add to register which contains 3") {
                Duet.Add('a', Duet.Const(2)).execute(duet)
                duet.registers['a'] `should equal` 5L
            }
            it("should add a to register b") {
                Duet.Add('b', Duet.Register('a')).execute(duet)
                duet.registers['a'] `should equal` 5L
            }
            it("should add empty c to register b") {
                Duet.Add('b', Duet.Register('c')).execute(duet)
                duet.registers['b'] `should equal` 5L
            }
        }
        on("mul") {
            val duet = Duet()
            Duet.Set('a', Duet.Const(2)).execute(duet)
            it("should multiply register a and value") {
                Duet.Mul('a', Duet.Const(3)).execute(duet)
                duet.registers['a'] `should equal` 6L
            }
        }
        on("mod") {
            val duet = Duet()
            Duet.Set('a', Duet.Const(5)).execute(duet)
            it("should calculate register a mod value") {
                Duet.Mod('a', Duet.Const(3)).execute(duet)
                duet.registers['a'] `should equal` 2L
            }
        }
        on("snd") {
            val duet = Duet()
            Duet.Set('a', Duet.Const(5)).execute(duet)
            it("should sound frequency") {
                Duet.SoundPart1(Duet.Const(4)).execute(duet)
                duet.sound `should equal` 4L
            }
            it("should sound frequenc from register") {
                Duet.SoundPart1(Duet.Register('a')).execute(duet)
                duet.sound `should equal` 5L
            }
        }
        on("rcv") {
            val duet = Duet()
            it("should not stop when zero value") {
                Duet.RcvSoundPart1(Duet.Const(0)).execute(duet)
                duet.stop `should equal` false
            }
            it("should stop when non zero value") {
                Duet.RcvSoundPart1(Duet.Const(1)).execute(duet)
                duet.stop `should equal` true
            }
        }
        on("jgz") {
            val duet = Duet()
            it("should not jump when zero value") {
                Duet.Jgz(Duet.Const(0), Duet.Const(3)).execute(duet)
                duet.pc `should equal` 1
            }
            it("should jump when greater zero value") {
                Duet.Jgz(Duet.Const(1), Duet.Const(3)).execute(duet)
                duet.pc `should equal` 4
            }
        }
    }
    describe("parse instructions") {
        on("example input") {
            val input = day18ExampleInput
            it("should parse to the correct instructions") {
                parseDuetInstructions(input) `should equal` listOf(
                        Duet.Set('a', Duet.Const(1)),
                        Duet.Add('a', Duet.Const(2)),
                        Duet.Mul('a', Duet.Register('a')),
                        Duet.Mod('a', Duet.Const(5)),
                        Duet.SoundPart1(Duet.Register('a')),
                        Duet.Set('a', Duet.Const(0)),
                        Duet.RcvSoundPart1(Duet.Register('a')),
                        Duet.Jgz(Duet.Register('a'), Duet.Const(-1)),
                        Duet.Set('a', Duet.Const(1)),
                        Duet.Jgz(Duet.Register('a'), Duet.Const(-2))
                )
            }
        }
    }
    describe("duet part 1") {
        on("exercise input") {
            val instructions = parseDuetInstructions(day18Input)
            val received = Duet(instructions).execute().sound
            println("sound received: $received")
            received `should equal` 8600L
        }
    }

})

data class Duet(val instructions: List<Instr> = listOf(),
                val id: Int = 0,
                val registers: MutableMap<Char, Long> = mutableMapOf(),
                var sound: Long = 0,
                var pc: Int = 0,
                var stop: Boolean = false) {
    fun execute(): Duet {
        while (! stop) {
            val instruction = instructions[pc]
            println("pc: ${pc} instr: $instruction sound: ${sound} registers: ${registers}")
            instruction.execute(this)
            if (instruction !is Jgz) pc++
        }
        return this
    }
    abstract class Instr() {
        abstract fun execute(duet: Duet)
    }
    data class Set(val r: Char, val i: Param) : Instr() {
        override fun execute(duet: Duet) {
            duet.registers.set(r, i.value(duet))
        }
    }
    data class Add(val r: Char, val i: Param) : Instr() {
        override fun execute(duet: Duet) {
            duet.registers[r] = (duet.registers[r]?:0) + i.value(duet)
        }
    }
    data class Mul(val r: Char, val i: Param) : Instr() {
        override fun execute(duet: Duet) {
            duet.registers[r] = (duet.registers[r]?:0) * i.value(duet)
        }
    }
    data class Mod(val r: Char, val i: Param) : Instr() {
        override fun execute(duet: Duet) {
            duet.registers[r] = (duet.registers[r]?:0) % i.value(duet)
        }
    }
    data class SoundPart1(val i: Param) : Instr() {
        override fun execute(duet: Duet) {
            duet.sound = i.value(duet)
        }
    }
    data class RcvSoundPart1(val i: Param) : Instr() {
        override fun execute(duet: Duet) {
            if (i.value(duet) != 0L) duet.stop = true
        }
    }
    data class Jgz(val r: Param, val i: Param) : Instr() {
        override fun execute(duet: Duet) {
            if (r.value(duet) <= 0L) duet.pc++
            else duet.pc = duet.pc + i.value(duet).toInt()
        }
    }

    abstract class Param() {
        abstract fun value(duet: Duet): Long
    }
    data class Const(val v: Long) : Param() {
        override fun value(duet: Duet) = v
    }
    data class Register(val r: Char) : Param() {
        override fun value(duet: Duet) = duet.registers[r]?:0
    }
}


fun parseDuetInstructions(input: String) =
        input.split("\n")
        .mapIndexed { index, s ->  Pair(index, s)}
        .filter { ! it.second.isBlank() }
                .map {
                    val parts = it.second.trim().split("""\s+""".toRegex())
                    val cmd = parts[0]
                    val par1 = parts[1]
                    val par2 = if (parts.size >= 3) parts[2] else null
                    when(cmd) {
                        "set" -> Duet.Set(parseRegister(par1), parseParamenter(par2!!))
                        "add" -> Duet.Add(parseRegister(par1), parseParamenter(par2!!))
                        "mul" -> Duet.Mul(parseRegister(par1), parseParamenter(par2!!))
                        "mod" -> Duet.Mod(parseRegister(par1), parseParamenter(par2!!))
                        "snd" -> Duet.SoundPart1(parseParamenter(par1))
                        "rcv" -> Duet.RcvSoundPart1(parseParamenter(par1))
                        "jgz" -> Duet.Jgz(parseParamenter(par1), parseParamenter(par2!!))
                        else -> throw IllegalArgumentException("Cmd: $cmd illegal, line ${it.first}")
                    }
                }

fun parseParamenter(par: String): Duet.Param =
        if (par[0].isLetter()) Duet.Register(par[0])
        else Duet.Const(par.toLong())
fun parseRegister(par: String): Char = par[0]


val day18ExampleInput = """
        set a 1
        add a 2
        mul a a
        mod a 5
        snd a
        set a 0
        rcv a
        jgz a -1
        set a 1
        jgz a -2
        """

val day18Input = """
        set i 31
        set a 1
        mul p 17
        jgz p p
        mul a 2
        add i -1
        jgz i -2
        add a -1
        set i 127
        set p 735
        mul p 8505
        mod p a
        mul p 129749
        add p 12345
        mod p a
        set b p
        mod b 10000
        snd b
        add i -1
        jgz i -9
        jgz a 3
        rcv b
        jgz b -1
        set f 0
        set i 126
        rcv a
        rcv b
        set p a
        mul p -1
        add p b
        jgz p 4
        snd a
        set a b
        jgz 1 3
        snd b
        set f 1
        add i -1
        jgz i -11
        snd a
        jgz f -16
        jgz a -19
        """