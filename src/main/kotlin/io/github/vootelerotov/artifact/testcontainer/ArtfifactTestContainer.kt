package io.github.vootelerotov.artifact.testcontainer

import org.jboss.shrinkwrap.resolver.api.maven.Maven
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact

object ArtfifactTestContainer {

  fun fromArtifact(fullyQualifiedName: String): MavenResolvedArtifact {
    val artifact = Maven.configureResolver()
      .resolve(fullyQualifiedName)
      .withoutTransitivity()
      .asSingle(MavenResolvedArtifact::class.java)
    return artifact
  }

}
