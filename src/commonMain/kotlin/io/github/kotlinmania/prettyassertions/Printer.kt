// port-lint: source printer.rs
package io.github.kotlinmania.prettyassertions

private const val SIGN_RIGHT = '>'
private const val SIGN_LEFT = '<'

private const val RESET = "\u001B[0m"
private const val RED_LIGHT = "\u001B[31m"
private const val GREEN_LIGHT = "\u001B[32m"
private const val RED_HEAVY = "\u001B[1;48;5;52;31m"
private const val GREEN_HEAVY = "\u001B[1;48;5;22;32m"

internal fun writeHeader(): String =
    "\u001B[1mDiff$RESET $RED_LIGHT$SIGN_LEFT left$RESET / ${GREEN_LIGHT}right $SIGN_RIGHT$RESET :\n"

private class LatentDeletion {
    private var value: String? = null
    private var count: Int = 0

    fun set(value: String) {
        this.value = value
        count += 1
    }

    fun take(): String? =
        if (count == 1) {
            val current = value
            value = null
            current
        } else {
            null
        }

    fun flush(out: StringBuilder) {
        val current = value
        if (current != null) {
            out.appendStyled(RED_LIGHT, "$SIGN_LEFT$current")
            out.append('\n')
            value = null
        } else {
            count = 0
        }
    }
}

internal fun writeLines(left: String, right: String): String {
    val out = StringBuilder()
    val changes = diffLines(left, right)
    val previousDeletion = LatentDeletion()
    var index = 0

    while (index < changes.size) {
        when (val change = changes[index]) {
            is DiffChange.Both -> {
                previousDeletion.flush(out)
                out.append(' ').append(change.left).append('\n')
            }
            is DiffChange.Left -> {
                previousDeletion.flush(out)
                previousDeletion.set(change.value)
            }
            is DiffChange.Right -> {
                val next = changes.getOrNull(index + 1)
                if (next is DiffChange.Right) {
                    previousDeletion.flush(out)
                    out.appendStyled(GREEN_LIGHT, "$SIGN_RIGHT${change.value}")
                    out.append('\n')
                } else {
                    val deleted = previousDeletion.take()
                    if (deleted != null) {
                        out.append(writeInlineDiff(deleted, change.value))
                    } else {
                        previousDeletion.flush(out)
                        out.appendStyled(GREEN_LIGHT, "$SIGN_RIGHT${change.value}")
                        out.append('\n')
                    }
                }
            }
        }
        index += 1
    }

    previousDeletion.flush(out)
    return out.toString()
}

internal fun writeInlineDiff(left: String, right: String): String {
    val diff = diffChars(left, right)
    val out = StringBuilder()

    val leftWriter = InlineWriter.new(out)
    leftWriter.writeWithStyle(SIGN_LEFT, RED_LIGHT)
    for (change in diff) {
        when (change) {
            is DiffChange.Both -> leftWriter.writeWithStyle(change.left, RED_LIGHT)
            is DiffChange.Left -> leftWriter.writeWithStyle(change.value, RED_HEAVY)
            is DiffChange.Right -> Unit
        }
    }
    leftWriter.finish()

    val rightWriter = InlineWriter.new(out)
    rightWriter.writeWithStyle(SIGN_RIGHT, GREEN_LIGHT)
    for (change in diff) {
        when (change) {
            is DiffChange.Both -> rightWriter.writeWithStyle(change.right, GREEN_LIGHT)
            is DiffChange.Left -> Unit
            is DiffChange.Right -> rightWriter.writeWithStyle(change.value, GREEN_HEAVY)
        }
    }
    rightWriter.finish()

    return out.toString()
}

private class InlineWriter(private val out: StringBuilder) {
    private var style: String = ""

    fun writeWithStyle(value: Char, newStyle: String) {
        writeWithStyle(value.toString(), newStyle)
    }

    fun writeWithStyle(value: String, newStyle: String) {
        if (newStyle == style) {
            out.append(value)
        } else {
            if (style.isNotEmpty()) {
                out.append(RESET)
            }
            out.append(newStyle).append(value)
            style = newStyle
        }
    }

    fun finish() {
        if (style.isNotEmpty()) {
            out.append(RESET)
        }
        out.append('\n')
        style = ""
    }

    companion object {
        fun new(out: StringBuilder): InlineWriter = InlineWriter(out)
    }
}

private sealed class DiffChange {
    data class Both(val left: String, val right: String) : DiffChange()
    data class Left(val value: String) : DiffChange()
    data class Right(val value: String) : DiffChange()
}

private fun diffLines(left: String, right: String): List<DiffChange> =
    reorderTrailingDeletion(diff(splitLines(left), splitLines(right)))

private fun diffChars(left: String, right: String): List<DiffChange> =
    diff(left.map { it.toString() }, right.map { it.toString() })

private fun reorderTrailingDeletion(changes: List<DiffChange>): List<DiffChange> {
    if (changes.size < 3) {
        return changes
    }
    val beforeTrailing = changes[changes.lastIndex - 2]
    val trailingDeletion = changes[changes.lastIndex - 1]
    val finalInsertion = changes[changes.lastIndex]
    return if (
        beforeTrailing is DiffChange.Left &&
        trailingDeletion is DiffChange.Left &&
        trailingDeletion.value.isEmpty() &&
        finalInsertion is DiffChange.Right
    ) {
        changes.dropLast(3) + beforeTrailing + finalInsertion + trailingDeletion
    } else {
        changes
    }
}

private fun splitLines(value: String): List<String> {
    if (value.isEmpty()) {
        return emptyList()
    }
    val lines = mutableListOf<String>()
    var start = 0
    for (index in value.indices) {
        if (value[index] == '\n') {
            lines += value.substring(start, index)
            start = index + 1
        }
    }
    lines += value.substring(start)
    return lines
}

private fun diff(left: List<String>, right: List<String>): List<DiffChange> {
    val lengths = Array(left.size + 1) { IntArray(right.size + 1) }
    for (leftIndex in left.indices.reversed()) {
        for (rightIndex in right.indices.reversed()) {
            lengths[leftIndex][rightIndex] =
                if (left[leftIndex] == right[rightIndex]) {
                    lengths[leftIndex + 1][rightIndex + 1] + 1
                } else {
                    maxOf(lengths[leftIndex + 1][rightIndex], lengths[leftIndex][rightIndex + 1])
                }
        }
    }

    val changes = mutableListOf<DiffChange>()
    var leftIndex = 0
    var rightIndex = 0
    while (leftIndex < left.size && rightIndex < right.size) {
        if (left[leftIndex] == right[rightIndex]) {
            changes += DiffChange.Both(left[leftIndex], right[rightIndex])
            leftIndex += 1
            rightIndex += 1
        } else if (lengths[leftIndex + 1][rightIndex] >= lengths[leftIndex][rightIndex + 1]) {
            changes += DiffChange.Left(left[leftIndex])
            leftIndex += 1
        } else {
            changes += DiffChange.Right(right[rightIndex])
            rightIndex += 1
        }
    }
    while (leftIndex < left.size) {
        changes += DiffChange.Left(left[leftIndex])
        leftIndex += 1
    }
    while (rightIndex < right.size) {
        changes += DiffChange.Right(right[rightIndex])
        rightIndex += 1
    }
    return changes
}

private fun StringBuilder.appendStyled(style: String, value: String) {
    append(style).append(value).append(RESET)
}
