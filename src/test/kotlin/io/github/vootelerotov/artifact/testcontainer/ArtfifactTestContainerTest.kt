package io.github.vootelerotov.artifact.testcontainer

import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven
import org.junit.jupiter.api.Test
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File
import java.nio.file.Path
import java.time.Duration

internal class ArtfifactTestContainerTest {

  @Test
  fun runnableJarFromArtifactInLocalMaven() {
    publishToMavenLocal(Path.of("test-projects", "main-class-jar","pom.xml").toFile())

    val container = ArtfifactTestContainer.fromArtifact(
      "io.github.vootelerotov.test.projects:main-class-jar:1.0-SNAPSHOT"
    ).build()
      .withLogConsumer { println(it.utf8String) }
      .waitingFor(Wait.forLogMessage(".*Started!.*", 1).withStartupTimeout(Duration.ofSeconds(1)))

    container.use {
      container.start() // Not asserting anything, relying on the #waitingFor to throw an error when Started! is not present
    }
  }

  @Test
  fun jarWithDependencyFromArtifactInLocalMaven() {
    publishToMavenLocal(Path.of("test-projects", "jar-with-dependencies","pom.xml").toFile())

    val container = ArtfifactTestContainer.fromArtifact(
      "io.github.vootelerotov.test.projects:jar-with-dependencies:1.0-SNAPSHOT"
    ).withClassName("io.github.vootelerotov.jar.with.dependencies.Main").build()
      .withLogConsumer { println(it.utf8String) }
      .waitingFor(Wait.forLogMessage(".*Started!.*", 1).withStartupTimeout(Duration.ofSeconds(1)))

    container.use {
      container.start() // Not asserting anything, relying on the #waitingFor to throw an error when Started! is not present
    }
  }

  private fun publishToMavenLocal(pom: File) {
    EmbeddedMaven.forProject(pom).setGoals("clean", "install").build()
  }
}
