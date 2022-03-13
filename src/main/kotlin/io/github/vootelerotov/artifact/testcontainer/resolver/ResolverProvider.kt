package io.github.vootelerotov.artifact.testcontainer.resolver

import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem

interface ResolverProvider {

  fun resolver(): ConfigurableMavenResolverSystem

}
