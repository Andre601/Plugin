package net.powerplugins.plugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ParseUtil{
    
    private static final MiniMessage mm = MiniMessage.miniMessage();
    
    public static Component parse(String msg){
        return mm.deserialize(msg);
    }
}
