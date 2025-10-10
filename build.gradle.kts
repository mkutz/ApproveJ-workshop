plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.spring)
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependency.management)
  alias(libs.plugins.kotlin.jpa)
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

  runtimeOnly(libs.postgresql)

  testImplementation(libs.spring.boot.starter.test) { exclude(group = "junit", module = "junit") }
  testImplementation(libs.spring.boot.testcontainers)
  testImplementation(libs.approvej.core)
  testImplementation(libs.assertj.core)
  testImplementation(libs.datasource.proxy)
  testImplementation(libs.kotlin.test.junit5)
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
