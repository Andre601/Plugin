package net.powerplugins.plugin.listener;

import net.powerplugins.plugin.PowerPlugins;
import net.powerplugins.plugin.placeholders.PAPIPlaceholders;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerListener implements Listener{
    
    private final PowerPlugins plugin;
    
    public ServerListener(PowerPlugins plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onServerLoad(ServerLoadEvent event){
        plugin.getLogger().info("Server loaded. Starting Plugin checks...");
        plugin.checkPluginUpdates();
        
        if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            plugin.getLogger().info("Registering PlaceholderAPI expansion...");
            new PAPIPlaceholders(plugin);
            plugin.getLogger().info("Placeholder Expansion registered!");
        }
    }
}
