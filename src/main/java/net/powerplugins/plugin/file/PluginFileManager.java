package net.powerplugins.plugin.file;

import net.powerplugins.plugin.PowerPlugins;
import net.powerplugins.plugin.serializers.PluginFileSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class PluginFileManager{
    
    private final Map<String, PluginFile> plugins = new HashMap<>();
    
    private final File pluginsFolder;
    private final Logger logger;
    
    public PluginFileManager(PowerPlugins plugin){
        this.pluginsFolder = new File(plugin.getDataFolder(), "plugins");
        this.logger = plugin.getLogger();
    }
    
    public int loadFiles(){
        plugins.clear();
        
        if(!pluginsFolder.exists() && !pluginsFolder.mkdirs()){
            logger.warning("Unable to create plugins folder in PowerPlugins directory. Missing permissions?");
            return -1;
        }
        
        if(!pluginsFolder.isDirectory()){
            logger.warning("/plugins/PowerPlugins/plugins is not a directory!");
            return -1;
        }
        
        File[] files = pluginsFolder.listFiles(((dir, name) -> name.endsWith(".yml")));
        if(files == null || files.length == 0){
            logger.info("No valid YAML files present in plugins folder.");
            return 0;
        }
        
        for(File file : files){
            YamlConfigurationLoader loader = getLoader(file);
            
            try{
                ConfigurationNode node = loader.load();
                
                if(node == null)
                    continue;
                
                PluginFile plugin = node.get(PluginFile.class);
                if(plugin == null || plugin.name() == null){
                    logger.warning("Plugin file " + file.getName() + " is null or contains a name with value null!");
                    continue;
                }
                
                plugins.put(plugin.name().toLowerCase(Locale.ROOT), plugin);
            }catch(IOException ex){
                logger.warning("Unable to load YAML file " + file.getName() + " as PluginFile.");
                logger.warning("Cause: " + ex.getMessage());
            }
        }
        
        return plugins.size();
    }
    
    public PluginFile updateFile(PluginFile pluginFile, Plugin plugin){
        File file = new File(pluginsFolder, plugin.getName().toLowerCase(Locale.ROOT) + ".yml");
        
        YamlConfigurationLoader loader = getLoader(file);
        try{
            ConfigurationNode node = loader.load();
            
            PluginFile newPluginFile = pluginFile.createCopy()
                .setAuthors(plugin.getPluginMeta().getAuthors())
                .setVersion(plugin.getPluginMeta().getVersion())
                .setDepends(plugin.getPluginMeta().getPluginDependencies())
                .setSoftDepends(plugin.getPluginMeta().getPluginSoftDependencies())
                .build();
            
            saveFile(newPluginFile, loader, node);
            
            plugins.put(plugin.getName().toLowerCase(Locale.ROOT), newPluginFile);
            
            return newPluginFile;
        }catch(IOException ex){
            logger.warning("Encountered IOException while updating a plugin YAML file.");
            logger.warning("Cause: " + ex.getMessage());
            
            return null;
        }
    }
    
    public PluginFile createPluginFile(@Nullable Plugin plugin){
        if(plugin == null)
            return null;
        
        File file = new File(pluginsFolder, plugin.getName().toLowerCase(Locale.ROOT) + ".yml");
        YamlConfigurationLoader loader = getLoader(file);
        
        try{
            ConfigurationNode node = loader.load();
            PluginFile pluginFile = new PluginFile.Builder().setFromPlugin(plugin);
            
            if(!saveFile(pluginFile, loader, node))
                return null;
            
            plugins.put(plugin.getName().toLowerCase(Locale.ROOT), pluginFile);
            
            return pluginFile;
        }catch(IOException ex){
            logger.warning("Encountered IOException while creating a plugin YAML file.");
            logger.warning("Cause: " + ex.getMessage());
            
            return null;
        }
    }
    
    private boolean saveFile(PluginFile pluginFile, YamlConfigurationLoader loader, ConfigurationNode node){
        try{
            node.set(PluginFile.class, pluginFile);
            
            loader.save(node);
            return true;
        }catch(IOException ex){
            logger.warning("Encountered IOException while saving a PluginFile.");
            logger.warning("Cause: " + ex.getMessage());
            
            return false;
        }
    }
    
    public PluginFile getPlugin(String name){
        PluginFile pluginFile = plugins.get(name.toLowerCase(Locale.ROOT));
        
        if(pluginFile == null)
            return createPluginFile(Bukkit.getPluginManager().getPlugin(name));
        
        return pluginFile;
    }
    
    private YamlConfigurationLoader getLoader(File file){
        return YamlConfigurationLoader.builder()
            .defaultOptions(opt -> opt.serializers(builder -> builder.register(PluginFile.class, PluginFileSerializer.INSTANCE)))
            .file(file)
            .build();
    }
}
