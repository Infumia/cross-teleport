import com.diffplug.gradle.spotless.YamlExtension.JacksonYamlGradleConfig
import com.diffplug.spotless.LineEnding

plugins {
  java
  alias(libs.plugins.spotless)
}

val spotlessApply = property("spotless.apply").toString().toBoolean()
val shadePackage = property("shade.package")
val signRequired = !property("dev").toString().toBoolean()
val relocations =
  property("relocations")
    .toString()
    .trim()
    .replace(" ", "")
    .split(",")
    .filter { it.isNotEmpty() }
    .filter { it.isNotBlank() }

allprojects { group = "tr.com.infumia" }

subprojects {
  apply<JavaPlugin>()
}

repositories {
  mavenCentral()
}

if (spotlessApply) {
  spotless {
    lineEndings = LineEnding.UNIX
    isEnforceCheck = false

    val prettierConfig =
      mapOf(
        "prettier" to "latest",
        "prettier-plugin-java" to "latest",
      )

    format("encoding") {
      target("*.*")
      encoding("UTF-8")
      endWithNewline()
      trimTrailingWhitespace()
    }

    yaml {
      target(
        "**/src/main/resources/*.yaml",
        "**/src/main/resources/*.yml",
        ".github/**/*.yml",
        ".github/**/*.yaml",
      )
      endWithNewline()
      trimTrailingWhitespace()
      val jackson = jackson() as JacksonYamlGradleConfig
      jackson.yamlFeature("LITERAL_BLOCK_STYLE", true)
      jackson.yamlFeature("MINIMIZE_QUOTES", true)
      jackson.yamlFeature("SPLIT_LINES", false)
    }

    kotlinGradle {
      target("**/*.gradle.kts")
      indentWithSpaces(2)
      endWithNewline()
      trimTrailingWhitespace()
      ktlint()
    }

    java {
      target("**/src/main/java/ **/*.java")
      importOrder()
      removeUnusedImports()
      indentWithSpaces(2)
      endWithNewline()
      trimTrailingWhitespace()
      prettier(prettierConfig)
        .config(
          mapOf("parser" to "java", "tabWidth" to 2, "useTabs" to false, "printWidth" to 100),
        )
    }
  }
}
