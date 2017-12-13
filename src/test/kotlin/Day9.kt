import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 9: Stream Processing ---

A large stream blocks your path.
According to the locals, it's not safe to cross the stream at the moment because it's full of garbage.
You look down at the stream; rather than water, you discover that it's a stream of characters.

You sit for a while and record part of the stream (your puzzle input).
The characters represent groups - sequences that begin with { and end with }.
Within a group, there are zero or more other things, separated by commas: either another group or garbage.
Since groups can contain other groups, a } only closes the most-recently-opened unclosed group
- that is, they are nestable.
Your puzzle input represents a single, large group which itself contains many smaller ones.

Sometimes, instead of a group, you will find garbage.
Garbage begins with < and ends with >.
Between those angle brackets, almost any character can appear, including { and }.
Within garbage, < has no special meaning.

In a futile attempt to clean up the garbage,
some program has canceled some of the characters within it using !:
inside garbage, any character that comes after ! should be ignored, including <, >, and even another !.

You don't see any characters that deviate from these rules.
Outside garbage, you only find well-formed groups, and garbage always terminates according to the rules above.

Here are some self-contained pieces of garbage:

<>, empty garbage.
<random characters>, garbage containing random characters.
<<<<>, because the extra < are ignored.
<{!>}>, because the first > is canceled.
<!!>, because the second ! is canceled, allowing the > to terminate the garbage.
<!!!>>, because the second ! and the first > are canceled.
<{o"i!a,<{i<a>, which ends at the first >.

Here are some examples of whole streams and the number of groups they contain:

{}, 1 group.
{{{}}}, 3 groups.
{{},{}}, also 3 groups.
{{{},{},{{}}}}, 6 groups.
{<{},{},{{}}>}, 1 group (which itself contains garbage).
{<a>,<a>,<a>,<a>}, 1 group.
{{<a>},{<a>},{<a>},{<a>}}, 5 groups.
{{<!>},{<!>},{<!>},{<a>}}, 2 groups (since all but the last > are canceled).

Your goal is to find the total score for all groups in your input.
Each group is assigned a score which is one more than the score of the group that immediately contains it.
(The outermost group gets a score of 1.)

{}, score of 1.
{{{}}}, score of 1 + 2 + 3 = 6.
{{},{}}, score of 1 + 2 + 2 = 5.
{{{},{},{{}}}}, score of 1 + 2 + 3 + 3 + 3 + 4 = 16.
{<a>,<a>,<a>,<a>}, score of 1.
{{<ab>},{<ab>},{<ab>},{<ab>}}, score of 1 + 2 + 2 + 2 + 2 = 9.
{{<!!>},{<!!>},{<!!>},{<!!>}}, score of 1 + 2 + 2 + 2 + 2 = 9.
{{<a!>},{<a!>},{<a!>},{<ab>}}, score of 1 + 2 = 3.

What is the total score for all groups in your input?

--- Part Two ---

Now, you're ready to remove the garbage.

To prove you've removed it, you need to count all of the characters within the garbage.
The leading and trailing < and > don't count, nor do any canceled characters or the ! doing the canceling.

<>, 0 characters.
<random characters>, 17 characters.
<<<<>, 3 characters.
<{!>}>, 2 characters.
<!!>, 0 characters.
<!!!>>, 0 characters.
<{o"i!a,<{i<a>, 10 characters.

How many non-canceled characters are within the garbage in your puzzle input?


 */


// TODO full description in file
// TODO use str.toInt instead of Integer.parseInt
// TODO use correct spek style (describe, given)
// TODO read test data from file


data class State(var garbage: Boolean = false,
                 var escape: Boolean = false,
                 var groupsOpened: Int = 0,
                 var groups: Int = 0,
                 var score: Int = 0,
                 var garbageCount: Int = 0)

fun Sequence<Char>.skipGarbage(state: State): Sequence<Char> {
    return this.filter {
        if (state.escape) {
            state.escape = false
            !state.garbage
        } else {
            when(it) {
                '!' -> {
                    state.escape = true
                    false
                }
                '<' -> {
                    if (state.garbage) state.garbageCount++
                    else state.garbage = true
                    false
                }
                '>' -> {
                    if (state.garbage) {
                        state.garbage = false
                        false
                    } else true
                }
                else -> if (state.garbage) {
                            state.garbageCount++
                            false
                        }
                        else true
            }
        }
    }
}

fun skipGarbage(input: String) = input.asSequence().skipGarbage(State()).joinToString("")

fun groups(input: String) = groups(input.asSequence())
fun groups(input: Sequence<Char>) = with(State()) {
    handleGroups(input, this).toList() // call toList so that sequence is pulled
    this.score
}

fun handleGroups(input: Sequence<Char>, state: State) =
    input.skipGarbage(state)
        .map {
            when (it) {
                '{' -> state.groupsOpened++
                '}' -> {
                    state.groups++
                    state.score += state.groupsOpened
                    state.groupsOpened--
                }
            }
            it
        }

fun countGarbage(input: String) = countGarbage(input.asSequence())
fun countGarbage(input: Sequence<Char>)= with(State()) {
    handleGroups(input, this).toList() // call toList so that sequence is pulled
    this.garbageCount
}

class Day9Spec : Spek({
    describe("skip garbage") {
        on("<>") {
            val input = "<>"

            it("should be empty") {
                skipGarbage(input) `should equal` ""
            }
        }
        on("A<>B") {
            val input = "A<>B"

            it("should be AB") {
                skipGarbage(input) `should equal` "AB"
            }
        }
        on("<random characters>") {
            val input = "<random characters>"

            it("should be empty") {
                skipGarbage(input) `should equal` ""
            }
        }
        on("<<<<>") {
            val input = "<<<<>"

            it("should be empty") {
                skipGarbage(input) `should equal` ""
            }
        }
        on("<{!>}>") {
            val input = "<{!>}>"

            it("should be empty") {
                skipGarbage(input) `should equal` ""
            }
        }
        on("A<!!>B") {
            val input = "A<!!>B"

            it("should be AB") {
                skipGarbage(input) `should equal` "AB"
            }
        }
    }
    describe("some garbage cases") {

        val testData = arrayOf(
                //         string         result
                //--|--------------------|----------------
                data("<!!>",            ""),
                data("<!!!>>",          ""),
                data("<{o\"i!a,<{i<a>", ""),
                data("!<<>",            "<")
        )

        onData("skip garbage escaped %s", with = *testData) { input, expected ->

            it("returns $expected") {
                skipGarbage(input) `should equal` expected
            }
        }
    }
    describe("count groups") {
        on("{}") {
            val input = "{}"
            it("should be 1") {
                groups(input) `should equal` 1
            }
        }
    }
    describe("score of groups in example") {

        val testData = arrayOf(
                //         string                       result
                //--|---------------------------------|----------------
                data("{{{}}}",                        6),
                data("{{},{}}",                       5),
                data("{<a>,<a>,<a>,<a>}",             1),
                data("{{<ab>},{<ab>},{<ab>},{<ab>}}", 9),
                data("{{<!!>},{<!!>},{<!!>},{<!!>}}", 9),
                data("{{<a!>},{<a!>},{<a!>},{<ab>}}", 3)
        )

        onData("input %s", with = *testData) { input, expected ->
            it("returns $expected") {
                groups(input) `should equal` expected
            }
        }
    }
    describe("score of groups in input") {
        println("score: ${groups(day9input)}")
    }

    describe("count garbage") {

        val testData = arrayOf(
                //         string               result
                //--|------------------------|----------------
                data("<>",                 0),
                data("A<>B",               0),
                data("<random characters>",17),
                data("<<<<>",              3),
                data("<{!>}>",             2),
                data("<!!>",               0),
                data("<!!!>>",             0),
                data("<{o\"i!a,<{i<a>",    10)
        )

        onData("input %s", with = *testData) { input, expected ->
            it("returns $expected") {
                countGarbage(input) `should equal` expected
            }
        }
    }

    describe("garbage in input") {
        println("garbage: ${countGarbage(day9input)}")
    }


})


val day9input = "{{{{{{{{<a}!!!aa!!!>ua,a,!>{!>!!!>a>,<!>,!!!!,!!!\"!,!a!e}!>!>,<!!oi!!}>},<!!'u\"!!!!!>,<e>}},{{<}eaa'<!>},<<>},<}<!},!>},<e>}}},{{{{}}},{{{<,!>,<!!}!>!!!>!!!>!>!>a!!,!!',>},<e,!!!!!><'!<,>},{{{<!<!>i'u\">}},<!e!!!>!!eo!!\"\"'!>,<a!i!!!>!!,,>}}},{{{{{{<o!!''!!!>!>,<e}a!>},<>}}},{{<aeo!!!>!!!!<!!!!!!!>,<uu!>},<{!!!>>},{<>}}},{{{<},!!'>}},{{<!\"}}!!!ooe'!o!!!!<>},{<u!'aoa!!u{!o!>!>},<!!!>'>}},{{{<!>,<!!!>}!!!>u>},<'a,!>,<!{<!!!>e!>!u>},{{{{},<!>e!\"\"!!'e\">},<{e!!!>!}'}o!>,<,o!>},<!!}!>,<i>},<!>},<!oo'!>},<}}<'o}!>,<'!!!>'>},{<{e>,{}}}},{{},{}}},{{<uu!!!!o!>,<!<!!o!!!>!>e\"'!>},<!!e!,!!!u!!!>>},<,!>}}<<!!!>,<ai!>{!e>}},{{{<a'!>},<o{ie!>},<!!!>{}au!>},<!!!!iu!ai>,{<!!{',>}},{<eu!o,!<}!>i!\"!!!>a,uu!ao>},{<!!!>,<oe!>{e!!!>},<!!!!!!!>!!!>!ei!!io}!>},<\"!!o!}>,<!!!><!!!><!!!!!!!>\"!!!o!>,<!\"!!!>,<!>},<>}},{},{{{{{<\"!!!!!>>}}}},{<{!>},<}i'!!i!>!'}<!'!{}>,{{<!!!>ioi\"u>}}}}}},{{{<{\"'e<e!>},<!o!!,!,'!!!!!>>}},{{{{<ia'oa!!i<o,!>u!!!>ao'a>},{}},<!>},<!!!>!>},<!!!>\"!>!>},<u,!>,<eauu}!!}o\">},{{{<}',\"'>},{{}}},<{<>},{<{!!\"}!!!>!!!>{!,\"!>},<!>,<!}!!!>ae}'>,<!!<u{!!!>!!!>},<o!!!!<!>,<!!}>}}},{{{{<o!!}!>},<a!>i!!!>},<e!e<,>}},{{{<\"iao,}>},{{{}},{{{}},{<!>},<!!i'!>},<o!!{e'!{e\"!!u\"!!!>>}},{{<>,{<!>o\"!>!>},<,ea>}},{{<e'>},{}},{<!<!!!>!>},<!>},<{!e,!>},<},e>,<!!!>\"!!u{u{!>,<!uuuoi!!!>!>,<>}}}},{{{{<!!!>},<<\"!!!>},<iu!>}e<io>},<e!>,<!!}}>},<!>a{{!!!>,<{!a}ua<',!!!o!!!>e,!!!>>},<!!{a!>},<e'oe!!{u!!u>}}},{{{<ai!!{}ai'>,<}!>{!>o!>},<!!!!!!!e!>'}<!a>},{<!!o}!!!>\"!!ou!!o>,{<!>},<!>},<>}}},{{<io>}}},{{{<\"!eui!>},<!!!>!>o!!!}>,{<o!!<ie!>!!!!!>!i!'!!e>}},{{<!!!!!>i!>,<,\"\"}a}{!!!>{u!>,<!!'>},<!!!>,a!!!>,<!!iu<}!!!>>},{{{<!!!!'!<ao!>,<}>,<e!>!!!!!>i>},{<,a!{{{!>,<!>!!a!>},<i,!!'>},{{},{}}},{{<!!!!!>u!>'!>!!!>,o!>,<!>a!!,<!>},<!!',>}},{{<!'u}{>},<'!{'u!>,<!>},<!>,<!!!>},<>}}},{{{<!!e!>'>},<!!!><!\"e!!!!ea<,ue>},{{<i!><u>}},{{<!>a>,<<\"e,u!!!'i!'!!!!!!}!>}i\"aa>}}},{{{<u!>ai!!!>!!!>'!>},<e!>!>u\"i>,<aeiu}>}},{{{<,i!!!>!!e\"au!!'\"u>},{}},{}}}}},{{{{},{{<,!>},<<!!!>\"!>,<!>,<<!>!!!!\"!>,<!>,<>},<\"<<{},!!!o,o!!!>!!!>!>},<>}},{{<{,<!!e!>{>},<\"oi,!!e!!!>i!>,<{!>,<!>},<!!!!o>}},{{{<,{!>'i!>!!!>!!!!!>},<'i{u>},<!>},<!>uo!!o!>,<o!!e!ae!!',!>,<>},{{{<u!!!!!>\"u!!{>},{<!<{uo!>},<<!>},<>}},{}}},{{},{{{{{<,,>},{}},{{<i'!!!>!>!!e!>o!!!!!!!e'!!!><!\"\",!!!>\"u>}},{{<iiu!!{!!!>'aa!!!>},<!!!>!'}!!!>>}}}},{{{<i!!ae<!!u<a!!\"''!>,<>},{<!!!!!>!!o!>a,<!!{!>!>,<,!!!!>}}},{{{{{}},{{<!!'eo!>!o<,e!>,<!>},<!!>},{{<<u',}!!!>!<!i!>},<!>>},{<e<e!>},<}!>,<ii!!<\"!>},<!!!>>}}},{{<{}!>!>i!>,<!>!!!>!!!!\"{!>,<,'!>},<>,{<!iee!!!!\"ao{\",!>u\"!{!!!!}>}},{<{>,{<!>},<!e\">}},{<a!!!>},<u>}}},{{{{<!>},<>},{{<!<e!!!!!!u!!!!!ea!!ue!!u}!>},<!>,<!>},<!!!>a>}}},{{<a!>,<ua<!>},<'e>},{<a!!!!!!!>>}}},{{<i!!!!<!>,<'{,!{!!!!!!}}e!!{<io>},{{<,o\"e!>},<!>},<i{i!>},<!!>}}},{{{<a!>}!!'a\"<ou>}}}}},{{},{<,!>},<>}},{{{},{<>}},{<!a!!!>u!!a>}},{{},{<!!,'>,<}o!!!>!!!>>},{{{<<,,!!!>!!!>!>},<e!!{<!>i!!!>o!!!>'!>},<!!!>>}}}}}},{{{<!\"!\"!,!>},<!!!>a{e!!!>!!!!!>>,<!>,<!>},<!!\"!>,<ii'!!{'>},{<!>},<,>}},{<o!!,!i\"'!>,<>,<ie,!<\"oeu,'!!!>!>},<'i'>},{{<!!,!!{!!eai!!!aa!!!>'<!>},<!!>},<!>},<o!!!>,<iu}\">}}}},{{{{<e>},{{{}},<!i!>,<i'!i!!!>!!!!}!!!>'<!>},<uo!>>}},{<,!!!!!a!>},<!uo!!!>,<!!e!!!>>,<>},{<>}},{{<e!!u!a<{u>,<<!!!>!>},<{!!!>,{!!!!o!u\",!!!>'o!!!!a!'!!,>},{<}!>,<u!!,{!e'!!!>,<e!>!!!>u!!!!!>!>},<!!!>a!!!>!>>}},{{{},{<uueua!!!>!>},<!!!!!><!>,<!!!>e}<>,<}{uuo\"!!o!e>}},{{{<}!>\"!!!>{o!!!!<>},{},{{}}},{},{{<{!!!>eo!>},<,>},<!!{<!>,<{iu{!,!!oi!!\"!{'!!>}}}}},{{{{<!!u!!!!!>e,\"!!e!>!!,'!>,<>},{{{<<!>},<!!!>u>},{<!!!!!>\"<u!'!!!<!>,<a{!!!>,<>}}}},{{<!>,<!>i!!a!>},<e\"!>>},{{<<''o!>!!!>>}}},{{<!!!!u!!!!a,!!<\"!>{ao>},{<<o!!!'!>,<u!>,<o!u!>{!!>,{<},,o}!!\"ioo!>,<}!!,o}i!>,<<a\">,{<!!!>!!!!!!!>!>},<,{!!ia!>,<!!!>!!!>a!!{}o>}}},{<a>,<!>!!o>}},{{{{{<{o!!e'}{,<!!u>}},{{<o\"<!!!>!>},<i{!!a!!!>}'o{!'{>},<!!{u!a!>,<\"!!{>}},{{<!!!>!!o!!<!>!>},<\"!>a{!>},<'!>},<!>},<!!!uo\">,{<}!>},<!!i!>,<!!!>},<!!!>!>},<!!!a!<!>i!!!>>}},{<}ia\"!!!>o,<\",!,>}}},{{<!>,<<a!>,<>,<}a<!!!!}!>},<u'\"!!!>},<'!e>},{<!ue!!!>},<oe>},{{}}},{{}},{{{{}},{{<<<,!!o!!!!e!!!>}!!!>!>,<ia'\"'\"!>},<e{>}},{{<,oe!a}!>>},{<{!>},<!!e!!!>!>!!o!!!>u'\"!>,<!>>}}},{{{<<'!>},<e!!!>!!!!!u!{e!!i!>,<!>},<'!!}!>,<!>},<!>,<!!!!!>e>},{<\"!!!>,<!!!>},<!>}'!!\"!>},<!\"a<!!!>,e!}oe>}},{<\">}}}}},{{{<!!}'!!{a!>,<!>a>},{<!!i!}!!!!!>!>,<e!!!,}!>{!>,<u!a!>,<>}},{{{{<!>!>,<>},{{<\">}}},{{{<,i!!!>}!>,<!>!>,<!!!>},<e!>},<!>''!>>},{}},{{<!>,<{!!!>'!ai!!!>,<>}}},{{{{{<!'!!ei,!>!!!>!>,<!>{!}'o!>!!ui}>}},{{<!!!>!!!>,<e!ou{!>},<ei!!!>!uu!a!>},<>}}},{<<o{{\"!!!>o!>,<!!}!!<!>,<}o'!>>,<}!,,},!>},<o!!{<\"\">}},{<!!!>{<!!!>,<'!>,<i!!!>o!'o>,{}},{<ui!ou!{o>}},{{<,!>},<!!!><\"!!!!!>''>},{{<ou}!>,<!!}'!!\"<!!u!!!}!!e!!\"!>,<!{!>!!!!!>},<!!!!!>,<!!!!!>>}},{{<e!'>},<o{a!!!>,!!'!>},<!>},<!>,<ue\"\"!><<<i>}}},{{{<<!>},<<!!!>,<\"oai,<o!!!>!>>},{<'ue!!!><!!!>{!>},<e!!,!!\">}}},{},{{{},{}},{<!u!>,<!\"!>},<{o!!\">,<>}}},{{{}},{{},{<,u!!'!>!>!>},<}!!e!{!!u!!!>},<!>!>},<,>}},{<>}},{{{<u}!\"!!'u!!i!>,<!!!>}!!!>!''!>!!>},<!!u!!{,ui!{!!!!!>!!!>},<!!!!>},{<iie,!!!!!!!>'>,<!!a!!e',!>u{'!!,<!!!>'>},{}}},{{{{{{},{}}}},{}}},{{{{{<!!u!!}!u!!!>!>},<!>!!u!!{{e<'!>},<'!>!>},<>},{<\"o!>,<!>,<!,!>,<u>}}}}},{{{{<e{<e{!!!>!>,<ii},}!>,<a>}},{<'au!>},<{{aaiu!!>},{<!i,ui!!!!!>i\"!!iiiu}>}},{},{{<}!!'<ua!!!!!!o}a!>,<!>},<>,{}}},{{{{<i!>iie!o\"!!,!>,<!>,<!>,<>},{<i{!>e!>'>}},{}},{{<'!>},<'!!}!!a!>},<e!!a}!{{'!>e>}},{{<}i\"'!>,<!!!>!>},<!ae{!!o!!!>!!,!!!!!>,<!!>},{{<i<!!a,!'>,{<\"i!>},<!,!>'euu!!!>,<,>}}}}}}},{{{{{{<'u!}!>!>},<a>}}},{{{<!!ee!!\"}>,{<eu,!!!!'!!!>!>,<,{oee}<!>},<i,>}}},{{{<u!>,<!!!>ai!>},<\"\"e!!!>!>,<!u>},<!!!!!!'!!'!!i!!\"ei,}!!!i<,!>},<!>>},{<a!!!>aui!>},<e!<!>}\"}!>,<,!!!>!>},<!!!>!!>},{{{}},{<!>!ue\"a,!!!!!>e!!,!>!>,<<>}}},{{{<!>,<\"!!{,<!><!>},<<!!iio!>},<>},{}},{{},<a,\"!>,<}!>,<a!!!>e!!\">},{{<!>!o!!uu!,!>,<u>},{<!a!!!>!>,<iu!o!!ia!!!>!>,<!!!>,<,o!!!>!>!!!>e{>}}},{{{<!>o!!!>}oa\"!!!>!a\"!{>,<i>},{{<'>},{<ee!!!>!!!a!!ia!,!>,<!!!!}!!ui!>u<u>}},{{}}},{{<a<!e!!e!!!!!>!u!!!>\"!>,<o!!}!>>}}}},{{{{}},{{{<!!}{o!u{!<o'}u!>,<!!!!<>}}},{{{<e!!!>!>},<!>!>},<!u>}},<i'ei'!>},<!!oo<i!>},<!!!>,o{!>>}},{<\"e!>,ia\"!>},<u!!!a\"!}<ea!!<!{>}},{{}}},{{{{{<!{aiioi<!ei!>!!!>},<a!!a!>},<!u!!!!!!,!}<a>,{<'oo!!!>!>,<!!!!!>!u!>},<>}},<\"},!!!>,!>!!!>!!!i,>},{{<{!!u\">,<{,{!>},<{!}'>},{{<<o!{'!!!>,<>},{<i'oo!{u!!\">}},{<oo>,<<>}},{{{{<i{!>,<!>},<\"<!!!>e<e!>,<!!!\"\"<!!'o>}},{{},<e\"a!>},<au,>}},<!!i{!>,<!!!!<>}},{{<\"<o}'e!!!!,<>,{<>}},{<>,<!o!>},<\"!!!!!>!\"{{\"''au!!o,<!!!,>},{{<!!!>!>!e<o!>,<>,<>},{<>}}}},{{{{{{<\"}>},{{<!!{u!!!>,<!>},<<!!!!\"'>},{<!>},<'\"}o,o!>},<!!o!>},<!!ie'!>}!>,<a,>}},{<{'e,a!>i!>,<!!!>,<!!!!\"'a,o>}},{{<i\"i\"!>},<!!,!!!>!>'a!{!>,<!!!>,<!>},<e>},{{<!>,<>}}},{{<'i!>,<,o,!!i'i!!e<!}o>},<!>}e{!!o<!!!>o!!,!>},<,!>,<!>},<!>},<o!!{>}}},{{<!i{!!,u}'u!ea>,{{<!!oo>},{<,>}}},{{<!>,<!!i!!!>'!!u}!!,,u}<!!,!ea>},{{<!!i!!,\"!>'o\"{ou,'}>},<!!!>>}}},{{<e'!!<u>},<!>,<i}!>!>,<<!!!>\"{u<uo>}},{{{},{{<u!!euu!>},<>}}},{{},{<!!!>a<<}{\"\"'!!!>>}}},{{<}!{'!!}}e,'e!!'}!>},<!!e>},{{{<u!a,eoo!>,<{!!,<>},{<!>,<>}}},{<!<}uei}o,io!>,<{!!!>,<!>>,<!!{oioi!>,<!!,!>},<iu!>},<!!o,}!u<>}}},{{{{{<!!!!!!e!>},<u!!i!!!>a!\">,<a!>},<}>},{{},{<e!!!>>}}},{{},{<i!!>,{}},{<\"\"{!a>}}},{{{<a,\"!!!>!!'!au',a!'aie'!>,<{o>,{<<<<!!e<>}},{{<e'\"!!a\"\"!!>},{<a!oe!>},<,<>}},{{<}i'<!>!>,<}!!oa!!eeu!>},<a>}}},{{},{{{{}},{<!>!!<!!}>}}}},{<!!!!'i,<!>},<\"{!!\"},>,{<!e'!>},<,!><!!{\"!>!\"!>!!'>}}},{{{<!!io!>,<ioi,>},{<i{!!!>,<!>!<!!!!{ae!!!>!>},<e!>!!!>'a,\"!>},<>}}},{{{<>}}}},{{{{{{<{!>},<!!!!ua!!{e!>a>}},{<oa!!u{,!>},<e!>,<!>'o'u>,{<!!\"!!!>,<}!>!!}!!<\"ui!>,<!\"!>},<,ou>}},{<o!>>,<!>},<{!!,a!>,<!,aa!>'!!!!!>\"!o!>,a>}},{{<!\"!!!e!>,!o,'<ea'i!>>,{<!>},<'!,!>},<e}>}}},{{{<!>,<!!}!!!>,<a!!<e}!o!>i,!>},<,>},<o,>}}},{{{},{{<!a!!!>!>},<o!!e<!!oa!!}!!>},<}!>!!!!i{>}},{<<\"!>!>,<<<!!!>,!!!>!!!\"{a>,<!>,<!!,!!!!!>>}},{{{{<>,{<{o{i'<{!!\"i<!>,<u!>>}}},{{<u!>,<!>,<!!,!}!ee,!!!!'<'!<!><>}},{{{{<!>},<'!!!>ue!o!>!!<!>},<<!!>},<!!!>}>},<!>!>u<>},{{},{<!>},<!o!>,<i<e}>}},{{<u\"u<,u\"'a}<i!!!!!>e}>,{<!>,<!!!>>}},{}}}},{{{<!>,!!!>e>}}},{{<,oe>},{{<<!o!!!>},<!>,<u!!}!!!>'!!'\"!!!!!>>}},{{{}}}}},{{{<!>},<!!a!>,<!>!!}a\"<'>,{<!''u!!!>},<!!'aa!>i!!o'e!!!a>}},{<!>},<o!>i'!a!!<aa>,<'<e>},{{<'!!!>},<}i{i!!!>!!{!!a,!,>}}},{{}}}}},{{{{<e}!!>,{<e!!!>!!!!!'u{!!\"!!!}o!'aou!!!!!>a>,<!!o}}ai!>,<i>}},{<!>},<!>,<'!!!>u!o{oo{'!!!>>,{}},{{<!!{!\"\"o,}!!!!{!o<!,ii!!<,!!u,<>}}},{{{<<!>,<!>},<!>,<!!}!!!a,<,o!>},<!!a}!>,<!{}>}},{<\"}{!>,<!a<<o!{u}!!!!!!!!\"<!>,<!!<!>>},{{<<!!,\"!>!!<!>,<a!>},<e,!>},<<{!!>},{<<!>,<!!{!!!o!}!>,<!!!>},<!o>,<oo>}}}},{{<{o!!!!!>{!>,<}>},{{}}},{{<{!!!>,<<!>!!!!u!>!!!>'i{,}u>}}},{{{{{<!>},<!!!>e!>},<oui\">}},{<,,!'!>,<o!!!>ua<!>>}},{{{<{!,!>,<!!!>\"!!!!<!>,<<!!<a!!''!>>}},{{}}},{{{<!!u}{!>\"!!!>},<>},{<!!!>u'ea!>eu!u!!!><u\"e,>}}}},{{{{{},<!!!>!u!!!>},<,{oi!>,<'!!!>,<>}},{{{{<!>,<\"u}!!\">,{}}},{<i!i,{{!>,<{!!!>!>!!uuo!>,<'!>,<>,<!!!!!><ioe!<!!ae'!!!>},<,>},{}},{{<<}!!{!{!!>},{<!!!>!ioaau!>!>},<ou,oo>,{<!>{e<,iei!!au!!!><u{!>},<>,<!!!!!>'!\"\"!!!>e{!!!>u>}}}},{<!!o!!!,<!'!!!i>}}}},{{{{<a!!!!!!,iai!!!{!!o!!e<uo!!!><>},<}!!'!>,<<a,!!},!>,<!!!!!>}'!>,<!>u>},{<!!!>!<o!!!>{o!uaa,!!!>{!!!!}<o!!!>!>},<>,{<,\"{ae!>},<!>},<!!!!!>!!!>!!!>}e,>}}},{{{<<\"!>'a'!!uu>},<,!>!>a!>!!>},{<!!!!'!!!>e!!!!!}uu<!!\"!o!{ie!>\"eo>,{}},{{},{{{},{}},{<!>,<'!>e!}!!a<o>}}}},{{<!!!!!>},u!!!>e<{!!u\">},{}},{{<,'i!!!>>,<\"e<,'e{u!!!!o}\"!\"\"uu!!!!!>!>},<'o>},{{{}},<o!>},<!!!>!!!>e,!!!>,<!>},<,!!!>,<a>}}}},{{{},{{}},{{{<>}},{<>}}},{{},{{{<ueao>,<ou!!!>},<!>!>ei\"e<i!!>},{<!!!>,<!>,<u<,!!a}<!>,<e>}}}},{{{{{{{<\"!>\">}}},{{<{!,}!>!!!>,<\"!>},<!!!i!>},<i!!'!!uau!!!>!>},<>,{{}}}},{{{<>,<!>,<!>,<!!!!ioi!!!><{{}!!!!!!'>}},{<!!'!!{o!!i!!i}o!!!>},,>,{}},{{{<>,{{{<e!}{!!o\"\"!>u!!!>\">},<!!'{!!!>\"!!!!!u!!!<,!!!>>}}},{<!!!>!!a!>},<}u!!!><!>,<o!!!>!uo}\"u!{!>!!<>}},{{<a!>,<ui!!<!!!!{o!>}}!>},<\"e,>,{<a<aeu\"a!!\"u!!i!!u!>},<!>,<!>,<'\"<a<>,<>}},{<!>},<}\"e'a!>},<!!'!>,<e!a!!!!o'!{!>},<!>},<'>}},{{{{},<<<o!>},<!>,<!!}!!!}>},{{},{<u!!'}i!!!!>}},{{}}}}}},{{{<\"!!e!>},<i}!>i>,<!>},<!>!>},<>},{<!!oeou!>},<i!au{ai!!!!!>!>!>,<!!!>>,{{{}}}}},{{{{{<e!,,,!!u}i!>,<e,}}o!'>}},{<!!!>>}},{<{},!!iue''o!!!!!>!>},<>}},{{}},{<a!>e'!!,!!!>},<>}}}},{{<>}},{{{<'}{ae>},{<!e!>,<<}!!!>a!>},<!>,<!!!>!!!>a!!o>,{<!>},<<{a,e,,i}o!>,<>}}},{{}},{<ui!!!!ui!>,<{!>},<>,{}}}},{{{{{},<!!!!!>,<a''ao!>},<{!\",>},{<!>!!u!!!!!>a!!!!!>,>,{<!>!>,<!!!!!>i}u!!!>!!!>,<!!uu!>},<!!>}}},{{{{<iai,!>'!>,<,u!!!>},<!!!>!!ao>},{<!!!>{!!!u!!',a!!!!!>u!>,<{>}},{{{}}}}},{{}}},{{{<i!>,<\"!>,<{>},{{<e!!a!!!!!!}!!!>!!i<!!!>{>},{<!!!>!!!>}!!a!{,e!>a!!,!!!>>}},{}}},{{{{{<e!!iue<e!!,>}}},{{<o!>},<,!>,<>,{<i,,\"{a!'}{o>}},<!!!>,!{!!!>},<!!oa!!,oa{!>>}},{{},{{<!!!!e'!>},<{!>,<!!i!}{a!!!><uu,!!!>},<}!!\">,{}},<!!eo!>,<!>,<,o!>,<!>},<,!!!>}\"o<!>,<i!!!!!!!>},<>}},{{{},<oae!!!e!>,<>}}}}}}}},{{{{}}},{{},{<!{'ie'!!!>,<!>,<ou!>,<!!!>},<{ea!u!>a!!!><>,{{<o!!!!!<,i<!>},<i!}!}!!!>,<!>u{!!!>>}}},{<!!!>a!>,<<{e{!>},<}}!>>}},{{<!>,<e>,{<!>},<!a!>a!!!>!!!>o!uio{o>}},{{<!!!>!!o!!iu!<\"}!!o{'!{ooio<>},<e>}}},{}},{{{{<>},{{{},{<!!!>},<i!!!!!>!iaa,}e!>!!o}a!!!>},<>,<e!>},<!>,<!!i,,,\"'uo>}}},{{<>,<>}}},{{{<!ee!!!!!>!>},<'!!!>!>i>}}}},{{}},{{{<}!!!>{!!>},{<'a,!{i!!,!>!!'!>}u>}},{<!!\"!>!>,<\">,{<<<!><o!>},<!>},<e!!<\"!>>}},{}},{{{<!!!!!>!!!!!!<\"<!\"{}!>},<!!!!!!!>e}'!!{!!i>}},{{<!!i!>>,<o\"!{'!>!>'!>,<!!<i!>},<o!>},<!!!>,<!!}a!>,<!!>},{{},{<!!a\">}}},{<!>,<ii!!,>,{<a<o\"!>>}}}},{{{<!>,<o!!!>}>},{{{<u>}},{<!>},<!>},<>}},{{<!!!!!>ao!>},<<!>},<!>,<!>,<!',o!>,<!!{!>,<!>},<!>},<{>}}},{{{<}!>},<<a>},<,!!!>ua!>!>,<o{u!!!i!>>},{{<'o>}},{{<e!>},<!!!>>},<!>!>,<\"<!!!!i}>}},{{{{},{{<!!<o<!>},<>,{<>}},{{}},{}},{{{<>},<eoeuo!>,<>},{{{<o!!,!>,<!>\"o!>,<,!!!>o'!!<oe>},{{{},{}},{}},{{<!>!>},<eo>},<>}},{{<!>!>\",!!!>,<!!<,!>>},<!!!>{!>},<}<uo!!oe!o<o!>,<\"{\"<>},{}},{{},<!!{!!!!!>!>i!>'{ei!!!a!!!><!!{!!!!!>}<>}},{{{{}},{<i!!!>!>},<!>a!>},<\"}!!,u,'{a!>},<{>,<!!!>io!!<!!{o!!!!!,e,>},{{<e>},{}}}}},{{{{{<u!>,<\">,{<<!>uo!>,<>}}}},{<<u}\"!<{{!!}!!a!!\">},{{<}!!!>},<i!>},<!>},<}>},{<!!ui>,{}}}},{{<}\"o!<ae!!'!>},<!>},<'u'!>},<<>},{{{<ai,}io!!!>e!>,<a,>}},{{<!>!><a!!!>o!!!>},<,{,a<u!!}e>}},{{{<'!>}!!'u,!!!i!!u>}},{<!>,<o!>!,>}}},{{{{}}},{{<!!!>,a!!<iae!!!>{<!!ao!!!u>},<!!!>!!<!>i\"!>,<!!!>!!iu!>},<!!\"'\"!}!>,<<,>}}},{{{{{<iu!>},<'aae!o!!!>{aa,!>!!!>,<>},{<u!!!>,io<{u!!!!u>,{}}},{<}!<!!}!!!>!>},<}!>!>!>!!i,!>'!>,<,\">,<{ioo!>},<!!!>{}a'e\"!o>},{}},{}},{{{{}},{{<!!ue<>},{{}}},{{<a!,>,{}}}},{{{}}},{},{{{<o!!!>!>,<!>i!>},<,e!!!u<!>},<>,{<{<!!!>a!!!!'oi<!!!>!!}!>},<!!!>{>}}},{{{{<!!'u,>,<!!\"!!}\"!!{!i>}},{{<!>},<{!!<}<e>,<u!>,<!!!>!!!>!>,<a<!!!a!!u!a\"!!,!!''>},{{{{},{{<!>!!!>!>,<e!!}!><!>,<!!!>,<'!>e!>!!ee,>}},{{{<,o!,,o<!!i!u!>>,{<!!!!ii\"{}!!!>',>}},<!><\"a!!!!{a!>},<e{!!!>}}>},{{{<\"',!a>},<!>},<'!>,<!>,<{i!>},<!}!!!!!!!>!>,<i!>!'!!}<>},<{!o\"o>},{{{{<i!!!!'{>}},{{<!{!}!>e!!!>!!!>i'e,,a}!>u\">},{<!>},<u!>,i!i!!!>!>,<,a!!'\"\",uu>}}},{{},{{<!u!>a\"}>},{{<!<iu!!!!!!u'''>}}}}}}},{},{<'!oue!!!>oa!u!>,<}e!>{o>}},{{},{{{<!!!!!eii!!!>},<>}},{{<}!>},<e<!>!!!>i,!!!>},<!e!!!!!>e!!!!!>\">}}}},{}}},{{{<!!!>\"!,!>,<!>!!!>,!!!!!>!!!!!!!!'oa!!u{a\"!!!>e>},{<{,!!!>!>,<<!!!>,<\"!!!>!>},<!!a\"o}'>}},{{<>},{{<!>},<\"!!!!!>'!>,<!>>},{<!!!>!>,<e{,a!!e}!>,<u<}!!i!>},<!!!>'>}}}}},{{{{<!!!>o<!!!>!!!!e\"!!\",!!!!!!i!!!!!>!'>},{<!!e!>,<'!!!>>}},{}},{{{<!'!!!>!ao!>,<!!ia!>},<!>i!>},<'!>,<>}},{{{<o!!,>},{{}}}},{{{<\"!>,<!<<!>,<'>}}},{{},{<\"!a!>},<ia{!>!!!>{!!!>},<!!}a\"!!'!!'\"u>},{{<a\"!!!>},<'!!!>i!>,<',}}>},{}}}},{{},{{<o!!!>!!!>!i}''eu!>},<!!a,!!!>>},{<{!!!>o'u'oa!!e,>}}},{{<!>,<!}<}\"!!!>u,!>!!!!!!!>!!!!o>,{<{!>},<!>'e!!\"!!e>}},{{},{}}}},{{{<!>,<!!!>\",!>oo{!>},<e'!!!!!>>},{<!>'a!>,<eu!!!>u}!!!!u\">}}}},{{<!o'!i!>,<,!!\",a!>},<>,{<{}oe!!!>}{!>},<,{'!>},<u!!!!!>,<>}},{{<'ao'a!!,!!<i!!!>>},{<i}!>,<'e>}}},{{{<<!!!>,<i!!!!!!!>}>}}}}}},{{{{{{{<{!>,<o\"u}!!!>{o>},{<ue!!{>}},{<!!i!>},<o,!}eo'!!ue!>},<e>}},{{<{{!!i{!>},<!!!>!!!!!>},<e'<'!!{!>},<>,{<a!>,<'!>{!!!>\"!}\"<a}!!!>!!e!!ii!>>}}},{{{{{}},{<!!!'!!!!!>!,!>,<i,,i\"{,e!>,<!>,<,>}}},{{<<ai!!!!!!!>!!!>},<\"'o!>,<o'!!!>,<!!{}>,{}},{{<!!!>!>},<i!>},<i>},{}}},{{<<{>},{<!!!>,!!i!!ea<}!!!!o{}a!>,<,}!>>}}}},{{<\"a!>},<ee,!>,<}!!!!!>'eoi{!!}!>},<>},{<!!i}!>!>},<!u!>!>,<}\",!!'!><!>!<>},{}},{{{<u!>,<ae!i!>,<!!e!!!>iia>},{<!>,<!!!>a{u}ieu!!a}!!!!e!>},<o!>},<!>u!!u!>},<>}},{{<!!!>>,{<'>}}},{<!!u!>},<,!!o!!!>!!a!!!>!!}>}},{{},{{}}}},{}},{{{<e'o!>},<!!iie!>,<,!>,<!>!!e!>},<!!!!!!!>,<>,{<!!!!!>u!>,<e!o!e!>!!!>!e>}}},{<<!>,<i\"!!}!!o!!!>!e{aaoui>,{<,}i'i\"}\"ou>}}},{{{<>},{<!!i,oa!!!>!!!!!>}}}eu>}},{<iaa!!!!i>},{{{},<<u!!!>!!o!>,<',!!!>u!!!>!!'{!>'!!,e>},{}}}}},{{{<}!>,<!>!!!>},<>},{{{}},{{<{,!>u\"!!a\"',<}!!!>u!!!!!>a!><!e>}},{<!>,<!!\"'}!oa}uu}!{e'ei!!oa{!!!!!>>}}},{}},{{{},{{{<!!e\">}},{{{<!!!>!!,\">}},{},{{<a\"oe!!!>\"<!>,<!>,<\">},{<a!>,<i!>!'!!!>u'!>,<,!>,<o,>}}}},{<\"<!!!>iii!>!!!>},<<!>,<!!!>},<!!o!>,<!!!>ui\">,<<i<a!!'\"}<e!!!><!!!!!\"}e,,!a>}},{<euo<!!!>},<u>,{<!!,!!u{!!!>!!!!!!!!}e<>}},{{<'}i!>},<a\"e!!!>ue!!'oa>},<!e!!!>>}}}},{{{<'{!>,<a!ai'!!!!!><u!>,<\"!>},<>},<a}!>},<}eo!!!>ai!>},<e',!>,<a!>!a\"}>},{{<}o!>ao\"!}<<>},{{<>}}}}},{{{{{<!!!>!>},<!ai!!>},{<{>}},{}},{{{},<i},ue!!!!ou!ee!'e'!!'>}}},{{<!>},<ii!!!>!>,<!>},<<}!>},<!,!>},<\"}a>},{<,!!!>}!>},<!!,!>},<!>u<i!!\"!>,<'u!>,<!>,<!!!>>}},{{{{{<\"e!!!!!!u!!!>!!\"!!!>\"!}!oo!!!>,<>},{<!>},<!<!'u!>,<ae>}},{{},{<i!{>}}},{{{<{'!!<}!>,<,i,!>},<!>,<o!!!>,<}!!!!o>},<!!!>!e}a!e!>}!!o>},{<!o!!ae,a!>},<}}!>},<}{!!>},{<ea!>io\"'{!<!>,<>,<o,\"a'!>,<i!!!>}}!!!>,<!!<!>,<!}{a'>}},{{{{},{<{!!!!!>!!u'!!!!!!!<}e\"a}>}},{<!>},<!!!>io!!!!!>},<,e,}!>!>!!e}!>},<ui<>,{{<i}o!!!>!!!!,{!!ee!>,<,!!>}}},{<!>},<,!>},<>,{{<{!>,<{\"a}!!!>!!!>!!a!!ai!u>,{<ie\"!\"i>}}}}},{{{{<!\"!!!>!}!!a!!>,<!>u!!!>!!,o!!,<!!<!>,<\"!>,<e!!!>,{}!>},<!\">}},{{}}},{{{<!>},<a\"!!oo!!!!i>,{<uoo!>},<!!!>!!e,e!!u!u,i!!i,>,{}}},{{{<!>,<'i!>},<{e!{!!i>}},{<uiua{!!<e>}},{}},{<io!>e>,{{<i,!}!e>},<,!>{!!!>!a!}>}},{{},{{<a!>,<a!>!>,<oi!!!!!>,<!>!\"!!a!!!>!<!<>}}}},{{<\"ioe!>,<!!a<!o\"!>!!i{!!!>,<!>!!o>}},{{{},{}},{<>,{<'!!'!>!i<!>>}},{{<!>,<!!!>,a<o<{!,}!a!!!>,!!!>>}}}}}},{{<\"'\",<'>},{{<u},!!!>\",!>},<e{e!!!>!!!>!>,<!!'o}i>,<!>,<{{}i\"a{!!!!!!!>,<}o,>},<!ii>},{{<{!>,<!!o{>}}},{},{{<'uo!>ui{!!!>!>,<!'!!'!\"eo!<!!{!i>,{<!!,{{i!!!>,<!o>}},{{},{<o>}}}},{{{{{{},{}},{}},{{<e!>e!!o!!!>ue\"{!!!>!!!>!>>}}},{{<>,{<i\",a!\"\">}},{{<!i!>,<!!!\"e!!u!!!!!!!>o}e\"}>},{}}}},{{{<},!>,<!!!>uu!!'}!!!>o!!}<{!!!>,<u!}>,{{{},{{},{<<a!<!>},<u!!i{<!>},<>}}}}},{{<!>!>o!!!>!>,<<,!\"!!}\">}},{{<!!!!'o>},{}}},{<ao!!!>!!!>!!!>!>},<!\"aao\"{!>},<!>>},{{<'oa{!u}!o!!e!>},<!!'>}}}}},{{{{{<!>},<o}o!!!>,<'>}},{}},{}},{{{},<!>,<i!!e!>},<<<!>,<e{aa!iouie>},{<!!!>,>,<{!!!>,<'!!!>},<\"!>\"}!>},<!!!>,<!>!!!!!e!!!!,!>,<>},{{<!!!>,<>}}},{{<!>!>},<<<!!e!!!!!>>},{{<}!>},<'>}}}}}}"



