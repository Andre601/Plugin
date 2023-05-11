package net.powerplugins.plugin.commands;

import net.powerplugins.plugin.PowerPlugins;
import net.powerplugins.plugin.utils.ParseUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class CmdPowerPlugins extends Command{
    
    private final PowerPlugins plugin;
    
    public CmdPowerPlugins(PowerPlugins plugin){
        super("powerplugins", "Command for managing the plugin", "/powerplugins reload", Collections.emptyList());
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args){
        if((sender instanceof Player player) && !player.hasPermission("powerplugins.admin")){
            player.sendMessage(ParseUtil.parse("<red>You don't have the required permissions to use this command."));
            return true;
        }
        
        if(args.length == 0 || !args[0].equalsIgnoreCase("reload")){
            sender.sendMessage(ParseUtil.parse("<red>Invalid command usage! Use <grey>/powerplugins reload</grey>!"));
            return true;
        }
        
        sender.sendMessage(ParseUtil.parse("<grey>Reloading Plugin files and config.yml..."));
        int loaded = plugin.getPluginFileManager().loadFiles();
        if(loaded == -1){
            sender.sendMessage(ParseUtil.parse("<red>There was an issue while loading the plugin files. Please see console."));
            return true;
        }else
        if(loaded == 0){
            sender.sendMessage(ParseUtil.parse("<green>No plugin files have been loaded."));
        }else{
            sender.sendMessage(ParseUtil.parse("<green>Successfully loaded " + loaded + " plugin file(s)!"));
        }
        
        if(plugin.getConfigManager().loadConfig()){
            sender.sendMessage(ParseUtil.parse("<green>Successfully reloaded config.yml!"));
        }else{
            sender.sendMessage(ParseUtil.parse("<red>There was an issue while reloading the config.yml. Please see console."));
            return true;
        }
        
        sender.sendMessage(ParseUtil.parse("<green>Reload completed successfully!"));
        return true;
    }
}
