// port-lint: source lib.rs
package io.github.kotlinmania.prettyassertions

/**
 * Pretty Assertions.
 *
 * When writing tests in Kotlin, you probably use equality assertions often.
 *
 * If such a test fails, it will present all the details of `a` and `b`. But
 * you have to spot the differences yourself, which is not always
 * straightforward. This package provides drop-in Kotlin-facing assertion
 * helpers that add a colorful diff.
 */

/**
 * A comparison of two values.
 *
 * Where both values have a useful string representation, the comparison can be
 * displayed as a pretty diff. The values may have different types, although in
 * practice they are usually the same.
 */
class Comparison<TLeft, TRight> private constructor(
    private val left: TLeft,
    private val right: TRight,
) {
    /**
     * Render the two values as strings and diff that output.
     */
    fun fmt(): String =
        buildString {
            append(writeHeader())
            append(writeLines(prettyDebug(left), prettyDebug(right)))
        }

    override fun toString(): String = fmt()

    companion object {
        /**
         * Store two values to be compared in future.
         *
         * Expensive diffing is deferred until converting the comparison to text.
         */
        fun <TLeft, TRight> new(left: TLeft, right: TRight): Comparison<TLeft, TRight> =
            Comparison(left, right)
    }
}

/**
 * A comparison of two strings.
 *
 * In contrast to [Comparison], which uses each value's diagnostic
 * representation, [StrComparison] uses the string values directly, resulting in
 * multi-line output for multi-line strings.
 */
class StrComparison<TLeft, TRight> private constructor(
    private val left: TLeft,
    private val right: TRight,
) {
    /**
     * Render the two string values as a pretty diff.
     */
    fun fmt(): String =
        buildString {
            append(writeHeader())
            append(writeLines(left.toString(), right.toString()))
        }

    override fun toString(): String = fmt()

    companion object {
        /**
         * Store two values to be compared in future.
         *
         * Expensive diffing is deferred until converting the comparison to text.
         */
        fun <TLeft, TRight> new(left: TLeft, right: TRight): StrComparison<TLeft, TRight> =
            StrComparison(left, right)
    }
}

/**
 * Asserts that two expressions are equal to each other.
 *
 * On failure, this function prints a diff derived from each value's diagnostic
 * representation, or from the direct string values when both sides are strings.
 */
fun <TLeft, TRight> assertEq(left: TLeft, right: TRight, message: String? = null) {
    if (left != right) {
        val comparison = createComparison(left, right)
        failAssertion(
            "assertion failed: `(left == right)`${customMessage(message)}\n\n$comparison\n",
        )
    }
}

/**
 * Asserts that two string expressions are equal to each other.
 *
 * On failure, this function prints a diff derived from each value's string
 * representation. See [StrComparison] for further details.
 */
fun <TLeft, TRight> assertStrEq(left: TLeft, right: TRight, message: String? = null) {
    if (left != right) {
        failAssertion(
            "assertion failed: `(left == right)`${customMessage(message)}\n\n" +
                "${StrComparison.new(left, right)}\n",
        )
    }
}

/**
 * Asserts that two expressions are not equal to each other.
 *
 * On failure, this function prints the shared value using its diagnostic
 * representation.
 */
fun <TLeft, TRight> assertNe(left: TLeft, right: TRight, message: String? = null) {
    if (left == right) {
        failAssertion(
            "assertion failed: `(left != right)`${customMessage(message)}\n\n" +
                "Both sides:\n${prettyDebug(left)}\n",
        )
    }
}

internal class PrettyAssertionFailure(message: String) : AssertionError(message)

private fun failAssertion(message: String): Nothing {
    throw PrettyAssertionFailure(message)
}

internal interface CompareAsStrByDefault

internal interface CreateComparison<out TComparison> {
    fun createComparison(): TComparison
}

private data class DebugComparisonFactory<TLeft, TRight>(
    private val left: TLeft,
    private val right: TRight,
) : CreateComparison<Comparison<TLeft, TRight>> {
    override fun createComparison(): Comparison<TLeft, TRight> =
        Comparison.new(left, right)
}

private data class StringComparisonFactory<TLeft : CharSequence, TRight : CharSequence>(
    private val left: TLeft,
    private val right: TRight,
) : CreateComparison<StrComparison<TLeft, TRight>>, CompareAsStrByDefault {
    override fun createComparison(): StrComparison<TLeft, TRight> =
        StrComparison.new(left, right)
}

private fun <TLeft, TRight> createComparison(left: TLeft, right: TRight): Any =
    if (left is CharSequence && right is CharSequence) {
        StringComparisonFactory(left, right).createComparison()
    } else {
        DebugComparisonFactory(left, right).createComparison()
    }

private fun customMessage(message: String?): String =
    if (message == null) "" else ": $message"

private fun prettyDebug(value: Any?): String =
    when (value) {
        null -> "null"
        is ByteArray -> value.joinToString(prefix = "[\n", postfix = "\n]") { "    $it," }
        is ShortArray -> value.joinToString(prefix = "[\n", postfix = "\n]") { "    $it," }
        is IntArray -> value.joinToString(prefix = "[\n", postfix = "\n]") { "    $it," }
        is LongArray -> value.joinToString(prefix = "[\n", postfix = "\n]") { "    $it," }
        is UByteArray -> value.joinToString(prefix = "[\n", postfix = "\n]") { "    $it," }
        is UShortArray -> value.joinToString(prefix = "[\n", postfix = "\n]") { "    $it," }
        is UIntArray -> value.joinToString(prefix = "[\n", postfix = "\n]") { "    $it," }
        is ULongArray -> value.joinToString(prefix = "[\n", postfix = "\n]") { "    $it," }
        is Array<*> -> value.joinToString(prefix = "[\n", postfix = "\n]") { "    ${prettyDebug(it)}," }
        is Iterable<*> -> value.joinToString(prefix = "[\n", postfix = "\n]") { "    ${prettyDebug(it)}," }
        else -> value.toString()
    }
