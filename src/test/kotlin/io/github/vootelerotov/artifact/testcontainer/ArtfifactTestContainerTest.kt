package io.github.vootelerotov.artifact.testcontainer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ArtfifactTestContainerTest {

  @Test
  fun fromArtifact() {
    val (artifact, dependencyCount) = ArtfifactTestContainer.fromArtifact("org.antlr:antlr4:4.9.3")
    assertThat(artifact.coordinate.artifactId).isEqualTo("antlr4")
    assertThat(dependencyCount).isEqualTo(6)
  }
}
