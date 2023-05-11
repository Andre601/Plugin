package net.powerplugins.plugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ParseUtil{
    
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();
    
    public static Component parse(String msg){
        return mm.deserialize(msg);
    }
    
    public static String toLegacy(String msg){
        return legacy.serialize(parse(msg));
    }
}
