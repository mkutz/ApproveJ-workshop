plugins {
  jacoco
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.spring)
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependency.management)
  alias(libs.plugins.kotlin.jpa)
  alias(libs.plugins.sonar)
  alias(libs.plugins.spotless)
}

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

repositories { mavenCentral() }

ext["junit-jupiter.version"] = libs.versions.junit.get()

ext["testcontainers.version"] = libs.versions.testcontainers.get()

dependencies {
  implementation(libs.jackson.module.kotlin)
  implementation(libs.kotlin.reflect)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.kafka)

  runtimeOnly(libs.postgresql)

  testImplementation(libs.approvej.json.jackson)
  testImplementation(libs.assertj.core)
  testImplementation(libs.awaitility.kotlin)
  testImplementation(libs.datasource.proxy)
  testImplementation(libs.kotlin.test.junit5)
  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.spring.boot.testcontainers)
  testImplementation(libs.spring.kafka.test)
  testImplementation(libs.stubit.random)
  testImplementation(libs.testcontainers.junit.jupiter)
  testImplementation(libs.testcontainers.kafka)
  testImplementation(libs.testcontainers.postgresql)

  testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin { compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") } }

allOpen {
  annotation("jakarta.persistence.Entity")
  annotation("jakarta.persistence.MappedSuperclass")
  annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.jacocoTestReport { reports { xml.required = true } }

sonar {
  properties {
    property("sonar.projectKey", "mkutz_ApproveJ-workshop")
    property("sonar.organization", "mkutz")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}

spotless {
  format("misc") {
    target("**/*.md", "**/*.xml", "**/*.yml", "**/*.yaml", "**/*.html", "**/*.css", ".gitignore")
    targetExclude("**/build/**/*", "**/.idea/**")
    trimTrailingWhitespace()
    endWithNewline()
    leadingTabsToSpaces(2)
  }

  kotlin {
    target("**/*.kt")
    targetExclude("**/build/**/*")
    ktfmt().googleStyle()
    leadingTabsToSpaces(2)
  }

  kotlinGradle {
    target("**/*.gradle.kts")
    targetExclude("**/build/**/*.gradle.kts")
    ktfmt().googleStyle()
  }
}
