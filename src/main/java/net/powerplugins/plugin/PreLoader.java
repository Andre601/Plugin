package net.powerplugins.plugin;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class PreLoader implements PluginLoader{
    @Override
    public void classloader(@NotNull PluginClasspathBuilder pluginClasspathBuilder){
        ComponentLogger logger = pluginClasspathBuilder.getContext().getLogger();;
        
        logger.info("Loading Libraries for plugin...");
        
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        
        resolver.addRepository(new RemoteRepository.Builder(
            "paper",
            "default",
            "https://repo.papermc.io/repository/maven-public/"
        ).build());
        
        logger.info("Loading club.minnced:discord-webhooks:0.8.2 from https://repo.papermc.io...");
        
        resolver.addDependency(new Dependency(new DefaultArtifact("club.minnced:discord-webhooks:0.8.2"), null));
        pluginClasspathBuilder.addLibrary(resolver);
        
        logger.info("Loading complete!");
    }
}
