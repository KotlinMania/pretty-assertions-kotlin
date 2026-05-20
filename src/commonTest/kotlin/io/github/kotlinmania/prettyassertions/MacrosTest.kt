// port-lint: source tests/macros.rs
package io.github.kotlinmania.prettyassertions

import kotlin.test.Test
import kotlin.test.assertFailsWith

class MacrosTest {
    @Test
    fun assertStrEqPassesStrings() {
        val value = "some value"
        assertStrEq(value, value)
    }

    @Test
    fun assertStrEqFailsWithStringDiff() {
        assertFailsWith<AssertionError> {
            assertStrEq("foo\nbar", "foo\nbaz")
        }
    }

    @Test
    fun assertEqPassesEqualValues() {
        val value = "some value"
        assertEq(value, value)
    }

    @Test
    fun assertEqFailsWithDiff() {
        assertFailsWith<AssertionError> {
            assertEq(666, 999)
        }
    }

    @Test
    fun assertEqFailsWithCustomMessage() {
        assertFailsWith<AssertionError> {
            assertEq(666, 999, "custom panic message")
        }
    }

    @Test
    fun assertNePassesDifferentValues() {
        assertNe("a", "b")
    }

    @Test
    fun assertNeFailsWithSharedValue() {
        assertFailsWith<AssertionError> {
            assertNe(666, 666)
        }
    }

    @Test
    fun assertNeReturnsAfterPassing() {
        fun notZero(value: UInt): UInt {
            assertNe(value, 0u)
            return value
        }
        notZero(1u)
    }
}
