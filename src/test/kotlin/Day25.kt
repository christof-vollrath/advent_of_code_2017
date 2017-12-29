
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/*
--- Day 25: The Halting Problem ---

Following the twisty passageways deeper and deeper into the CPU,
you finally reach the core of the computer.
Here, in the expansive central chamber, you find a grand apparatus that fills the entire room,
suspended nanometers above your head.

You had always imagined CPUs to be noisy, chaotic places, bustling with activity.
Instead, the room is quiet, motionless, and dark.

Suddenly, you and the CPU's garbage collector startle each other.
"It's not often we get many visitors here!", he says. You inquire about the stopped machinery.

"It stopped milliseconds ago; not sure why. I'm a garbage collector, not a doctor."
You ask what the machine is for.

"Programs these days, don't know their origins.
That's the Turing machine! It's what makes the whole computer work."
You try to explain that Turing machines are merely models of computation,
but he cuts you off. "No, see, that's just what they want you to think.
Ultimately, inside every CPU, there's a Turing machine driving the whole thing!
Too bad this one's broken. We're doomed!"

You ask how you can help.
"Well, unfortunately, the only way to get the computer running again
would be to create a whole new Turing machine from scratch,
but there's no way you can-"
He notices the look on your face, gives you a curious glance, shrugs,
and goes back to sweeping the floor.

You find the Turing machine blueprints (your puzzle input)
on a tablet in a nearby pile of debris.
Looking back up at the broken Turing machine above, you can start to identify its parts:

A tape which contains 0 repeated infinitely to the left and right.
A cursor, which can move left or right along the tape and read or write values at its current position.
A set of states, each containing rules about what to do based on the current value under the cursor.
Each slot on the tape has two possible values: 0 (the starting value for all slots) and 1.
Based on whether the cursor is pointing at a 0 or a 1,
the current state says what value to write at the current position of the cursor,
whether to move the cursor left or right one slot, and which state to use next.

For example, suppose you found the following blueprint:

Begin in state A.
Perform a diagnostic checksum after 6 steps.

In state A:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state B.
  If the current value is 1:
    - Write the value 0.
    - Move one slot to the left.
    - Continue with state B.

In state B:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the left.
    - Continue with state A.
  If the current value is 1:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state A.

Running it until the number of steps required to take the listed diagnostic checksum
would result in the following tape configurations (with the cursor marked in square brackets):

... 0  0  0 [0] 0  0 ... (before any steps; about to run state A)
... 0  0  0  1 [0] 0 ... (after 1 step;     about to run state B)
... 0  0  0 [1] 1  0 ... (after 2 steps;    about to run state A)
... 0  0 [0] 0  1  0 ... (after 3 steps;    about to run state B)
... 0 [0] 1  0  1  0 ... (after 4 steps;    about to run state A)
... 0  1 [1] 0  1  0 ... (after 5 steps;    about to run state B)
... 0  1  1 [0] 1  0 ... (after 6 steps;    about to run state A)

The CPU can confirm that the Turing machine is working by taking a diagnostic checksum
after a specific number of steps (given in the blueprint).
Once the specified number of steps have been executed, the Turing machine should pause;
once it does, count the number of times 1 appears on the tape.
In the above example, the diagnostic checksum is 3.

Recreate the Turing machine and save the computer!
What is the diagnostic checksum it produces once it's working again?

Your puzzle answer was 633.

--- Part Two ---

The Turing machine, and soon the entire computer, springs back to life.
A console glows dimly nearby, awaiting your command.

> reboot printer

Error: That command requires priority 50. You currently have priority 0.
You must deposit 50 stars to increase your priority to the required level.
The console flickers for a moment, and then prints another message:

Star accepted.

You must deposit 49 stars to increase your priority to the required level.
The garbage collector winks at you, then continues sweeping.

You don't have enough stars to reboot the printer, though. You need 2 more.


 */

class Day25Spec : Spek({
    describe("example turing machine ") {
        on("example input") {
            val inputString = day25ExampleInput
            val machine = parseTuringMachine(inputString)
            machine.runDiagnostic()
            machine.tape.size `should equal` 3
            machine.tape `should equal` setOf(-2, -1, 1)
        }
    }
    describe("tape to string diagnostic output") {
        on("empty tape") {
            val tape = setOf<Int>()
            it("should be empty string") {
                tapeToString(tape) `should equal` ""
            }
        }
        on("tape with one 1") {
            val tape = setOf(0)
            it("should be string 1") {
                tapeToString(tape) `should equal` "1"
            }
        }
        on("tape with some positions set to 1") {
            val tape = setOf(0, -5, 3)
            it("should be correct string") {
                tapeToString(tape) `should equal` "100001001"
            }
        }
    }
    describe("parse turing machine") {
        on("example input") {
            val inputString = day25ExampleInput
            it("should be parsed to the correct machine") {
                val machine = parseTuringMachine(inputString)
                machine.diagnosticSteps `should equal` 6
                machine.state `should equal` 'A'
                machine.stateTable `should equal` mapOf(
                        Pair('A', 0) to TuringInstruction(write = 1, direction = TuringDirection.RIGHT, nextState = 'B'),
                        Pair('A', 1) to TuringInstruction(write = 0, direction = TuringDirection.LEFT, nextState = 'B'),
                        Pair('B', 0) to TuringInstruction(write = 1, direction = TuringDirection.LEFT, nextState = 'A'),
                        Pair('B', 1) to TuringInstruction(write = 1, direction = TuringDirection.RIGHT, nextState = 'A')
                )
            }
        }
    }
    describe("exercise turing machine ") {
        on("exercise input") {
            val inputString = day25Input
            val machine = parseTuringMachine(inputString)
            println("Diagnostic run is ${machine.diagnosticSteps} steps")
            machine.runDiagnostic()
            println("checksum: ${machine.tape.size}")
            machine.tape.size `should equal` 633
        }
    }
})


class TuringMachine(var state: Char, val diagnosticSteps: Int, val stateTable: Map<Pair<Char, Int>, TuringInstruction>, var pos: Int = 0, val tape: MutableSet<Int> = mutableSetOf()) {
    fun runDiagnostic() {
        (1..diagnosticSteps).forEach { execute() }
    }

    fun execute() {
        val instr = stateTable[Pair(state, valueAt(pos, tape))] ?: throw IllegalArgumentException("State table incomplete for state $state ${valueAt(pos, tape)}")
        setValue(pos, tape, instr.write)
        if (instr.direction == TuringDirection.RIGHT) pos++
        else pos--
        state = instr.nextState
        //println(tapeToString(tape))
    }

    fun setValue(pos: Int, tape: MutableSet<Int>, write: Int) = if (write == 1) tape.add(pos) else tape.remove(pos)
    fun valueAt(pos: Int, tape: Set<Int>) = if (tape.contains(pos)) 1 else 0
}

fun tapeToString(tape: Set<Int>): String =
    if (tape.isEmpty()) ""
    else {
        val min = tape.min()!!
        val max = tape.max()!!
        (min..max).map {
            if (tape.contains(it)) '1'
            else '0'
        }.joinToString("")
    }

data class TuringInstruction(val write: Int, val direction: TuringDirection, val nextState: Char)
enum class TuringDirection { RIGHT, LEFT }

fun parseTuringMachine(inputString: String): TuringMachine {
    val defs = inputString.split("\n\n")
    val header = parseHeader(defs[0])
    val turingInstructions = parseTuringStateTable(defs.drop(1))
    return TuringMachine(state = header.first,
            diagnosticSteps = header.second,
            stateTable = turingInstructions)
}

fun parseHeader(input: String): Pair<Char, Int> {
    val parts = input.split("\n").filter { ! it.isBlank() }
    val statePattern = """Begin in state (\w)\.""".toPattern()
    val stateMatcher = statePattern.matcher(parts[0])
    stateMatcher.find()
    val state = stateMatcher.group(1)
    val diagnosticPattern = """Perform a diagnostic checksum after (\d*) steps\.""".toPattern()
    val diagnosticMatcher = diagnosticPattern.matcher(parts[1])
    diagnosticMatcher.find()
    val diagnostic = diagnosticMatcher.group(1).toInt()
    return Pair(state[0], diagnostic)
}

fun parseTuringStateTable(input: List<String>) = input.flatMap {
    parseStateTableLine(it)
}.toMap()

fun parseStateTableLine(input: String): List<Pair<Pair<Char, Int>, TuringInstruction>> {
    val lines = input.split("\n")
    val state = parseTuringState(lines[0])
    val currentValue1 = parseTuringCurrentValue(lines[1])
    val turingInstruction1 = parseTuringInstruction(lines.drop(2))
    val currentValue2 = parseTuringCurrentValue(lines[5])
    val turingInstruction2 = parseTuringInstruction(lines.drop(6))
    return listOf(
            Pair(Pair(state, currentValue1), turingInstruction1),
            Pair(Pair(state, currentValue2), turingInstruction2)
    )
}

fun parseTuringState(input: String): Char {
    val statePattern = """In state (\w):""".toPattern()
    val stateMatcher = statePattern.matcher(input)
    stateMatcher.find()
    return stateMatcher.group(1)[0]
}

fun parseTuringCurrentValue(input: String): Int {
    val valuePattern = """\s*If the current value is (\d):""".toPattern()
    val valueMatcher = valuePattern.matcher(input)
    valueMatcher.find()
    return valueMatcher.group(1).toInt()
}

fun parseTuringInstruction(lines: List<String>): TuringInstruction {
    val writeValuePattern = """\s*- Write the value (\d)\.""".toPattern()
    val writeValueMatcher = writeValuePattern.matcher(lines[0])
    writeValueMatcher.find()
    val write = writeValueMatcher.group(1).toInt()

    val dirPattern = """\s*- Move one slot to the (\w*)\.""".toPattern()
    val dirMatcher = dirPattern.matcher(lines[1])
    dirMatcher.find()
    val dir = TuringDirection.valueOf(dirMatcher.group(1).toUpperCase())

    val nextStatePattern = """\s*- Continue with state (\w)\.""".toPattern()
    val nextStateMatcher = nextStatePattern.matcher(lines[2])
    nextStateMatcher.find()
    val nextState = nextStateMatcher.group(1)[0]
    return TuringInstruction(write, dir, nextState)
}

val day25ExampleInput = """
Begin in state A.
Perform a diagnostic checksum after 6 steps.

In state A:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state B.
  If the current value is 1:
    - Write the value 0.
    - Move one slot to the left.
    - Continue with state B.

In state B:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the left.
    - Continue with state A.
  If the current value is 1:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state A.
"""

val day25Input = """
Begin in state A.
Perform a diagnostic checksum after 12302209 steps.

In state A:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state B.
  If the current value is 1:
    - Write the value 0.
    - Move one slot to the left.
    - Continue with state D.

In state B:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state C.
  If the current value is 1:
    - Write the value 0.
    - Move one slot to the right.
    - Continue with state F.

In state C:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the left.
    - Continue with state C.
  If the current value is 1:
    - Write the value 1.
    - Move one slot to the left.
    - Continue with state A.

In state D:
  If the current value is 0:
    - Write the value 0.
    - Move one slot to the left.
    - Continue with state E.
  If the current value is 1:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state A.

In state E:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the left.
    - Continue with state A.
  If the current value is 1:
    - Write the value 0.
    - Move one slot to the right.
    - Continue with state B.

In state F:
  If the current value is 0:
    - Write the value 0.
    - Move one slot to the right.
    - Continue with state C.
  If the current value is 1:
    - Write the value 0.
    - Move one slot to the right.
    - Continue with state E.
"""
