# Unit Testing in Java — Cheat Sheet

A condensed reference based on *Unit Testing in Java — The Missing Bits* by Martin Patsov (Dreamix). The focus is on **how** to write unit tests — the rules that differ from production code. Use it before writing tests, during code review, or in interviews.

## Table of Contents

- [Naming Tests](#naming-tests)
- [Test Structure (Given / When / Then)](#test-structure-given--when--then)
- [Naming, Readability & Structure](#naming-readability--structure)
- [Testing Principles](#testing-principles)
- [Qualities of Good Tests](#qualities-of-good-tests)
- [Testable Code Design](#testable-code-design)
- [More Best Practices](#more-best-practices)
- [Quick Rules of Thumb](#quick-rules-of-thumb)

---

## Naming Tests

The hardest things in software: naming things, cache invalidation, and off-by-one errors. Pick **one** naming convention and use it everywhere.

| Convention | Example |
|---|---|
| `operation_whenConditions_generatesResult` | `runEngine_withWrongFuel_fails` |
| `operation_shouldGenerateResult_whenCondition_isPresent` | `runEngine_shouldFail_whenWrongFuel_isProvided` |
| `resultOccurs_ifCondition_isPresent` | `engineFails_ifWrongFuel_isProvided` |

**A test name should always contain three parts:**
1. **What** is being tested (method / action / operation).
2. The **context** / scenario covered.
3. The **expected behavior / result**.

Drop any one of these and the reader has to open the test body to understand it — wasted time.

---

## Test Structure (Given / When / Then)

Every unit test has three mandatory parts. Separate them visually with **blank lines** for readability and to avoid missing something.

| Given / Arrange | When / Act | Then / Assert |
|---|---|---|
| Preparation & input data | The actual execution under test | Validate the outcome |

**Given / Arrange** — all variables, literals, and mocked behavior. Can be split further into:
1. Mocks initialization
2. Data (real objects) initialization
3. Mocking of desired behavior (e.g. with Mockito)

**When / Act** — the call on the object under test. This is the "operation" from the naming conventions.

**Then / Assert** — validate that the behavior produced the desired outcome.

> ⚠️ A test without validation tests **nothing** — it only inflates coverage. If you enforce high coverage gates, developers may skip asserts to hit the number while adding zero real value.

---

## Naming, Readability & Structure

- **Name the tested object `objectUnderTest`** (or `componentUnderTest`) — keeps focus on what matters.
  `Car objectUnderTest = new MercedesBenz();`
- **Use the builder pattern** for complex objects — field names and values stay obvious.
  `Car objectUnderTest = MercedesBenzBuilder.oneBenz().withFuel(GAZOLINE).withTankCapacity(80L).build();`
- **Use named variables** for context-poor literals (empty strings, raw numbers, nulls).
  `long tankCapacityInLitres = 80L;`
- **Extract constants** when a value means "the same thing" as another value in the test's scope.
- **One test, one thing, one reason to fail** — that's what makes it a *unit* test. No cross-component interaction, no side effects.
- **Compare whole objects** rather than individual fields → a single assert. (Multiple asserts aren't forbidden; experience and context guide you.)
- **Suffix mocked variables with `Mock`** to separate them from real instances.
  `Engine gazolineEngineMock = mock(GazolineEngine.class);`
  (Exception: when mocking multiple deps of the same type with `@InjectMocks`, keep the original field names.)
- **(JUnit 5)** Use `@DisplayName` for human-readable test results.
- **Add human-readable messages to failing assertions** — especially true/false asserts — so failures explain themselves.

---

## Testing Principles

- **Validate behavior, not implementation.** Testing implementation produces fragile, "change-detecting" tests you don't want.
  - If a method **returns a result** → assert the result.
  - If a method **returns nothing** → verify the called methods on mocked dependencies (often the only way).
- **TDD: Red → Green → Refactor.**
  - **Red:** always see the test fail at least once before writing the code — proof it's not a false positive.
  - **Refactor:** care for tests like production code — remove redundant mocks, stale tests, and duplicate scenarios.
- **Follow the test pyramid:** aim to have a failing *unit* test for every failing *integration* test — it pinpoints the source of problems faster.

---

## Qualities of Good Tests

### Trustworthy
A test fails when it should and passes when it should.
- **No false positives** — e.g. tests without asserts, or tests never seen failing.
- **No false negatives** — fragile tests that break on implementation change while behavior is unchanged. (Unit tests should *enable* safe refactoring.)
- **Mutation testing** (tools exist) — deliberately break production code to confirm tests catch it, then revert.
- **No business/production logic in tests** — production code carries production bugs; copied private functions, conditionals, and loops make a test meaningless.

### Maintainable
> Tests are often *more* important than production code — yet we instinctively value what's written first (the production code) more. If maintaining tests becomes a burden, developers stop writing them.

- **Avoid fragile tests** — don't verify method calls when a result is returned; don't assert values irrelevant to the scenario.
- **Don't test private methods** — internals change often. Test them through their public caller. If a private method truly needs testing, make it public or package-private.
- **Extract and avoid duplication** — use `@Before` setup methods.
- **Enforce test isolation** — execution order or running only a subset must not change results; no test calling other tests; start each test on a clean slate (no shared-state corruption).

### Readable
- Keep **one consistent naming convention** across all tests.
- The name must convey **what** + **context** + **expected result** (see [Naming Tests](#naming-tests)).

---

## Testable Code Design

- **Write tests first (TDD)** — enforces testable design, smaller classes, better readability, easier maintenance. (See the Three Laws of TDD.)
- **Use constructor injection, not field injection** — easier mocking; an alternative to `@InjectMocks`.
  - Rule: don't instantiate dependencies inside the constructor body or field declaration.
- **Avoid static methods, final methods, and final classes** — they're hard to test and push you toward PowerMock.
  - Rule: if you reach for PowerMock, rethink it twice, then don't. Hide static/final logic behind package-private methods and test those instead.

---

## More Best Practices

- **Test boundary values.** For a zip code valid in 1000–2000, test `999, 1000, 2000, 2001`. For `if` conditions, test the border values. For array params, test empty, null, and max-size.
- **Skip trivial getters/setters** with no validation or transformation — near-zero value.
- **Test invalid input** and validate behavior — it documents intent for other developers.
- **Don't test components only through their interaction** — implementation may shift and leave a component untested.
- **Create regression tests for bugs** — add the test *before* you start debugging. It's your safety net against yourself.
- **Avoid generic matchers in the assert part** — use a literal or variable, not `any()` / `anyInt()`. (Generic matchers are fine in the *mock* part.)
- **Put tests in `src/test`**, never `src/main`.
- **Avoid state in tests** (e.g. static variables) — it breaks isolation via side effects.
- **Be careful loading data from files** — I/O is slow and unpredictable; close any closeable resources.
- **Be careful with locales and dates** — use fixed dates, not `LocalDateTime.now()`; set the locale per test and restore it afterward.
- **Make asserts as human-readable as possible** — AssertJ helps.
- **Don't over-specify test method names** — omit ids/codes/object names that go stale fast. Treat names like comments: update them on every test change.

---

## Quick Rules of Thumb

- Structure every test as **Given / When / Then** (Arrange / Act / Assert), separated by blank lines.
- Name = **what** + **context** + **expected result**. Pick one convention, keep it everywhere.
- One test → one thing → one reason to fail. No logic, loops, or conditionals in tests.
- Validate **behavior**, not implementation — avoid fragile, change-detecting tests.
- TDD: Red → Green → Refactor. See the test fail at least once.
- Don't test private methods directly; test through the public caller.
- Constructor injection over field injection. Avoid static/final/PowerMock.
- Test boundaries, invalid input, and add regression tests before debugging.
- Use `objectUnderTest`, suffix mocks with `Mock`, prefer AssertJ for readable asserts.
- A test without an assert tests nothing.

> **Key takeaway:** Stay in control of what you are creating.

---

*Source: "Unit Testing in Java — The Missing Bits" by Martin Patsov, Java Expert at Dreamix.*
