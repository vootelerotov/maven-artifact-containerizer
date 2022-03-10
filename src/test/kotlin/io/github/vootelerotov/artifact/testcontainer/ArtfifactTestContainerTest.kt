package io.github.vootelerotov.artifact.testcontainer

import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File
import java.nio.file.Path
import java.time.Duration

internal class ArtfifactTestContainerTest {

  @Nested
  @TestInstance(PER_CLASS)
  inner class RunnableJar {

    @BeforeAll
    fun publishRunnableJarToMavenLocal() { // io.github.vootelerotov.test.projects:main-class-jar:1.0-SNAPSHOT
      publishToMavenLocal(Path.of("test-projects", "main-class-jar", "pom.xml").toFile())
    }

    @Test
    fun fromArtifactInLocalMaven() {
      val container = ArtfifactTestContainer.fromArtifact(
        "io.github.vootelerotov.test.projects:main-class-jar:1.0-SNAPSHOT"
      ).build()
        .withLogConsumer { println(it.utf8String) }
        .waitingFor(Wait.forLogMessage(".*Started: nothing!.*", 1).withStartupTimeout(Duration.ofSeconds(1)))

      container.use {
        container.start() // Not asserting anything, relying on the #waitingFor to throw an error when Started! is not present
      }
    }

    @Test
    fun withArguments() {
      val container = ArtfifactTestContainer.fromArtifact(
        "io.github.vootelerotov.test.projects:main-class-jar:1.0-SNAPSHOT"
      ).withArgs("to", "test", "this").build()
        .withLogConsumer { println(it.utf8String) }
        .waitingFor(Wait.forLogMessage(".*Started: to test this!.*", 1).withStartupTimeout(Duration.ofSeconds(1)))

      container.use {
        container.start() // Not asserting anything, relying on the #waitingFor to throw an error when Started! is not present
      }
    }

  }

  @Nested
  @TestInstance(PER_CLASS)
  inner class JarWithDependencies {

    @BeforeAll
    fun publishJarWithDependenciesToMavenLocal() { // io.github.vootelerotov.test.projects:jar-with-dependencies:1.0-SNAPSHOT
      publishToMavenLocal(Path.of("test-projects", "jar-with-dependencies", "pom.xml").toFile())
    }

    @Test
    fun fromArtifactInLocalMaven() {
      val container = ArtfifactTestContainer.fromArtifact(
        "io.github.vootelerotov.test.projects:jar-with-dependencies:1.0-SNAPSHOT"
      )
        .withClassName("io.github.vootelerotov.jar.with.dependencies.Main").build()
        .withLogConsumer { println(it.utf8String) }
        .waitingFor(Wait.forLogMessage(".*Started: nothing!.*", 1).withStartupTimeout(Duration.ofSeconds(1)))

      container.use {
        container.start() // Not asserting anything, relying on the #waitingFor to throw an error when Started! is not present
      }
    }

    @Test
    fun withArguments() {
      val builder = ArtfifactTestContainer.fromArtifact(
        "io.github.vootelerotov.test.projects:jar-with-dependencies:1.0-SNAPSHOT"
      )
        .withClassName("io.github.vootelerotov.jar.with.dependencies.Main")
        .withArgs("to", "test")

      val container = builder.build()
        .withLogConsumer { println(it.utf8String) }
        .waitingFor(Wait.forLogMessage(".*Started: to test!.*", 1).withStartupTimeout(Duration.ofSeconds(1)))

      container.use {
        container.start() // Not asserting anything, relying on the #waitingFor to throw an error when Started! is not present
      }
    }

  }

  private fun publishToMavenLocal(pom: File) {
    EmbeddedMaven.forProject(pom).setGoals("clean", "install").build()
  }
}
