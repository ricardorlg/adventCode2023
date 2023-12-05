import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String,day:String) = Path("src/data/$day/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun String.safeSubstring(startIndex: Int, endIndex: Int): String {
    if (startIndex < 0 || endIndex > this.length) return ""
    return this.substring(startIndex, endIndex)
}

fun <A> List<String>.mapUntilEmpty(transform: (List<String>) -> A): List<A> {
    return buildList {
        var current = ArrayList<String>()
        for (line in this@mapUntilEmpty) {
            if (line.isEmpty()) {
                add(transform(current))
                current = ArrayList()
                continue
            } else {
                current.add(line)
            }
        }
        if (current.isNotEmpty()) {
            add(transform(current))
        }
    }
}
