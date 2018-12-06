import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {
    val linePattern = Regex("""\[(.+)] (.+)""")
    val datePattern = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")

    val sortedParsedLines = File("input.txt").readLines().mapNotNull { line ->
        linePattern.matchEntire(line)?.let { matchResult ->
            if (matchResult.groupValues.size < 3) {
                null
            } else {
                val time = LocalDateTime.parse(matchResult.groupValues[1], datePattern)
                time to matchResult.groupValues[2]
            }
        }
    }.sortedBy { it.first }

    val guardsData = mutableMapOf<String, MutableList<IntRange>>().apply {
        var guardId = ""
        var fallAsleepMinute = 0
        sortedParsedLines.forEach { (time, message) ->
            when (message) {
                "falls asleep" -> fallAsleepMinute = time.minute
                "wakes up" ->
                    getOrPut(guardId, ::mutableListOf)
                        .add(fallAsleepMinute until time.minute)
                else -> guardId = message.drop(7).takeWhile { it != ' ' }
            }
        }
    }

    val (sleepyGuardId, sleepTime) = guardsData
        .mapValues { entry -> entry.value.sumBy { it.count() } }
        .maxBy { it.value }
        ?.let { it.key to it.value }
        ?: throw Exception("up")

    println("Guard #$sleepyGuardId is a master sleeper with $sleepTime minutes slept.")

    val guardsSleepyMinute = guardsData.mapValues { entry ->
        (0..59)
            .map { minute -> minute to entry.value.count { it.contains(minute) } }
            .maxBy { it.second }
            ?: throw Exception("up")
    }

    val (sleepyMinute, times) = guardsSleepyMinute[sleepyGuardId] ?: throw Exception("up")

    println("He's been $times times asleep at minute $sleepyMinute")

    guardsSleepyMinute.maxBy { it.value.second }?.also { entry ->
        println("Guard #${entry.key} was asleep most often (${entry.value.second} times) at minute ${entry.value.first}")
    }
}
