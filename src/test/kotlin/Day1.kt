import org.amshove.kluent.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/*
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
*/


fun <E> List<E>.shiftLeft() = if (this.isEmpty()) this else this.drop(1) + this.first()

fun CharSequence.splitToDigits() = if (this.isEmpty()) listOf()
                                    else this.map { Character.getNumericValue(it) }

fun captcha(list: List<Int>) = (list zip list.shiftLeft())
                                    .filter { it.first == it.second}
                                    .map {it.first}
                                    .sum()
fun captcha(string: String) = captcha(string.splitToDigits())

class Day1Spec : Spek({
    describe("shiftLeft elements in a list") {
        on("shiftLeft empty list") {
            val list = listOf<Int>()

            it("should stay empty") {
                list.shiftLeft() `should equal` list
            }
        }
        on("shiftLeft of list with one element") {
            val list = listOf(1)

            it("should be the same list because of round shiftLeft") {
                list.shiftLeft() `should equal` list
            }
        }
        on("shiftLeft of list with 1, 2, 3") {
            val list = listOf(1, 2, 3)

            it("should be 2, 3, 1") {
                list.shiftLeft() `should equal` listOf(2, 3, 1)
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

    describe("my captcha") {
        println(captcha(
                "7385764686251444473997915123782972536343732657517834671759462795461213782428342931896181695578996274321317419242359534783957372932953774336338118488967172727651862498838195317654289797558683458511126996217953322817229372373455862177844478443391835484591525235651863464891177927244954925827786799436536592561374269299474738321293575385899438446558569241236278779779983587912431395475244796538888373287186921647426866237756737342731976763959499149996315591584716122199183295277439872911371313924594486766479438544417416529743495114819825984524437367225234184772617942525954961136976875325182725754768372684531972614455134523596338355374444273522115362238734383164778129376628621497662965456761631796178353599629887665939521892447361219479646483978798392716119793282717739524897385958273726776318154977675546287789874265339688753977185129334929715486381875286278528247696464162297691698154712775589541945263574897266575996455547625537947927972497979333932115165151462742216327321116291372396585618664475715321298122335789262942284571328414569375464386446824882551918843185195829547373915482687534432942778312542752798313434628498295216692646713137244198123219531693559848915834623825919191532658735422176965451741869666714874158492556445954852299161868651448123825821775363219246244515946392686275545561989355573946924767442253465342753995764791927951158771231944177692469531494559697911176613943396258141822244578457498361352381518166587583342233816989329544415621127397996723997397219676486966684729653763525768655324443991129862129181215339947555257279592921258246646215764736698583211625887436176149251356452358211458343439374688341116529726972434697324734525114192229641464227986582845477741747787673588848439713619326889624326944553386782821633538775371915973899959295232927996742218926514374168947582441892731462993481877277714436887597223871881149693228928442427611664655772333471893735932419937832937953495929514837663883938416644387342825836673733778119481514427512453357628396666791547531814844176342696362416842993761919369994779897357348334197721735231299249116477"
        ))
    }


})


