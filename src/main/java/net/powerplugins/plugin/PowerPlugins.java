package net.powerplugins.plugin;

import net.powerplugins.plugin.commands.CmdPlugins;
import net.powerplugins.plugin.commands.CmdPowerPlugins;
import net.powerplugins.plugin.config.ConfigManager;
import net.powerplugins.plugin.file.PluginFile;
import net.powerplugins.plugin.file.PluginFileManager;
import net.powerplugins.plugin.listener.CommandListener;
import net.powerplugins.plugin.listener.ServerListener;
import net.powerplugins.plugin.webhooks.WebhookManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PowerPlugins extends JavaPlugin{
    
    private PluginFileManager pluginFileManager = null;
    private ConfigManager configManager = null;
    
    private CmdPlugins cmdPlugins = null;
    private CmdPowerPlugins cmdPowerPlugins = null;
    
    private WebhookManager webhookManager = null;
    
    @Override
    public void onEnable(){
        configManager = new ConfigManager(this);
        if(!configManager.loadConfig()){
            getLogger().warning("Couldn't load config.yml! Please see previous console entries for reasons.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        pluginFileManager = new PluginFileManager(this);
        
        if(configManager.getString("guild", "webhook") != null && !configManager.getString("guild", "webhook").isEmpty()) 
            webhookManager = new WebhookManager(this, configManager.getString("guild", "webhook"));
        
        getLogger().info("Loading Plugin info...");
        int loaded = pluginFileManager.loadFiles();
        if(loaded == -1){
            getLogger().warning("There was an issue while loading the plugin info.");
            getLogger().warning("Please check the previous console entries for any warnings.");
            
            getServer().getPluginManager().disablePlugin(this);
            return;
        }else
        if(loaded == 0){
            getLogger().info("No Plugin info has been loaded.");
        }else{
            getLogger().info("Loaded information of " + loaded + " plugin(s)!");
        }
        
        getLogger().info("Registering commands...");
        cmdPlugins = new CmdPlugins(this);
        cmdPowerPlugins = new CmdPowerPlugins(this);
        
        if(getServer().getCommandMap().register("powerplugins", cmdPlugins)){
            getLogger().info("Registered /plugins");
        }else{
            getLogger().info("Registered /powerplugins:plugins");
        }
        
        if(getServer().getCommandMap().register("powerplugins", cmdPowerPlugins)){
            getLogger().info("Registered /powerplugins");
        }else{
            getLogger().info("Registered /powerplugins:powerplugins");
        }
        
        getLogger().info("Register events...");
        new CommandListener(this);
        new ServerListener(this);
        getLogger().info("Events registered!");
        
        getLogger().info("Plugin ready! Waiting for server to finish loading...");
    }
    
    @Override
    public void onDisable(){
        getLogger().info("Unloading components of plugin...");
        this.pluginFileManager = null;
        getLogger().info("Unloaded PluginFileManager.");
        this.configManager = null;
        getLogger().info("Unloaded ConfigManager.");
        this.cmdPlugins = null;
        this.cmdPowerPlugins = null;
        getLogger().info("Unloaded PowerPlugins commands.");
        this.webhookManager = null;
        getLogger().info("Unloaded WebhookManager.");
        
        getLogger().info("Disabling complete. Good bye!");
    }
    
    public void checkPluginUpdates(){
        if(webhookManager == null){
            getLogger().warning("WebhookManager was not initialized. Skipping...");
            return;
        }
        
        Map<PluginFile, Boolean> toSend = new HashMap<>();
        for(Plugin plugin : getPlugins()){
            PluginFile pluginFile = pluginFileManager.getPlugin(plugin.getName());
            if(pluginFile == null){
                getLogger().warning("Couldn't retrieve nor create PluginFile for plugin " + plugin.getName());
                continue;
            }
            
            if(!pluginFile.wasUpdated(plugin.getPluginMeta().getVersion()))
                continue;
            
            // Plugin is not new but has been updated, so we have to update the file too.
            if(!pluginFile.isNew())
                pluginFile = pluginFileManager.updateFile(pluginFile, plugin);
            
            // updateFile may return null.
            if(pluginFile == null)
                continue;
            
            toSend.put(pluginFile, pluginFile.isNew());
        }
        
        webhookManager.sendEmbeds(toSend);
        
        getLogger().info("Plugin checks complete! Cleaning up WebhookManager...");
        
        webhookManager.clean();
    }
    
    public List<Plugin> getPlugins(){
        return Arrays.stream(getServer().getPluginManager().getPlugins())
            .sorted(Comparator.comparing(Plugin::getName))
            .filter(Plugin::isEnabled)
            .toList();
    }
    
    public ConfigManager getConfigManager(){
        return configManager;
    }
    
    public PluginFileManager getPluginFileManager(){
        return pluginFileManager;
    }
    
    public CmdPlugins getCmdPlugins(){
        return cmdPlugins;
    }
}
