import PrettyAssertions
import Testing

@Suite("PrettyAssertions Swift Export Smoke Tests")
struct PrettyAssertionsExportTests {
    @Test
    func swiftModuleLoads() {
        assertEq(left: "hello", right: "hello", message: nil)
        assertNe(left: "hello", right: "world", message: nil)
        assertStrEq(left: "hello", right: "hello", message: nil)
        #expect(true, "PrettyAssertions Swift module imported successfully")
    }
}

