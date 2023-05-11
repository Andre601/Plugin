package net.powerplugins.plugin.file;

import java.util.Locale;

public enum PluginCategory{
    
    FREE("<green>Free</green>"),
    PREMIUM("<gold>Premium</gold>"),
    PRIVATE("<grey>Private</grey>"),
    NOT_SET("<red>Not set</red>");
    
    private final String displayName;
    
    PluginCategory(String displayName){
        this.displayName = displayName;
    }
    
    public static PluginCategory fromString(String category){
        for(PluginCategory cat : values()){
            if(cat.name().equalsIgnoreCase(category))
                return cat;
        }
        
        return PluginCategory.NOT_SET;
    }
    
    public String getDisplayName(){
        return displayName;
    }
    
    public String getName(){
        return this.name().toLowerCase(Locale.ROOT);
    }
}
