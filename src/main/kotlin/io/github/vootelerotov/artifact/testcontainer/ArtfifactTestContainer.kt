package io.github.vootelerotov.artifact.testcontainer

import org.jboss.shrinkwrap.resolver.api.maven.Maven
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact

object ArtfifactTestContainer {

  fun fromArtifact(fullyQualifiedName: String): JavaContainerBuilder {
    val resolver = Maven.configureResolver()
    val artifact = resolver
      .resolve(fullyQualifiedName)
      .withoutTransitivity()
      .asSingle(MavenResolvedArtifact::class.java)

    val artifactFile = artifact.asFile()

    val dependencyCoordinates = artifact.dependencies.map { it.coordinate.toCanonicalForm() }

    val transitiveDependencies = if (dependencyCoordinates.isEmpty()) arrayOf() else resolver
      .resolve(dependencyCoordinates)
      .withoutTransitivity()
      .asResolvedArtifact()

    val dependencyFiles = transitiveDependencies.map { it.asFile() }

    return JavaContainerBuilder(artifactFile, dependencyFiles)
  }

}
