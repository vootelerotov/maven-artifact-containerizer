package io.github.vootelerotov.artifact.testcontainer

import org.jboss.shrinkwrap.resolver.api.maven.Maven
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact

object ArtfifactTestContainer {

  fun fromArtifact(fullyQualifiedName: String): Pair<MavenResolvedArtifact, Int> {
    val resolver = Maven.configureResolver()
    val artifact = resolver
      .resolve(fullyQualifiedName)
      .withoutTransitivity()
      .asSingle(MavenResolvedArtifact::class.java)

    println(artifact.dependencies.size)
    val transitiveDependencies = resolver
      .resolve(artifact.dependencies.map { it.coordinate.toCanonicalForm() })
      .withoutTransitivity()
      .asResolvedArtifact()


    return artifact to transitiveDependencies.size
  }

}
