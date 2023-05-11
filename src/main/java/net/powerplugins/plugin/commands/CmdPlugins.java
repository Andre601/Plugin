package net.powerplugins.plugin.commands;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.powerplugins.plugin.PowerPlugins;
import net.powerplugins.plugin.file.PluginCategory;
import net.powerplugins.plugin.file.PluginFile;
import net.powerplugins.plugin.listener.CommandListener;
import net.powerplugins.plugin.utils.MiniMessageBuilder;
import net.powerplugins.plugin.utils.ParseUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CmdPlugins extends Command implements TabCompleter{
    
    private final String brandColour = "#F39C12";
    
    private final PowerPlugins plugin;
    
    public CmdPlugins(PowerPlugins plugin){
        super(
            "plugins",
            "Shows the plugins running on this server.",
            "/pl [free|premium|private|info <plugin>",
            Collections.singletonList("pl")
        );
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args){
        if(!(sender instanceof Player player)){
            sender.sendMessage(ParseUtil.parse("<red>This command can only be executed by a Player."));
            return true;
        }
        
        if(args.length == 0){
            clear(player);
            
            player.sendMessage(pluginCategories());
            return true;
        }else
        if(args[0].equalsIgnoreCase("info")){
            if(args.length == 1){
                player.sendMessage(ParseUtil.parse("<red>Please provide the name of a plugin."));
                return true;
            }
            
            PluginFile pluginFile = plugin.getPluginFileManager().getPlugin(args[1]);
            if(pluginFile == null){
                player.sendMessage(ParseUtil.parse("<red>A plugin with the name <grey>" + args[1] + "</grey> doesn't exist!"));
                return true;
            }
            
            Component pluginInfo = detailedPluginInfo(pluginFile);
            if(pluginInfo == null){
                player.sendMessage(ParseUtil.parse("<red>Unable to retrieve Info for plugin <grey>" + args[1] + "</grey>. Was it not set up?"));
                return true;
            }
            
            clear(player);
            player.sendMessage(pluginInfo);
            return true;
        }else{
            PluginCategory category = PluginCategory.fromString(args[0]);
            if(category == PluginCategory.NOT_SET){
                player.sendMessage(ParseUtil.parse("<red>Unknown Category <grey>" + args[0] + "</grey>."));
                return true;
            }
            
            List<Component> pages = getPages(category);
            if(pages.isEmpty()){
                player.sendMessage(ParseUtil.parse("<red>The provided category does not contain any plugins."));
                return true;
            }
            
            if(args.length == 1){
                clear(player);
                player.sendMessage(pages.get(0));
            }else{
                int page;
                try{
                    page = Integer.parseInt(args[1]);
                }catch(NumberFormatException ex){
                    player.sendMessage(ParseUtil.parse("<red>Invalid argument! Expected number but received <grey>" + args[1] + "</grey>."));
                    return true;
                }
                
                if(page < 1){
                    if(pages.size() == 1){
                        player.sendMessage(ParseUtil.parse("<red>Invalid Number! Value needs to be 1."));
                    }else{
                        player.sendMessage(ParseUtil.parse("<red>Invalid Number! Value needs to be between 1 and " + pages.size() + "."));
                    }
                    
                    return true;
                }
                
                if(page > pages.size()){
                    player.sendMessage(ParseUtil.parse("<red>Number is too high! Only allowed are values between 1 and " + pages.size() + "."));
                }else{
                    clear(player);
                    player.sendMessage(pages.get(page - 1));
                }
                
            }
            return true;
        }
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args){
        return switch(args.length){
            case 0 -> List.of(CommandListener.subCommands);
            case 1 -> CommandListener.partialMatches(args[0], List.of(CommandListener.subCommands));
            case 2 -> {
                if(!args[0].equalsIgnoreCase("info"))
                    yield Collections.emptyList();
                
                List<String> plugins = plugin.getPlugins().stream()
                    .map(Plugin::getName)
                    .toList();
                
                yield CommandListener.partialMatches(args[1], plugins);
            }
            default -> Collections.emptyList();
        };
    }
    
    private void clear(Player player){
        for(int i = 0; i < 50; i++){
            player.sendMessage(Component.text());
        }
    }
    
    private List<PluginFile> getPlugins(PluginCategory category){
        return plugin.getPlugins().stream()
            .map(pl -> plugin.getPluginFileManager().getPlugin(pl.getName()))
            .filter(Objects::nonNull)
            .filter(pl -> pl.category() == category)
            .toList();
    }
    
    private List<Component> getPages(PluginCategory category){
        List<List<PluginFile>> pluginFilePages = Lists.partition(getPlugins(category), 7);
        List<Component> pages = new ArrayList<>();
        
        int currentPage = 0;
        final int totalPages = pluginFilePages.size();
        
        for(List<PluginFile> pluginFiles : pluginFilePages){
            currentPage++;
            
            MiniMessageBuilder builder = MiniMessageBuilder.get()
                .append(getPrevButton(category, currentPage))
                .append(getHeader(category))
                .append(getNextButton(category, currentPage, totalPages));
            
            for(PluginFile pluginFile : pluginFiles){
                builder.newline()
                    .append(simplePluginInfo(pluginFile));
            }
            
            if(pluginFiles.size() < 7){
                int padding = pluginFiles.size();
                
                while(padding < 7){
                    padding++;
                    
                    builder.newline();
                }
            }
            
            builder.append(getPrevButton(category, currentPage))
                .append(getFooter(currentPage))
                .append(getNextButton(category, currentPage, totalPages));
            
            pages.add(builder.toComponent());
        }
        
        return pages;
    }
    
    private Component pluginCategories(){
        return MiniMessageBuilder.get()
            .append(getFooter(0)).newline()
            .newline()
            .append("<grey>")
            .append("Please click on a category").newline()
            .newline()
            .append(
                MiniMessageBuilder.get()
                    .append("[")
                    .append("Free", "green")
                    .append("]"),
                "<grey>Plugins you can download for free.",
                "/plugins free"
            ).newline()
            .append(
                MiniMessageBuilder.get()
                    .append("[")
                    .append("Premium", "gold")
                    .append("]"),
                "<grey>Plugins you have to pay for to download.",
                "/plugins premium"
            ).newline()
            .append(
                "[Private]",
                "<grey>Plugins specifically made for this server.",
                "/plugins private"
            ).newline()
            .newline()
            .append(getFooter(0))
            .toComponent();
    }
    
    private MiniMessageBuilder simplePluginInfo(PluginFile pluginFile){
        String author = pluginFile.authors().isEmpty() ? "<italic>Unknown</italic>" : pluginFile.authors().get(0);
        String description = pluginFile.description().length() > 30 ? pluginFile.description().substring(0, 25) + "..." : pluginFile.description();
        
        return MiniMessageBuilder.get()
            .append(
                MiniMessageBuilder.get()
                    .append(pluginFile.name(), brandColour)
                    .append("<grey>")
                    .append(" - ")
                    .append(author, "white")
                    .append(" [")
                    .append(pluginFile.version(), brandColour)
                    .append("]"),
                description,
                "/plugins info " + pluginFile.name()
            );
    }
    
    private Component detailedPluginInfo(PluginFile pluginFile){
        if(pluginFile.category() == PluginCategory.NOT_SET)
            return null;
        
        return MiniMessageBuilder.get()
            .append(
                MiniMessageBuilder.get()
                    .append("<grey>")
                    .append("[")
                    .append("<", brandColour)
                    .append("]"),
                "<grey>Back to category " + pluginFile.category().getDisplayName(),
                "/plugins " + pluginFile.category().getName()
            ).append(getHeader(pluginFile.category()))
            .append(getNextButton(pluginFile.category())).newline()
            .append(pluginFile.name(), brandColour)
            .append(" [")
            .append(pluginFile.version(), "white")
            .append("]").newline()
            .newline()
            .append("<grey>")
            .append("Authors: ")
            .append(getAuthors(pluginFile.authors()), "white").newline()
            .newline()
            .append(getDependencies(pluginFile.depends(), pluginFile.softdepends())).newline()
            .newline()
            .append("Plugin Page: ")
            .append(
                MiniMessageBuilder.get().append(pluginFile.url(), brandColour),
                "<grey>Click to open Link.",
                pluginFile.url()
            ).newline()
            .append(
                MiniMessageBuilder.get()
                    .append("<grey>")
                    .append("[")
                    .append("<", brandColour)
                    .append("]"),
                "<grey>Back to category " + pluginFile.category().getDisplayName(),
                "/plugins " + pluginFile.category().getName()
            )
            .append(getHeader(pluginFile.category()))
            .append(getNextButton(pluginFile.category()))
            .toComponent();
    }
    
    private MiniMessageBuilder getHeader(PluginCategory category){
        return MiniMessageBuilder.get()
            .append(
                MiniMessageBuilder.get()
                    .append("<st>--------------</st>[ ")
                    .append(category.getDisplayName())
                    .append(" ]<st>--------------</st>"),
                "grey"
            );
    }
    
    private MiniMessageBuilder getFooter(int page){
        if(page == 0){
            return MiniMessageBuilder.get().append("<st>-------------------------------------</st>", "grey");
        }else{
            return MiniMessageBuilder.get()
                .append(
                    MiniMessageBuilder.get()
                        .append("<st>--------------</st>[ ")
                        .append("Page " + page, brandColour)
                        .append(" ]<st>--------------</st>"),
                    "grey"
                );
        }
    }
    
    private MiniMessageBuilder getPrevButton(PluginCategory category, int page){
        if(page > 1){
            return MiniMessageBuilder.get()
                .append(
                    MiniMessageBuilder.get()
                        .append("<grey>")
                        .append("[")
                        .append("<", brandColour)
                        .append("]"),
                    "<grey>Go to page " + (page - 1) + ".",
                    "/plugins " + category.getName() + " " + (page - 1)
                );
        }else{
            return MiniMessageBuilder.get()
                .append(
                    MiniMessageBuilder.get()
                        .append("<grey>")
                        .append("[")
                        .append("<", brandColour)
                        .append("]"),
                    "<grey>Go back to Selection.",
                    "/plugins"
                );
        }
    }
    
    private MiniMessageBuilder getNextButton(PluginCategory category){
        return getNextButton(category, 0, 0);
    }
    
    private MiniMessageBuilder getNextButton(PluginCategory category, int page, int maxPages){
        if(page < maxPages){
            return MiniMessageBuilder.get()
                .append(
                    MiniMessageBuilder.get()
                        .append("<grey>")
                        .append("[")
                        .append(">", brandColour)
                        .append("]"),
                    "<grey>Go to page " + (page + 1) + ".",
                    "/plugins " + category.getName() + " " + (page + 1)
                );
        }else{
            return MiniMessageBuilder.get().append("[>]");
        }
    }
    
    private MiniMessageBuilder getAuthors(List<String> authors){
        MiniMessageBuilder builder = MiniMessageBuilder.get();
        if(authors.isEmpty())
            return builder.append("Unknown", "italic");
        
        if(authors.size() == 1)
            return builder.append(authors.get(0));
        
        for(String author : authors){
            if(builder.length() > 1)
                builder.append(", ", "grey");
            
            builder.append(author);
        }
        
        builder.replaceLast(",", " and");
        
        return builder;
    }
    
    private MiniMessageBuilder getDependencies(List<String> depends, List<String> softDepends){
        Map<String, Boolean> dependencies = new HashMap<>();
        
        for(String depend : depends){
            dependencies.put(depend, true);
        }
        
        for(String softDepend : softDepends){
            dependencies.put(softDepend, false);
        }
        
        MiniMessageBuilder builder = MiniMessageBuilder.get()
            .newline()
            .append("Dependencies:");
        
        if(dependencies.isEmpty()){
            builder.newline()
                .append("<italic>No Dependencies</italic>", "grey");
        }else{
            for(Map.Entry<String, Boolean> dependency : dependencies.entrySet()){
                builder.newline()
                    .append(
                        MiniMessageBuilder.get()
                            .append("- ", "grey")
                            .append(dependency.getKey(), "white"), 
                        MiniMessageBuilder.get()
                            .append("<grey>")
                            .append("Type: ")
                            .append(dependency.getValue() ? "Depend" : "Soft Depend", brandColour)
                            .append(" [")
                            .append((dependency.getValue() ? 
                                MiniMessageBuilder.get().append("Required", "red") : 
                                MiniMessageBuilder.get().append("Optional", "green")
                            ))
                            .append("]").newline()
                            .newline()
                            .append("Click to see detailed Plugin information (If available).").newline()
                            .append("Note:", brandColour)
                            .append(" The type of Dependency is based on the plugin's plugin.yml file.")
                            .toString(), 
                        "/plugins info " + dependency.getKey()
                    );
            }
        }
        
        return builder;
    }
}
