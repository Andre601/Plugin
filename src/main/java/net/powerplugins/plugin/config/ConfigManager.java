package net.powerplugins.plugin.config;

import net.powerplugins.plugin.PowerPlugins;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Logger;

public class ConfigManager{
    
    private final PowerPlugins plugin;
    private final File config;
    
    private ConfigurationNode node;
    
    public ConfigManager(PowerPlugins plugin){
        this.plugin = plugin;
        this.config = new File(plugin.getDataFolder(), "config.yml");
    }
    
    public boolean loadConfig(){
        plugin.getLogger().info("Loading config.yml...");
        
        File pluginFolder = config.getParentFile();
        if(!pluginFolder.exists() && !pluginFolder.mkdirs()){
            plugin.getLogger().warning("Unable to create folder for plugin. Missing permissions?");
            return false;
        }
        
        if(!config.exists()){
            try(InputStream stream = plugin.getClass().getResourceAsStream("/config.yml")){
                if(stream == null){
                    plugin.getLogger().warning("Unable to extract config.yml from Plugin.");
                    return false;
                }
                
                Files.copy(stream, config.toPath());
            }catch(IOException ex){
                plugin.getLogger().warning("Encountered IOException while creating config.yml...");
                plugin.getLogger().warning("Cause: " + ex.getMessage());
                return false;
            }
        }
        
        return reloadConfig();
    }
    
    public boolean reloadConfig(){
        return (node = createNode()) != null;
    }
    
    public String getString(Object... path){
        return node.node(path).getString("");
    }
    
    private ConfigurationNode createNode(){
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
            .file(config)
            .build();
        
        try{
            return loader.load();
        }catch(IOException ex){
            return null;
        }
    }
}
