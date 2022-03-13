package io.github.vootelerotov.artifact.testcontainer.resolver

import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem
import org.jboss.shrinkwrap.resolver.api.maven.Maven

class DefaultResolverProvider: ResolverProvider {
  override fun resolver(): ConfigurableMavenResolverSystem = Maven.configureResolver()
}
