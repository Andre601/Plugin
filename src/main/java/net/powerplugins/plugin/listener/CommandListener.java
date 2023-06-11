package net.powerplugins.plugin.listener;

import net.powerplugins.plugin.PowerPlugins;
import net.powerplugins.plugin.commands.CmdPlugins;
import net.powerplugins.plugin.utils.ParseUtil;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.*;

public class CommandListener implements Listener{
    
    private final PowerPlugins plugin;
    
    public static final String[] subCommands = new String[]{"free", "info", "premium", "private"};
    
    public CommandListener(PowerPlugins plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event){
        final String msg = event.getMessage().toLowerCase(Locale.ROOT).substring(1);
        if(!msg.startsWith("pl") && !msg.startsWith("plugins"))
            return;
        
        event.setCancelled(true);
        
        final String[] cmd = msg.split("\\s", 3);
        String[] args = new String[0];
        if(cmd.length > 1)
            args = Arrays.copyOfRange(cmd, 1, cmd.length);
        
        Command command = plugin.getServer().getCommandMap().getCommand("powerplugins:plugins");
        if(command == null){
            event.getPlayer().sendMessage(ParseUtil.parse("<red>Unable to process the command!"));
            return;
        }
        
        final CmdPlugins cmdPowerPlugins = plugin.getCmdPlugins();
        cmdPowerPlugins.execute(event.getPlayer(), command.getLabel(), args);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(TabCompleteEvent event){
        String cmd = event.getBuffer().toLowerCase(Locale.ROOT);
        if(!cmd.startsWith("/pl") && !cmd.startsWith("/plugins"))
            return;
        
        String[] split = cmd.split("\\s", 2);
        if(split.length == 1)
            return;
        
        List<String> suggestions = plugin.getServer().getCommandMap().tabComplete(event.getSender(), "powerplugins:plugins " + split[1]);
        if(suggestions == null)
            return;
        
        event.setCompletions(suggestions);
    }
    
    public static List<String> partialMatches(String token, List<String> originals){
        if(originals == null || originals.isEmpty())
            return Collections.emptyList();
        
        if(token == null || token.isEmpty())
            return originals;
        
        List<String> matches = new ArrayList<>();
        for(String str : originals){
            if(str.length() >= token.length() && str.regionMatches(true, 0, token, 0, token.length()))
                matches.add(str);
        }
        
        return matches;
    }
}
