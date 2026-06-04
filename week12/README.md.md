# Clean Code Cheat Sheet (Uncle Bob)

A condensed reference based on Robert C. Martin's *Clean Code* lecture series. Use it as a quick refresher before code review, refactoring, or interviews.

## Table of Contents

- [1. Fundamentals](#1-fundamentals)
- [2. Names](#2-names)
- [3. Functions](#3-functions)
- [4. Function Structure](#4-function-structure)
- [5. Form (Comments, Formatting, Classes vs Data Structures)](#5-form)
- [6. Test-Driven Development (TDD)](#6-test-driven-development-tdd)
- [7. Architecture & Use Cases](#7-architecture--use-cases)
- [8. Design Patterns](#8-design-patterns)
- [Quick Rules of Thumb](#quick-rules-of-thumb)

---

## 1. Fundamentals

- **Bad code slows development down.** You never go faster by rushing — you always go slower.
- **Rigidity** — a system resists change: one bug fix forces edits in many places. Rigid systems blow estimates.
- **Inseparability** — useful parts can't be pulled out for reuse elsewhere; reuse can't be reliably estimated.
- **Opacity** — code so poorly structured the author's intent can't be recovered no matter how hard you try.
- **"Go back and clean later" means never.** Clean as you go.

**What clean code looks like:**
- Does **one thing**, simply and directly.
- Reads like well-written prose.
- Looks like someone who *cares* wrote it.
- Every routine does pretty much what you expected.
- **Elegant** = does a lot in a few words. **Efficient** = uses few CPU cycles.
- Leave the world (code) better than you found it.

---

## 2. Names

**Reveal intent.** A name should reveal the intent of the thing. If you need a comment to explain it, the name failed.

- **Describe the problem, not the implementation.** If you must read the code to understand a name, it's a bad name.
- **Avoid disinformation** — a name should say what it means and mean what it says.
- Use **pronounceable** names.
- **Avoid encodings** — no prefixes like `IAccount`.

**Parts of speech:**

| Element | Naming rule |
|---|---|
| Classes & variables | Nouns / noun phrases |
| Methods | Verbs / verb phrases |
| Booleans | Predicates, e.g. `isEmpty`, `isPostable()` |
| Properties (C#) | Methods pretending to be variables → nouns (or predicates if boolean) |
| Enums | States / descriptors → adjectives |

- **Avoid noise words** — `Manager`, `Processor`, `Data`, `Info`. They signal you don't actually know how to name the thing.

**Scope-length rule:**
- **Variables:** shorter names for shorter scopes.
- **Functions & classes:** the *opposite* — longer scope → shorter name.
  - Private functions/classes can have longer names; they document themselves.
  - The more derived a class, the more adjectives in its name.

> Any fool can write code a computer understands. A good programmer writes code a **human** understands.

---

## 3. Functions

**A function should do one thing, do it well, and do it only.**

- **First rule: be small.** ~4–6 lines; 10 is way too big.
- **Second rule: smaller than that.**
- Long functions hide classes inside them.
- Keep indentation low — preferably one level.
- Different **levels of abstraction** should not be mixed in one function.

**Extract till you drop** — if you can extract a function from another, you should.
- **Feature envy:** move a function to the class whose data it uses (watch which variables/params it touches).
- **Switch statements** can usually be replaced with polymorphism.

**Why small functions win:** good names mean you can't get lost; teammates find things faster (geography / bedroom metaphors). Readability first — organize your thoughts into small, concise functions.

---

## 4. Function Structure

**Arguments — as few as possible:**
- Max 3, prefer ≤ 2. Pass a class or use a builder instead.
- **No boolean arguments, ever.**
- **No output arguments** (don't return values via parameters).
- Don't use `null` as a pseudo-boolean.

**Defensive programming is a smell** — leads to offensive code and wasted effort. Trust your tests.

**Class layout:**
- Public methods at the top.
- **Step-down rule:** no lower method calls an upper method.

**Switches & cases:**
- "A missed opportunity for polymorphism." Switches (and long if-else chains) create the **fan-out problem** — knots of dependency that block independent deployability.
- **Convert switch → OO:** replace the switch argument with an abstract base class exposing the operation; each case becomes a derived class implementing it.
- Dependency rule: source-code dependencies point toward interfaces — `A → I ← B` (B implements I), even when flow of control is `A → B`.

**Three surviving paradigms:** Functional, Structured, Object-Oriented.

**Functional programming** (Lisp, 1957): no mutable state, no side effects, pure functions.

**Side effects & temporal coupling** — calls that must happen in order: `open/close`, `set/get`, `new/delete`.
- Reduce coupling by passing a "block" (a small, meaningful transaction).
- Goal isn't to eliminate side effects, but to **impose discipline** on where/how they happen.

**Command Query Separation:**
- Functions that change state return nothing.
- Functions that return values change nothing.

**Tell, Don't Ask** (extreme CQS):
- Avoid trainwrecks: `o.getX().getZ().getA().doSomething()` → use `o.doSomething()` and let it propagate.
- **Law of Demeter** — a function may call methods on objects that are: passed as arguments, created locally, instance variables, or globals. It may **not** call methods on objects *returned from a previous call*.

**Structured programming** — every algorithm is built from 3 operations:
1. **Sequence** — exit of one block enters the next.
2. **Selection** — boolean splits flow into two blocks, then rejoins to a single exit.
3. **Iteration** — repeat a block until a boolean exits the loop.

Single entrance at top, single exit at bottom.
- **Early returns** are fine — they just reach the exit sooner.
- **Avoid mid-loop returns and `break`** — they add an unexpressed, indirect exit condition. `continue` is OK; labeled `break` is the worst.

> Making your code understandable matters more than making it work.

**Error handling:**
- Important, but if it obscures logic, it's wrong.
- **Errors first** — write error handling before the rest of the logic.
- **Prefer exceptions** — don't return `false`, `null`, or error codes.
- Exceptions are for callers — scope them to the throwing class; name them precisely instead of relying on detailed messages. ("The best comment is the one you don't have to write.")
- Use **unchecked** exceptions (derive from `RuntimeException`).
- **Null object / special case pattern** for special cases. `null` isn't always an error — it can be a valid return value when it carries logical meaning.

**"Trying is one thing":**
- If `try` appears, it's the first thing after variable declarations.
- The `try` body is a single line — one function call.
- `catch`/`finally` are the last things; nothing follows.
- A function does one thing: it *does something* **or** *handles errors*, never both.

---

## 5. Form

### Comments

- **The code is the coding standard.** A separate standards document means the code failed to express it.
- **Comments should be rare** — a comment is a confession that you failed to express yourself in code.
- **Comments are lies** — they're hard to keep truthful, rot over time, and degrade into misinformation (they're non-local).

**Good comments:** legal headers, informative (e.g. regex explanation), clarification of intent, warnings of consequences, public API docs. (Best API doc is the one you don't have to write.)

**Bad comments:** mumbling, redundant explanation, mandated redundancy, wrong/misleading, journal comments (use git), noise, banner/position markers, closing-brace comments, attribution ("Added by Rick"), HTML, non-local info, and **commented-out code (delete it)**. Skip TODOs — just do the thing.

### Formatting

- Whitespace carries information — be disciplined, not random.
- **Getting code to communicate matters more than getting it to work.**

**Vertical formatting:**
- One blank line between methods, and between methods and variables.
- Blank line between a constant and a private variable.
- Blank line separating declarations from the rest of a method, and around `if`/`while`.
- Group related variables; keep related things vertically close.

**Horizontal formatting:**
- Never scroll right. 200 chars is too much.

**Indentation:** keep it consistent across the team.

**File size (fitness):**

| Metric | Target |
|---|---|
| Average | 50–60 lines |
| Most files | < 200 lines |
| Maximum | 500 lines |

Project size and file size don't correlate; big projects don't imply big files. Keep files small.

### Classes vs Data Structures

**Classes:** expose functions, hide private variables — hide and abstract implementation.
- Follow Tell-Don't-Ask → fewer getters → fewer setters → less state.
- **Cohesion** = how many private variables a method uses. Getters/setters are low-cohesion; the more of them, the less cohesive the class.

**Data structures:** the opposite — public variables, virtually no functions.
- May have get/set, navigation aids, or methods manipulating individual variables.
- Expose (don't abstract) implementation. You can only *ask* a data structure questions.

**The Expression Problem:**

| Combination | Verdict |
|---|---|
| Switch + data structures | OK |
| Polymorphism + classes | OK |
| Switch + classes | NOT OK |

- Use **classes & polymorphism** when new **types** are more likely to be added.
- Use **data structures & switches** when new **methods** are more likely to be added.

### Boundaries

- Code on each side of a boundary is totally different (model/view, app/DB, main/application).
- The application should not know about the DB.
- **Impedance mismatch** (ORMs): dependencies that cross boundaries point **toward abstractions, away from concretions** — the **Dependency Inversion Principle**.

---

## 6. Test-Driven Development (TDD)

**The three laws:**
1. Write **no** production code except to pass a failing test.
2. Write only **enough** of a test to fail (not compiling counts as failing).
3. Write only **enough** production code to pass the test.

**Cycle:** Red → Green → Refactor.

**Why TDD:**
- Everything compiles and runs every minute or two → defects live in a tiny delta → almost no debugging.
- Tests are a low-level design document that's always up to date.
- Testable = **decoupled**.
- **Courage to change:** code rots because devs fear changing it. Tests eliminate the fear — nothing makes a system more flexible than a suite of tests.
- **Trust:** trust your tests like a parachute. Tests written *after the fact* feel like busywork, so you cut corners — you'd never jump with a holey parachute.

**Answers to common objections:**

| Objection | Answer |
|---|---|
| You write more code, so slower | Faster overall — less debugging, always-clean codebase |
| Manager won't allow it | It's your professional choice; no permission needed |
| Refactoring is rework | Every creative work is iterative — painters don't nail it first try |
| Who tests the tests? | The production code does |
| One change breaks many tests | Then the tests are poorly designed — refactor them like production code |
| Tests can't prove absence of bugs | Goal isn't proof; it's a parachute. 99% is damn good |
| TDD is dogma | Disciplines *are* predefined decisions — but step back and rethink periodically |
| Timing of tests doesn't matter | Tests written last are incomplete and untrusted; humans deprioritize what's done last |
| What about legacy code? | Legacy = code without tests. Start small, refactor a testable slice, spread tests gradually. You'll never cover all of it |
| How to test GUIs? | Mostly don't — test the logic and the model behind the screen, not the last UI layer |
| How to test DBs? | Mostly don't — test that your schema behaves |

**Professionalism:**
- **Double-entry bookkeeping** analogy: accountants record every debit and credit on separate paths to catch errors. TDD does the same — once on the test side, once on production. Why treat software with less respect?
- **QA should find nothing.** If a defect escapes, make sure that kind never happens again.
- Push for **100% coverage** — why aim lower?
- *Doctors washing hands:* a discipline that dropped death rates dramatically but took ~60 years to accept. Don't be the holdout.

---

## 7. Architecture & Use Cases

- **Architecture is about usage, not tools.** A good architecture **screams use cases**, not the delivery mechanism (web, DB, UI).
- An accounting system's architecture should scream **ACCOUNTING**, not **WEB**. You shouldn't be able to tell how it's delivered.
- **Decouple use cases from delivery** so thoroughly they could deploy separately.
- **Defer decisions:** a good architect keeps decisions about UI, service layer, and DB delayed. A good architecture maximizes decisions *not yet made*.
- A **use case** is a formal description of how a user interacts with the system to achieve a goal.
- **Partitioning:** use cases are application-specific; entity business rules are valid across applications.
- **Isolation:** use cases know nothing about the delivery mechanism — boundaries aren't crossed.
- **The architect is a person who writes code.**

---

## 8. Design Patterns

- **Gang of Four (GoF):** Gamma, Helm, Johnson, Vlissides. A pattern is "a named solution to a problem in a context."
- **Three categories:** Creational, Structural, Behavioral.
- Every pattern pursues one goal: **dependency management**. Each is a crystallization of the **SOLID** principles.

**Command pattern:**
- Decouples *what is done* from *who does it* (the actor doesn't know the concrete command).
- Decouples *what is done* from *when* (queue commands, run later — temporal decoupling).
- Enables do / undo / redo by persisting what each command did.
- **Actor / run-to-completion model:** commands run to completion before any other; one shared system stack serves thousands of threads (great for embedded/C++).

**Factories** — solve the problem of *who creates instances* without violating Dependency Inversion (the main app shouldn't depend on concretions).
- **Abstract Factory:** app depends on a `ShapeFactory` interface; the impl creates concrete shapes.
- **Boundary dilemma:** one factory method per shape means each new shape forces a change above the boundary → no independent deployability. Solution: a single `make(String name)` + `getShapeNames()` returning strings.
- **Trade static type safety for independent deployability** — benefits outweigh the cost; **TDD catches** the small type breaches.
- **Prototype:** clone already-created objects instead of building new ones.

**Strategy vs Template Method:**

| | Strategy | Template Method |
|---|---|---|
| Polymorphism | External | Internal |
| Coupling | High & low policy independent | Low-level depends on high-level |
| Flexibility | Hot-swappable at runtime, even mid-process | Stuck with one instance for its lifetime |
| Ease of creation | Lower | Higher |
| Independent deployability | Yes | No |

- **Abstract Factory** is a special case of **Strategy**.
- **Factory Method** is a special case of **Template Method**.

---

## Quick Rules of Thumb

- Functions: small, one thing, ≤ 2 args, no booleans, no output args.
- Names: reveal intent; no comment needed if the name is right.
- Comments: rare; a comment is a failure to express in code. Delete commented-out code.
- Prefer exceptions over error codes; use unchecked exceptions; errors first.
- Tell, Don't Ask. Obey the Law of Demeter — no trainwrecks.
- Command Query Separation: change state **or** return a value, not both.
- Replace switches with polymorphism (when adding types); keep switches with data structures (when adding methods).
- Dependencies point toward abstractions (Dependency Inversion).
- Architecture screams use cases, not frameworks. Defer decisions.
- TDD: Red → Green → Refactor. Trust tests like a parachute. Aim for 100% coverage.
- Leave the code cleaner than you found it.
