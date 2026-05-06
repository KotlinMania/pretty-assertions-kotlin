# pretty-assertions-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fpretty--assertions--kotlin-blue.svg)](https://github.com/KotlinMania/pretty-assertions-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/pretty-assertions-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/pretty-assertions-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/pretty-assertions-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/pretty-assertions-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`rust-pretty-assertions/rust-pretty-assertions`](https://github.com/rust-pretty-assertions/rust-pretty-assertions).

**Original Project:** This port is based on [`rust-pretty-assertions/rust-pretty-assertions`](https://github.com/rust-pretty-assertions/rust-pretty-assertions). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `rust-pretty-assertions/rust-pretty-assertions`

> The text below is reproduced and lightly edited from [`https://github.com/rust-pretty-assertions/rust-pretty-assertions`](https://github.com/rust-pretty-assertions/rust-pretty-assertions). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

## Pretty Assertions

[![Latest version](https://img.shields.io/crates/v/pretty-assertions.svg)](https://crates.io/crates/pretty-assertions)
[![docs.rs](https://img.shields.io/docsrs/pretty_assertions)](https://docs.rs/pretty_assertions)
[![Downloads of latest version](https://img.shields.io/crates/dv/pretty-assertions.svg)](https://crates.io/crates/pretty-assertions)
[![All downloads](https://img.shields.io/crates/d/pretty-assertions.svg)](https://crates.io/crates/pretty-assertions)

Overwrite `assert_eq!` with a drop-in replacement, adding a colorful diff.

## Usage

When writing tests in Rust, you'll probably use `assert_eq!(a, b)` _a lot_.

If such a test fails, it will present all the details of `a` and `b`.
But you have to spot the differences yourself, which is not always straightforward,
like here:

![standard assertion](https://raw.githubusercontent.com/rust-pretty-assertions/rust-pretty-assertions/2d2357ff56d22c51a86b2f1cfe6efcee9f5a8081/examples/standard_assertion.png)

Wouldn't that task be _much_ easier with a colorful diff?

![pretty assertion](https://raw.githubusercontent.com/rust-pretty-assertions/rust-pretty-assertions/2d2357ff56d22c51a86b2f1cfe6efcee9f5a8081/examples/pretty_assertion.png)

Yep — and you only need **one line of code** to make it happen:

```rust,ignore
use pretty_assertions::{assert_eq, assert_ne};
```

<details>
<summary>Show the example behind the screenshots above.</summary>

```rust,ignore
// 1. add the `pretty_assertions` dependency to `Cargo.toml`.
// 2. insert this line at the top of each module, as needed
use pretty_assertions::{assert_eq, assert_ne};

fn main() {
    #[derive(Debug, PartialEq)]
    struct Foo {
        lorem: &'static str,
        ipsum: u32,
        dolor: Result<String, String>,
    }

    let x = Some(Foo { lorem: "Hello World!", ipsum: 42, dolor: Ok("hey".to_string())});
    let y = Some(Foo { lorem: "Hello Wrold!", ipsum: 42, dolor: Ok("hey ho!".to_string())});

    assert_eq!(x, y);
}
```

</details>

## Semantic Versioning

The exact output of assertions is **not guaranteed** to be consistent over time, and may change between minor versions.
The output of this crate is designed to be read by a human. It is not suitable for exact comparison, for example in snapshot testing.

This crate adheres to semantic versioning for publically exported crate items, **except** the `private` module, which may change between any version.

## Tip

Specify it as [`[dev-dependencies]`](http://doc.crates.io/specifying-dependencies.html#development-dependencies)
and it will only be used for compiling tests, examples, and benchmarks.
This way the compile time of `cargo build` won't be affected!

Also add `#[cfg(test)]` to your `use` statements, like this:

```rust,ignore
#[cfg(test)]
use pretty_assertions::{assert_eq, assert_ne};
```

If you want to enforce usage of `pretty_assertions` macros over `std`, add the following to `clippy.toml`:

```rs
disallowed-macros = [
  { path = "std::assert_ne", reason = "use `pretty_assertions::assert_ne` instead" },
  { path = "std::assert_eq", reason = "use `pretty_assertions::assert_eq` instead" },
  { path = "std::assert_matches::assert_matches", reason = "use `pretty_assertions::assert_matches` instead" },
]
```

## Notes

- Since `Rust 2018` edition, you need to declare
  `use pretty_assertions::{assert_eq, assert_ne};` per module.
  Before you would write `#[macro_use] extern crate pretty_assertions;`.
- The replacement is only effective in your own crate, not in other libraries
  you include.
- `assert_ne` is also switched to multi-line presentation, but does _not_ show
  a diff.
- Under Windows, the terminal state is modified to properly handle VT100
  escape sequences, which may break display for certain use cases.
- The minimum supported rust version (MSRV) is 1.35.0

### `no_std` support

For `no_std` support, disable the `std` feature and enable the `alloc` feature:

```toml
# Cargo.toml
pretty_assertions = { version= "...", default-features = false, features = ["alloc"] }
```

## License

Licensed under either of

- Apache License, Version 2.0, ([LICENSE-APACHE](https://github.com/rust-pretty-assertions/rust-pretty-assertions/blob/HEAD/LICENSE-APACHE) or <http://www.apache.org/licenses/LICENSE-2.0>)
- MIT license ([LICENSE-MIT](https://github.com/rust-pretty-assertions/rust-pretty-assertions/blob/HEAD/LICENSE-MIT) or <http://opensource.org/licenses/MIT>)

at your option.

### Contribution

Unless you explicitly state otherwise, any contribution intentionally
submitted for inclusion in the work by you, as defined in the Apache-2.0
license, shall be dual licensed as above, without any additional terms or
conditions.

## Development

- Cut a new release by creating a GitHub release with tag. Crate will be built and uploaded to crates.io by GHA.

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:pretty-assertions-kotlin:0.1.0-SNAPSHOT")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same MIT license as the upstream [`rust-pretty-assertions/rust-pretty-assertions`](https://github.com/rust-pretty-assertions/rust-pretty-assertions). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the rust-pretty-assertions authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`rust-pretty-assertions/rust-pretty-assertions`](https://github.com/rust-pretty-assertions/rust-pretty-assertions) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
