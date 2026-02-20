# ApproveJ Workshop
[![Build](https://github.com/mkutz/approvej-workshop/actions/workflows/build-main.yml/badge.svg)](https://github.com/mkutz/approvej-workshop/actions/workflows/build-main.yml)
[![Sonar Quality Gate](https://img.shields.io/sonar/quality_gate/mkutz_ApproveJ-workshop?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=mkutz_ApproveJ-workshop)
[![Sonar Coverage](https://img.shields.io/sonar/coverage/mkutz_ApproveJ-workshop?server=http%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=mkutz_ApproveJ-workshop)

Workshop material for learning ApproveJ.org.


## Material

In order to fulfill the following objectives, you will need

- The contents of this repository,
- JDK 21 installed on your system,
- Docker (required for integration tests that use [Testcontainers](https://www.testcontainers.org/)), and
- an IDE like IntelliJ IDEA


## Code Base

This code base is a basic Spring Boot service written in Kotlin, built with Gradle.

The main purpose of the service is to provide an _API for creating virtual shopping carts and fill them with items_.
These items need to represent a valid article.
Articles are managed by some other service, which shares the article master data via a Kafka topic.
The service also provides another API to look for articles.


### Structure

Its structure is a simple version of [Hexagonal Architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)).

It features [three adapters, which demand services from the application](src/main/kotlin/org/approvej/workshop/service/adapters/demanding):

- an HTTP API controller to look up article data,
- another HTTP API to manage shopping carts, and
- a Kafka consumer to import articles.

There are [two adapters, which provide services to the application](src/main/kotlin/org/approvej/workshop/service/adapters/providing):

- a database adapter to store articles,
- another database adapter to store shopping carts.

The [application](src/main/kotlin/org/approvej/workshop/service/application) contains two domains:

- articles, and
- shopping carts.


### Tests

The tests are generally written as [sociable unit tests](https://martinfowler.com/bliki/UnitTest.html#SolitaryOrSociable).

Those [tests for the application](src/test/kotlin/org/approvej/workshop/service/application) code are using self-written stubs of the two providing ports to not require an application context or mocking.

Some of the [adapter tests](src/test/kotlin/org/approvej/workshop/service/adapters) rely on Spring Boot and Testcontainers to mitigate risks of framework and infrastructure integration.


### Formatting

Note that this code base uses [Spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle) to format and verify formatting.
So, wrong formatting can fail Gradle's `check` task.

You can automatically reformat the code by running

```bash
./gradlew spotlessApply
```

If you prefer to rely on your IDEs formatting, feel free to remove the `spotless` configuration from the [build.gradle.kts](build.gradle.kts).


## Objectives

### 🚧 Setup

- [ ] Checkout the code of this repository and load it in your IDE.

- [ ] Try to run all tests using Gradle on the command line

  ```bash
  ./gradlew check
  ```

- [ ] Execute a simple application unit test class like [ArticleManagerTest](src/test/kotlin/org/approvej/workshop/service/application/article/ArticleManagerTest.kt) via you IDE.

- [ ] Execute an adapter integration tests like [ShoppingCartControllerTest](src/test/kotlin/org/approvej/workshop/service/adapters/demanding/http/shoppingcart/ShoppingCartControllerTest.kt).


### 👍 Approving vs 🔬 Asserting String Results

Approval testing is particularly useful for tests with a lot of assertions on a single result objects.
As approving the result as whole, in contrast to asserting single aspects of it, makes the test less bloated, confusing and concise.

For the following objectives, you will need knowledge from the ApproveJ manual chapter [Approve Strings](https://approvej.org/#approve_strings).

- [ ] Take a look at the [ArticleControllerTest](src/test/kotlin/org/approvej/workshop/service/adapters/demanding/http/article/ArticleControllerTest.kt).

  The test contains a lot of assertions that specify the result body in quite some detail.

  For now, add an approval below the assertions on the response body:

  ```kotlin
  approve(response.body()).byFile()
  ```

  The test should fail and open a diff view of the new received file `ArticleControllerTest-get_articles-received.txt` and the new and empty approved file `ArticleControllerTest-get_articles-approved.txt` right next to the test file.

  You can approve the received body by putting the content of the received file into the approved file using the diff view's <kbd>>></kbd> button.

- [ ] Re-run the test.

  This time it should be successful and the received file should automatically be removed.


### 🖨️ Pretty Printing

Now the content of the approved file is not exactly great to read.
A change in the content of the last returned article might easily be overlooked.
Even though the content is in proper JSON format, the IDE honors the filename extension `txt` and does not apply proper syntax highlighting.

Let's change that by applying proper [printing](https://approvej.org/#printing) and the [JSON pretty printing](https://approvej.org/#_pretty_print_json_strings) abilities of ApproveJ's JSON module.

- [ ] Apply the `JsonStringPrettyPrinter` to the result using the `printedWith` method.

  ```kotlin
  approve(response.body())
    .printWith(jsonStringPrettyPrinter())
    .byFile()
  ```

  This time the diff view opens for two new files: `ArticleControllerTest-get_articles-received.json` and `ArticleControllerTest-get_articles-approved.json`.
  Note that applying the printer changed the filename extension automatically.

- [ ] Approve the received and rerun the test.
  It should succeed now.

- [ ] Compare the approved file's content with the original assertions.
  Were there any oversights?

- [ ] Remove the assertions on the response body as they are no longer needed.
  The assertion on the response code might still be useful.


### 🧽 Scrubbing Dynamic Data from Results

Note that so far the results must be very static.
The test arranges the test data in a way, that the result contains no dynamic value.
E.g. the IDs and article numbers of the articles are not random, but are always set to the same static values.
This may not always be possible.
Parts of the data may be generated by the code.
That could be IDs of any kind, timestamps, dates etc.

To deal with such problems, ApproveJ comes with a feature called [Scrubbing](https://approvej.org/#scrubbing).

- [ ] Take a look at the [ShoppingCartControllerApiTest](src/test/kotlin/org/approvej/workshop/service/adapters/demanding/http/shoppingcart/ShoppingCartControllerTest.kt).

  Again, add an approval to the first test case

  ```kotlin
  approve(response.body())
    .printWith(jsonStringPrettyPrinter())
    .byFile()
  ```

  Approve the results and re-run the test.

  It still fails, as the ID of the shopping cart is generated by the business logic, and we have no way of controlling it from the test code.

- [ ] Add `scrubbedOf(uuids())` to replace the dynamic UUID with a placeholder.
  Re-run the test and approve the result.

- [ ] Try to add the same approval the other test cases' assertions with the same approval.

- [ ] Figure out why the tests fail and chose one of the [built-in Scrubbers](https://approvej.org/#_built_in_scrubbers_replacements) to deal with the remaining dynamic values.

  The [`stringsMatching(pattern)`](https://approvej.org/javadoc/core/org/approvej/scrub/Scrubbers.html#stringsMatching(java.lang.String)) could be used, or you could write your own [custom Scrubber](https://approvej.org/#_custom_scrubber)?

- [ ] Again, compare the approved file with the original assertions.
  Remove the assertions.


### ☕️ Approving & Scrubbing POJO Results

So far, we only looked at API tests whose results are basically strings.
But ApproveJ also allows to [approve any kind of plain old Java object (POJO)](https://approvej.org/#approve_pojos).

- [ ] The [ArticleDtoTest](src/test/kotlin/org/approvej/workshop/service/adapters/demanding/http/article/ArticleDtoTest.kt) checks the mapping between the `Article` and the `ArticleDto` class.
  Again, this takes quite a lot of assertions.
  Try to replace it with an approval.

- [ ] The received file again contains the result object in a single line, which makes it hard to overlook differences.

  ApproveJ offers a variate of Printers to turn POJOs into readable strings.
  The most basic one would be the [generic `ObjectPrinter`](https://approvej.org/#_generic_object_printer), but you can also choose the [`JsonPrettyPrinter`](https://approvej.org/#_pretty_print_json_strings), or the [`YamlPrinter`](https://approvej.org/#_print_as_yaml), or use a [custom printer](https://approvej.org/#custom_printer_implementation).

  Choose one option and re-run the test.

- [ ] Again, we are faced with some dynamic values in the result that keep the test from succeeding.

  [Built-in scrubbers](https://approvej.org/#_built_in_scrubbers_replacements) like the [`uuids()`](https://approvej.org/javadoc/core/org/approvej/scrub/Scrubbers.html#uuids()) scrubber reduces the problem, but won't suffice this time.
  Maybe the [`stringsMatching(pattern)`](https://approvej.org/javadoc/core/org/approvej/scrub/Scrubbers.html#stringsMatching(java.lang.String)) could be used, or the [`strings(string…)`](https://approvej.org/javadoc/core/org/approvej/scrub/Scrubbers.html#strings(java.lang.String,java.lang.String...)) could also be an option, or you could write your very own [custom `Scrubber<ArticelDto>`](https://approvej.org/#_custom_scrubber)?

  Maybe you can also find a way to avoid scrubbing at all?

- [ ] Apply the same to the [ItemDtoTest](src/test/kotlin/org/approvej/workshop/service/adapters/demanding/http/shoppingcart/ItemDtoTest.kt).

  Here, you won't be able to avoid applying one of the [Scrubbers](https://approvej.org/javadoc/core/org/approvej/scrub/Scrubbers.html).

- [ ] You may find more opportunities to add ApproveJ to further tests.
  Feel free to try something out.


## Conclusion & Outlook

I hope this workshop was beneficial for you and the concept of Approval Testing in general and ApproveJ are clear to you, and you will be able to apply it in you daily work.
We've seen how Approval Testing can solve the issue of long assertion blocks on single objects.
This makes the test code and the results easier to understand.
It reduces the risk of oversights in writing detailed assertions.

We mostly applied it here to adapter tests for API code.
One could say that this is a form of contract verification.
But so far, we only looked into the demanding adapter code, even tough contracts exists on the providing side as well.

Right after the release of v1.0, I will try to extend the feature set of ApproveJ into that direction.
There are already experiments going on to allow approving produced Kafka messages, outgoing HTTP requests, and even database queries that are produced by the application.

If you have further ideas how ApproveJ could evolve, please do contact me.
If your idea is concrete and small enough, consider [filing an issue](https://github.com/mkutz/ApproveJ/issues/new/choose).

Happy coding 🚀
