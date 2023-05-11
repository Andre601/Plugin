package net.powerplugins.plugin.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.powerplugins.plugin.PowerPlugins;
import net.powerplugins.plugin.file.PluginFile;
import net.powerplugins.plugin.utils.ParseUtil;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class PAPIPlaceholders extends PlaceholderExpansion{
    
    private final PowerPlugins plugin;
    
    public PAPIPlaceholders(PowerPlugins plugin){
        this.plugin = plugin;
        this.register();
    }
    
    @Override
    public @NotNull String getIdentifier(){
        return "plugins";
    }
    
    @Override
    public @NotNull String getAuthor(){
        return String.join(", ", plugin.getPluginMeta().getAuthors());
    }
    
    @Override
    public @NotNull String getVersion(){
        return plugin.getPluginMeta().getVersion();
    }
    
    @Override
    public boolean persist(){
        return true;
    }
    
    @Override
    public String onRequest(OfflinePlayer player, String placeholder){
        String[] parts = placeholder.split("_", 2);
        if(parts.length == 1)
            return null;
        
        PluginFile pluginFile = plugin.getPluginFileManager().getPlugin(parts[0]);
        if(pluginFile == null)
            return "";
        
        return switch(parts[1].toLowerCase(Locale.ROOT)){
            case "name" -> pluginFile.name();
            case "description" -> pluginFile.description().length() > 20 ? pluginFile.description().substring(0, 15) + "..." : pluginFile.description();
            case "category" -> ParseUtil.toLegacy(pluginFile.category().getDisplayName());
            default -> null;
        };
    }
}
