# Workshop Notes

Notes taken while working through the ApproveJ Workshop.


## Setup

- `./gradlew check` passes cleanly — all tests green, spotless formatting OK.
- `ArticleManagerTest` runs as a fast unit test (no Spring context, uses stubs).
- `ShoppingCartControllerTest` is an integration test requiring Docker (TestContainers for PostgreSQL and Kafka).


## Approving vs Asserting String Results

### Key imports

```kotlin
import org.approvej.ApprovalBuilder.approve
```

### Workflow

1. Add `approve(value).byFile()` to the test.
2. First run **fails** — creates a `*-received.txt` file and an empty `*-approved.txt` file next to the test.
3. **Approve** by copying received content into the approved file (or use the IDE diff view's `>>` button).
4. Re-run — test **passes**, received file is automatically cleaned up.

### Observation: randomized `articleNumber`

The `anArticle()` builder randomizes `articleNumber` via `stubit-random`, so a plain `approve(response.body()).byFile()` fails on re-run because article numbers differ between runs. This requires adding a scrubber early:

```kotlin
approve(response.body())
  .scrubbedOf(stringsMatching("A-[A-Z]{3}-\\d{3}-\\d{3}"))
  .byFile()
```

This is a natural foreshadowing of the Scrubbing section.


## Pretty Printing

### Key imports

```kotlin
import org.approvej.json.jackson.JsonPrintFormat.json
```

### Usage

```kotlin
approve(response.body())
  .printedAs(json())
  .scrubbedOf(stringsMatching("A-[A-Z]{3}-\\d{3}-\\d{3}"))
  .byFile()
```

### Observations

- Applying `printedAs(json())` automatically changes the approved file extension from `.txt` to `.json`, which enables proper syntax highlighting in the IDE.
- The pretty-printed JSON is much easier to read and review than the single-line raw response.
- **Oversight discovered**: the original assertions did not verify `articleNumber` or `pricePerUnitFormatted` — the approved file captures these fields automatically, reducing the risk of missed checks.


## Scrubbing Dynamic Data

### Key imports

```kotlin
import org.approvej.scrub.Scrubbers.uuids
import org.approvej.scrub.Scrubbers.stringsMatching
import org.approvej.scrub.Scrubbers.isoInstants
```

### Built-in scrubbers used

| Scrubber | What it replaces | Placeholder format |
|---|---|---|
| `uuids()` | UUID values (e.g. `85fe6ee0-c2dd-...`) | `[uuid 1]`, `[uuid 2]`, ... |
| `stringsMatching("A-[A-Z]{3}-\\d{3}-\\d{3}")` | Article numbers (e.g. `A-TRX-919-840`) | `[scrubbed 1]`, `[scrubbed 2]`, ... |
| `isoInstants()` | ISO-8601 instants (e.g. `2026-02-20T08:49:02.008726Z`) | `[isoInstant 1]`, `[isoInstant 2]`, ... |

### Observations

- Each unique dynamic value gets a **unique numbered placeholder**, preserving referential integrity (e.g. the same UUID appearing in `id` and `imageUrl` both become `[uuid 1]`).
- Multiple `scrubbedOf(...)` calls can be chained for different value types.
- The `ShoppingCartControllerTest` response body revealed fields not covered by the original assertions: `articleNumber`, `quantityPerUnitValue`, `quantityTotalValue`, `quantityUnitSymbol`, and `insertionTime`.
- The `insertionTime` timestamp required `isoInstants()` since its format is `2026-02-20T08:49:02.008726Z` (ISO instant with `Z` suffix).


## Approving & Scrubbing POJO Results

### Observations

- `approve(dto)` works on any object, not just strings — ApproveJ serializes the POJO using the configured `PrintFormat`.
- Using `printedAs(json())` on a POJO produces the same clean JSON output as for strings.
- The `ArticleDtoTest` POJO required `uuids()` and `stringsMatching(...)` scrubbers for the same dynamic fields (`id`, `articleNumber`).
- The `ItemDtoTest` additionally needed `isoInstants()` for the `insertionTime` field.
- The README mentions you could avoid scrubbing by fixing the builder values (e.g. `anArticle().id(...).articleNumber(...).build()`), but using scrubbers is more realistic and teaches the concept better.


## API Reference (ApproveJ 1.3-SNAPSHOT)

### Core API

| Method | Description |
|---|---|
| `approve(value)` | Entry point — creates an `ApprovalBuilder<T>` |
| `.printedAs(printFormat)` | Converts value to a formatted string (also sets file extension) |
| `.printedBy(function)` | Converts value using a custom `Function<T, String>` |
| `.printed()` | Uses the default `PrintFormat` from configuration |
| `.scrubbedOf(scrubber)` | Replaces dynamic values with stable placeholders |
| `.byFile()` | Compares against an approved file (creates received + approved files on first run) |

### Print formats

| Format | Import | File extension |
|---|---|---|
| `json()` | `org.approvej.json.jackson.JsonPrintFormat.json` | `.json` |
| `singleLineString()` | `org.approvej.print.SingleLineStringPrintFormat.singleLineString` | `.txt` |

### File reviewers (configured in `approvej.properties`)

| Reviewer | Behavior |
|---|---|
| `none` (default) | Test fails with `ApprovalError`; no external tool launched |
| `automatic` | Auto-approves received file into approved file |
| Script (e.g. `idea diff --wait`) | Opens an interactive diff tool |

### Documentation links

- [ApproveJ Manual](https://approvej.org/)
- [Approve Strings](https://approvej.org/#approve_strings)
- [Printing](https://approvej.org/#printing)
- [Scrubbing](https://approvej.org/#scrubbing)
- [Built-in Scrubbers](https://approvej.org/#_built_in_scrubbers_replacements)
- [Scrubbers Javadoc](https://approvej.org/javadoc/core/org/approvej/scrub/Scrubbers.html)
