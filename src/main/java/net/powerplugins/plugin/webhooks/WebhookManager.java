package net.powerplugins.plugin.webhooks;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.google.common.collect.Lists;
import net.powerplugins.plugin.PowerPlugins;
import net.powerplugins.plugin.file.PluginFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebhookManager{
    
    private final PowerPlugins plugin;
    private final WebhookClient client;
    
    public WebhookManager(PowerPlugins plugin, String url){
        this.plugin = plugin;
        this.client = new WebhookClientBuilder(url).build();
    }
    
    public void sendEmbeds(Map<PluginFile, Boolean> toSend){
        if(toSend.isEmpty()){
            plugin.getLogger().info("No new or updated Plugins. Skipping Message sending...");
            return;
        }
        
        List<WebhookEmbed> embeds = new ArrayList<>();
        for(Map.Entry<PluginFile, Boolean> pluginFile : toSend.entrySet()){
            if(pluginFile.getValue() == null)
                continue;
            
            embeds.add(getEmbed(pluginFile.getKey(), pluginFile.getValue()));
            plugin.getLogger().info("Added " + pluginFile.getKey().name() + " to queue. Type: " + (pluginFile.getValue() ? "New Plugin" : "Update"));
        }
        
        List<List<WebhookEmbed>> embedSplits = Lists.partition(embeds, 10);
        for(int i = 0; i < embedSplits.size(); i++){
            String roleId = plugin.getConfigManager().getString("guild", "role");
            String tag;
            if(i == 0 && roleId != null){
                tag = "<@&" + roleId + ">";
            }else{
                tag = "\u200E";
            }
            
            List<WebhookEmbed> group = embedSplits.get(i);
            
            WebhookMessage message = new WebhookMessageBuilder()
                .setContent(tag)
                .addEmbeds(group)
                .build();
            
            plugin.getLogger().info("Sending Notification about plugin updates (Group " + (i + 1) + " of " + embedSplits.size() + ")...");
            
            client.send(message);
        }
    }
    
    public void clean(){
        client.close();
    }
    
    private WebhookEmbed getEmbed(PluginFile pluginFile, boolean isNew){
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
            .setColor(0xF39C12)
            .setTitle(new WebhookEmbed.EmbedTitle(
                (isNew ? "\uD83C\uDD95 " : "\uD83D\uDD04 ") + pluginFile.name(),
                null
            ));
        
        if(isNew){
            builder.setDescription(String.format(
                "Added `%s` to the Server!\n" +
                "Use `/pl info %s` on the MC server to find out more.",
                pluginFile.name(),
                pluginFile.name()
            ));
        }else{
            builder.setDescription(String.format(
                "`%s` has been updated to Version `%s`",
                pluginFile.name(),
                pluginFile.version()
            )).addField(new WebhookEmbed.EmbedField(
                false,
                "Plugin Page:",
                pluginFile.url()
            ));
        }
        
        builder.addField(new WebhookEmbed.EmbedField(
            false,
            "Plugin Author(s):",
            getAuthors(pluginFile.authors())
        ));
        
        return builder.build();
    }
    
    private String getAuthors(List<String> authors){
        if(authors.isEmpty())
            return "Unknown";
        
        if(authors.size() == 1)
            return authors.get(0);
        
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < authors.size(); i++){
            String author = authors.get(i);
            
            if(builder.length() + author.length() + 20 > 1024){
                int entriesLeft = authors.size() - i;
                
                builder.append(" *and ")
                    .append(entriesLeft)
                    .append(" more...*");
                
                break;
            }
            
            if(builder.length() > 0)
                builder.append(", ");
            
            builder.append(author);
        }
        
        builder.replace(builder.lastIndexOf(","), builder.lastIndexOf(",") + 1, " and");
        
        return builder.toString();
    }
}
