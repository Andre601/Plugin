package net.powerplugins.plugin.file;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public record PluginFile(String name, List<String> authors, String version, List<String> depends,
                         List<String> softdepends, String url, String description, PluginCategory category){
    
    public Builder createCopy(){
        return new Builder()
            .setName(name())
            .setAuthors(authors())
            .setVersion(version())
            .setDepends(depends())
            .setSoftDepends(softdepends())
            .setUrl(url())
            .setDescription(description())
            .setCategory(category.getName());
    }
    
    public boolean isNew(){
        return category() == PluginCategory.NOT_SET;
    }
    
    public boolean wasUpdated(String newVersion){
        return isNew() || !version().equalsIgnoreCase(newVersion);
    }
    
    public static class Builder{
        
        private String name = "";
        private List<String> authors = new ArrayList<>();
        private String version = "";
        private List<String> depends = new ArrayList<>();
        private List<String> softDepends = new ArrayList<>();
        private String url = "";
        private String description = "";
        private PluginCategory category = PluginCategory.NOT_SET;
        
        public Builder(){}
        
        public PluginFile setFromPlugin(Plugin plugin){
            return this.setName(plugin.getName())
                .setAuthors(plugin.getPluginMeta().getAuthors())
                .setVersion(plugin.getPluginMeta().getVersion())
                .setDepends(plugin.getPluginMeta().getPluginDependencies())
                .setSoftDepends(plugin.getPluginMeta().getPluginSoftDependencies())
                .build();
        }
        
        public Builder setName(String name){
            this.name = name;
            return this;
        }
        
        public Builder setAuthors(List<String> authors){
            this.authors = authors;
            return this;
        }
        
        public Builder setVersion(String version){
            this.version = version;
            return this;
        }
        
        public Builder setDepends(List<String> depends){
            this.depends = depends;
            return this;
        }
        
        public Builder setSoftDepends(List<String> softDepends){
            this.softDepends = softDepends;
            return this;
        }
        
        public Builder setUrl(String url){
            this.url = url;
            return this;
        }
        
        public Builder setDescription(String description){
            this.description = description;
            return this;
        }
        
        public Builder setCategory(String category){
            this.category = PluginCategory.fromString(category);
            return this;
        }
        
        public PluginFile build(){
            return new PluginFile(name, authors, version, depends, softDepends, url, description, category);
        }
    }
}
