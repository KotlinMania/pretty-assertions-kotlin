// port-lint: source printer.rs
package io.github.kotlinmania.prettyassertions

import kotlin.test.Test
import kotlin.test.assertEquals

private const val RED_LIGHT = "\u001B[31m"
private const val GREEN_LIGHT = "\u001B[32m"
private const val RED_HEAVY = "\u001B[1;48;5;52;31m"
private const val GREEN_HEAVY = "\u001B[1;48;5;22;32m"
private const val RESET = "\u001B[0m"

class PrinterTest {
    private fun checkPrinter(
        printer: (String, String) -> String,
        left: String,
        right: String,
        expected: String,
    ) {
        assertEquals(expected, printer(left, right))
    }

    @Test
    fun writeInlineDiffEmpty() {
        val expected = "${RED_LIGHT}<$RESET\n${GREEN_LIGHT}>$RESET\n"
        checkPrinter(::writeInlineDiff, "", "", expected)
    }

    @Test
    fun writeInlineDiffAdded() {
        val expected = "${RED_LIGHT}<$RESET\n${GREEN_LIGHT}>$RESET${GREEN_HEAVY}polymerase$RESET\n"
        checkPrinter(::writeInlineDiff, "", "polymerase", expected)
    }

    @Test
    fun writeInlineDiffRemoved() {
        val expected = "${RED_LIGHT}<$RESET${RED_HEAVY}polyacrylamide$RESET\n${GREEN_LIGHT}>$RESET\n"
        checkPrinter(::writeInlineDiff, "polyacrylamide", "", expected)
    }

    @Test
    fun writeInlineDiffChanged() {
        val expected =
            "${RED_LIGHT}<poly$RESET${RED_HEAVY}me$RESET${RED_LIGHT}ra$RESET" +
                "${RED_HEAVY}s$RESET${RED_LIGHT}e$RESET\n" +
                "${GREEN_LIGHT}>poly$RESET${GREEN_HEAVY}ac$RESET${GREEN_LIGHT}r$RESET" +
                "${GREEN_HEAVY}yl$RESET${GREEN_LIGHT}a$RESET${GREEN_HEAVY}mid$RESET" +
                "${GREEN_LIGHT}e$RESET\n"
        checkPrinter(::writeInlineDiff, "polymerase", "polyacrylamide", expected)
    }

    @Test
    fun writeLinesEmptyString() {
        val expected = "${GREEN_LIGHT}>content$RESET\n"
        checkPrinter(::writeLines, "", "content", expected)
    }

    @Test
    fun writeLinesStruct() {
        val left = """
            |Some(
            |    Foo {
            |        lorem: "Hello World!",
            |        ipsum: 42,
            |        dolor: Ok(
            |            "hey",
            |        ),
            |    },
            |)
        """.trimMargin()
        val right = """
            |Some(
            |    Foo {
            |        lorem: "Hello Wrold!",
            |        ipsum: 42,
            |        dolor: Ok(
            |            "hey ho!",
            |        ),
            |    },
            |)
        """.trimMargin()
        val expected =
            " Some(\n" +
                "     Foo {\n" +
                "$RED_LIGHT<        lorem: \"Hello W$RESET${RED_HEAVY}o$RESET" +
                "${RED_LIGHT}rld!\",$RESET\n" +
                "$GREEN_LIGHT>        lorem: \"Hello Wr$RESET${GREEN_HEAVY}o$RESET" +
                "${GREEN_LIGHT}ld!\",$RESET\n" +
                "         ipsum: 42,\n" +
                "         dolor: Ok(\n" +
                "$RED_LIGHT<            \"hey\",$RESET\n" +
                "$GREEN_LIGHT>            \"hey$RESET${GREEN_HEAVY} ho!$RESET" +
                "${GREEN_LIGHT}\",$RESET\n" +
                "         ),\n" +
                "     },\n" +
                " )\n"
        checkPrinter(::writeLines, left, right, expected)
    }

    @Test
    fun writeLinesMultilineBlock() {
        val expected =
            "${RED_LIGHT}<Proboscis$RESET\n" +
                "${RED_LIGHT}<Cabbage$RESET\n" +
                "${GREEN_LIGHT}>Probed$RESET\n" +
                "${GREEN_LIGHT}>Caravaggio$RESET\n"
        checkPrinter(::writeLines, "Proboscis\nCabbage", "Probed\nCaravaggio", expected)
    }

    @Test
    fun writeLinesMultilineInsert() {
        val expected =
            "${RED_LIGHT}<Cabbage$RESET\n" +
                "${GREEN_LIGHT}>Probed$RESET\n" +
                "${GREEN_LIGHT}>Caravaggio$RESET\n"
        checkPrinter(::writeLines, "Cabbage", "Probed\nCaravaggio", expected)
    }

    @Test
    fun writeLinesMultilineDelete() {
        val expected =
            "${RED_LIGHT}<Proboscis$RESET\n" +
                "${RED_LIGHT}<Cabbage$RESET\n" +
                "${GREEN_LIGHT}>Probed$RESET\n"
        checkPrinter(::writeLines, "Proboscis\nCabbage", "Probed", expected)
    }

    @Test
    fun writeLinesIssue12() {
        val left = """
            |[
            |    0,
            |    0,
            |    0,
            |    128,
            |    10,
            |    191,
            |    5,
            |    64,
            |]
        """.trimMargin()
        val right = """
            |[
            |    84,
            |    248,
            |    45,
            |    64,
            |]
        """.trimMargin()
        val expected =
            " [\n" +
                "${RED_LIGHT}<    0,$RESET\n" +
                "${RED_LIGHT}<    0,$RESET\n" +
                "${RED_LIGHT}<    0,$RESET\n" +
                "${RED_LIGHT}<    128,$RESET\n" +
                "${RED_LIGHT}<    10,$RESET\n" +
                "${RED_LIGHT}<    191,$RESET\n" +
                "${RED_LIGHT}<    5,$RESET\n" +
                "${GREEN_LIGHT}>    84,$RESET\n" +
                "${GREEN_LIGHT}>    248,$RESET\n" +
                "${GREEN_LIGHT}>    45,$RESET\n" +
                "     64,\n" +
                " ]\n"
        checkPrinter(::writeLines, left, right, expected)
    }

    @Test
    fun bothTrailing() {
        val expected =
            "${RED_LIGHT}<$RESET${RED_HEAVY}fan$RESET\n" +
                "${GREEN_LIGHT}>$RESET${GREEN_HEAVY}mug$RESET\n" +
                " \n"
        checkPrinter(::writeLines, "fan\n", "mug\n", expected)
    }

    @Test
    fun bothLeading() {
        val expected =
            " \n" +
                "${RED_LIGHT}<$RESET${RED_HEAVY}fan$RESET\n" +
                "${GREEN_LIGHT}>$RESET${GREEN_HEAVY}mug$RESET\n"
        checkPrinter(::writeLines, "\nfan", "\nmug", expected)
    }

    @Test
    fun leadingAdded() {
        val expected =
            "${RED_LIGHT}<fan$RESET\n" +
                "${GREEN_LIGHT}>$RESET\n" +
                "${GREEN_LIGHT}>mug$RESET\n"
        checkPrinter(::writeLines, "fan", "\nmug", expected)
    }

    @Test
    fun leadingDeleted() {
        val expected =
            "${RED_LIGHT}<$RESET\n" +
                "${RED_LIGHT}<fan$RESET\n" +
                "${GREEN_LIGHT}>mug$RESET\n"
        checkPrinter(::writeLines, "\nfan", "mug", expected)
    }

    @Test
    fun trailingAdded() {
        val expected =
            "${RED_LIGHT}<fan$RESET\n" +
                "${GREEN_LIGHT}>mug$RESET\n" +
                "${GREEN_LIGHT}>$RESET\n"
        checkPrinter(::writeLines, "fan", "mug\n", expected)
    }

    @Test
    fun trailingDeleted() {
        val expected =
            "${RED_LIGHT}<$RESET${RED_HEAVY}fan$RESET\n" +
                "${GREEN_LIGHT}>$RESET${GREEN_HEAVY}mug$RESET\n" +
                "${RED_LIGHT}<$RESET\n"
        checkPrinter(::writeLines, "fan\n", "mug", expected)
    }
}
