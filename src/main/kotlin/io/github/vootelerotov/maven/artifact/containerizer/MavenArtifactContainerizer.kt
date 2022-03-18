package io.github.vootelerotov.maven.artifact.containerizer

import io.github.vootelerotov.maven.artifact.containerizer.resolver.ResolverCreator
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact

class MavenArtifactContainerizer(repositoryConfig: RepositoryConfig) {

  private val resolver: ConfigurableMavenResolverSystem;

  init {
    resolver = ResolverCreator().createResolver(repositoryConfig)
  }

  companion object MavenArtifactTestcontainerizer {

    fun fromArtifact(fullyQualifiedName: String): JavaContainerBuilder =
      MavenArtifactContainerizer(RepositoryConfig.DEFAULT).fromArtifact(fullyQualifiedName)

    fun fromArtifact(repositoryConfig: RepositoryConfig, fullyQualifiedArtifactName: String): JavaContainerBuilder =
      MavenArtifactContainerizer(repositoryConfig).fromArtifact(fullyQualifiedArtifactName)

  }

  fun fromArtifact(fullyQualifiedName: String): JavaContainerBuilder {
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