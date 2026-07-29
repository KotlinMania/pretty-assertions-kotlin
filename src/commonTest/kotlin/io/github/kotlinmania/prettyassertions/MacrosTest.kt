// port-lint: source tests/macros.rs
package io.github.kotlinmania.prettyassertions

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val RED_LIGHT = "\u001B[31m"
private const val GREEN_LIGHT = "\u001B[32m"
private const val RED_HEAVY = "\u001B[1;48;5;52;31m"
private const val GREEN_HEAVY = "\u001B[1;48;5;22;32m"
private const val RESET = "\u001B[0m"

class MacrosTest {
    private fun assertFailureMessageContains(expectedSubstring: String, block: () -> Unit) {
        val error = assertFailsWith<AssertionError> { block() }
        assertTrue(
            error.message!!.contains(expectedSubstring),
            "Expected assertion message to contain:\n$expectedSubstring\nbut was:\n${error.message}",
        )
    }

    // --------------------------------------------------------------------
    // assert_str_eq
    // --------------------------------------------------------------------

    @Test
    fun assertStrEqPassesStr() {
        val value = "some value"
        assertStrEq(value, value)
    }

    @Test
    fun assertStrEqPassesString() {
        val value = "some value"
        assertStrEq(value, value)
    }

    @Test
    fun assertStrEqPassesComparableTypes() {
        val s0: String = "foo"
        val s1: String = "foo"
        assertStrEq(s0, s1)
    }

    @Test
    fun assertStrEqFailsFoo() {
        val expected =
            "assertion failed: `(left == right)`\n\n" +
                "\u001B[1mDiff$RESET $RED_LIGHT< left$RESET / ${GREEN_LIGHT}right >$RESET :\n" +
                " foo\n" +
                "$RED_LIGHT<ba$RESET${RED_HEAVY}r$RESET\n" +
                "${GREEN_LIGHT}>ba$RESET${GREEN_HEAVY}z$RESET\n"
        assertFailureMessageContains(expected) {
            assertStrEq("foo\nbar", "foo\nbaz")
        }
    }

    // A string wrapper analogous to the upstream MyString(String) that
    // implements AsRef<str>. In Kotlin, CharSequence plays the role of
    // AsRef<str> — any value that can be viewed as a character sequence.
    private class MyString(private val value: String) : CharSequence {
        override val length: Int get() = value.length
        override fun get(index: Int): Char = value[index]
        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
            value.subSequence(startIndex, endIndex)
        override fun toString(): String = value
        override fun equals(other: Any?): Boolean = when (other) {
            is MyString -> value == other.value
            is String -> value == other
            else -> false
        }
        override fun hashCode(): Int = value.hashCode()
    }

    @Test
    fun assertStrEqPassesAsRefTypes() {
        val s0 = MyString("foo")
        val s1 = "foo"
        assertStrEq(s0, s1)
    }

    @Test
    fun assertStrEqFailsAsRefTypes() {
        val expected =
            "assertion failed: `(left == right)`\n\n" +
                "\u001B[1mDiff$RESET $RED_LIGHT< left$RESET / ${GREEN_LIGHT}right >$RESET :\n" +
                " foo\n" +
                "$RED_LIGHT<ba$RESET${RED_HEAVY}r$RESET\n" +
                "${GREEN_LIGHT}>ba$RESET${GREEN_HEAVY}z$RESET\n"
        assertFailureMessageContains(expected) {
            assertStrEq(MyString("foo\nbar"), "foo\nbaz")
        }
    }

    // --------------------------------------------------------------------
    // assert_eq
    // --------------------------------------------------------------------

    @Test
    fun assertEqPasses() {
        val value = "some value"
        assertEq(value, value)
    }

    @Test
    fun assertEqPassesUnsized() {
        val a = byteArrayOf(101)
        assertEq(a, a.copyOf())
    }

    @Test
    fun assertEqPassesComparableTypes() {
        val s0: String = "foo"
        val s1: String = "foo"
        assertEq(s0, s1)
    }

    @Test
    fun assertEqFails() {
        val expected =
            "assertion failed: `(left == right)`\n\n" +
                "\u001B[1mDiff$RESET $RED_LIGHT< left$RESET / ${GREEN_LIGHT}right >$RESET :\n" +
                "$RED_LIGHT<$RESET${RED_HEAVY}666$RESET\n" +
                "${GREEN_LIGHT}>$RESET${GREEN_HEAVY}999$RESET\n"
        assertFailureMessageContains(expected) {
            assertEq(666, 999)
        }
    }

    @Test
    fun assertEqFailsTrailingComma() {
        val expected =
            "assertion failed: `(left == right)`\n\n" +
                "\u001B[1mDiff$RESET $RED_LIGHT< left$RESET / ${GREEN_LIGHT}right >$RESET :\n" +
                "$RED_LIGHT<$RESET${RED_HEAVY}666$RESET\n" +
                "${GREEN_LIGHT}>$RESET${GREEN_HEAVY}999$RESET\n"
        assertFailureMessageContains(expected) {
            assertEq(666, 999)
        }
    }

    @Test
    fun assertEqFailsUnsized() {
        val a = byteArrayOf(101)
        val b = byteArrayOf(101, 101)
        val expected =
            "assertion failed: `(left == right)`\n\n" +
                "\u001B[1mDiff$RESET $RED_LIGHT< left$RESET / ${GREEN_LIGHT}right >$RESET :\n" +
                " [\n" +
                "     101,\n" +
                "${GREEN_LIGHT}>    101,$RESET\n" +
                " ]\n"
        assertFailureMessageContains(expected) {
            assertEq(a, b)
        }
    }

    @Test
    fun assertEqFailsCustom() {
        val expected =
            "assertion failed: `(left == right)`: custom panic message\n\n" +
                "\u001B[1mDiff$RESET $RED_LIGHT< left$RESET / ${GREEN_LIGHT}right >$RESET :\n" +
                "$RED_LIGHT<$RESET${RED_HEAVY}666$RESET\n" +
                "${GREEN_LIGHT}>$RESET${GREEN_HEAVY}999$RESET\n"
        assertFailureMessageContains(expected) {
            assertEq(666, 999, "custom panic message")
        }
    }

    @Test
    fun assertEqFailsCustomTrailingComma() {
        val expected =
            "assertion failed: `(left == right)`: custom panic message\n\n" +
                "\u001B[1mDiff$RESET $RED_LIGHT< left$RESET / ${GREEN_LIGHT}right >$RESET :\n" +
                "$RED_LIGHT<$RESET${RED_HEAVY}666$RESET\n" +
                "${GREEN_LIGHT}>$RESET${GREEN_HEAVY}999$RESET\n"
        assertFailureMessageContains(expected) {
            assertEq(666, 999, "custom panic message")
        }
    }

    @Test
    fun assertEqFailsStr() {
        val expected =
            "assertion failed: `(left == right)`\n\n" +
                "\u001B[1mDiff$RESET $RED_LIGHT< left$RESET / ${GREEN_LIGHT}right >$RESET :\n" +
                " foo\n" +
                "$RED_LIGHT<ba$RESET${RED_HEAVY}r$RESET\n" +
                "${GREEN_LIGHT}>ba$RESET${GREEN_HEAVY}z$RESET\n"
        assertFailureMessageContains(expected) {
            assertEq("foo\nbar", "foo\nbaz")
        }
    }

    @Test
    fun assertEqFailsString() {
        val expected =
            "assertion failed: `(left == right)`\n\n" +
                "\u001B[1mDiff$RESET $RED_LIGHT< left$RESET / ${GREEN_LIGHT}right >$RESET :\n" +
                " foo\n" +
                "$RED_LIGHT<ba$RESET${RED_HEAVY}r$RESET\n" +
                "${GREEN_LIGHT}>ba$RESET${GREEN_HEAVY}z$RESET\n"
        assertFailureMessageContains(expected) {
            assertEq("foo\nbar", "foo\nbaz")
        }
    }

    // --------------------------------------------------------------------
    // assert_ne
    // --------------------------------------------------------------------

    @Test
    fun assertNePasses() {
        assertNe("a", "b")
    }

    @Test
    fun assertNePassesUnsized() {
        val a = byteArrayOf(101)
        val b = byteArrayOf(101, 101)
        assertNe(a, b)
    }

    @Test
    fun assertNePassesComparableTypes() {
        val s0: String = "foo"
        val s1: String = "bar"
        assertNe(s0, s1)
    }

    @Test
    fun assertNeFails() {
        val expected =
            "assertion failed: `(left != right)`\n\n" +
                "Both sides:\n" +
                "666\n"
        assertFailureMessageContains(expected) {
            assertNe(666, 666)
        }
    }

    @Test
    fun assertNeFailsTrailingComma() {
        val expected =
            "assertion failed: `(left != right)`\n\n" +
                "Both sides:\n" +
                "666\n"
        assertFailureMessageContains(expected) {
            assertNe(666, 666)
        }
    }

    @Test
    fun assertNeFailsUnsized() {
        val a = byteArrayOf(101)
        val expected =
            "assertion failed: `(left != right)`\n\n" +
                "Both sides:\n" +
                "[\n" +
                "    101,\n" +
                "]\n"
        assertFailureMessageContains(expected) {
            assertNe(a, a.copyOf())
        }
    }

    @Test
    fun assertNeFailsCustom() {
        val expected =
            "assertion failed: `(left != right)`: custom panic message\n\n" +
                "Both sides:\n" +
                "666\n"
        assertFailureMessageContains(expected) {
            assertNe(666, 666, "custom panic message")
        }
    }

    @Test
    fun assertNeFailsCustomTrailingComma() {
        val expected =
            "assertion failed: `(left != right)`: custom panic message\n\n" +
                "Both sides:\n" +
                "666\n"
        assertFailureMessageContains(expected) {
            assertNe(666, 666, "custom panic message")
        }
    }

    @Test
    fun assertNeNonEmptyReturn() {
        // Regression test: assert_ne returns a non-empty value when the
        // assertion passes. In Rust this is `#[should_panic]` because
        // calling not_zero(0) panics. Here we verify that calling with 0
        // does throw, and with a non-zero value it returns normally.
        fun notZero(value: UInt): UInt {
            assertNe(value, 0u)
            return value
        }

        // Non-zero passes and returns the value
        assertEquals(1u, notZero(1u))

        // Zero fails
        assertFailsWith<AssertionError> { notZero(0u) }
    }

    // --------------------------------------------------------------------
    // assert_matches — not portable
    // --------------------------------------------------------------------
    // The upstream assert_matches! macro (gated behind the "unstable"
    // feature) uses Rust's pattern matching syntax (pat, if guard) which
    // has no Kotlin equivalent. Kotlin's `when` expression does not support
    // binding sub-patterns like `Some(value) if value > 2` in a macro
    // context, and there is no Kotlin macro system to stringify a pattern
    // for the diff output. The specific Rust semantics that don't
    // translate: pattern macros, stringify!(pat), and ref bindings in
    // patterns. The six upstream tests (passes, passes_unsized, fails,
    // fails_guard, fails_unsized, fails_custom, fails_custom_trailing_comma)
    // are therefore not ported.
}