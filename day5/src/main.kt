import java.io.File

fun main(args: Array<String>) {
    val input = File("input.txt").readText()
    val reactedInput = reactPolymer(input)
    println(reactedInput.length)

    reactedInput.toLowerCase().toSet()
        .map { c ->
            reactedInput
                .filter { it.toLowerCase() != c }
                .let(::reactPolymer)
                .let(String::length)
        }
        .min()
        ?.also(::println)
}

fun reactPolymer(input: String): String = input.fold("") { acc, c ->
    val last = acc.lastOrNull()
    if (last == null || last == c || last.toLowerCase() != c.toLowerCase()) {
        acc + c
    } else {
        acc.dropLast(1)
    }
}
