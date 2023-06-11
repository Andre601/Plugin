package net.powerplugins.plugin.utils;

import net.kyori.adventure.text.Component;

public class MiniMessageBuilder{
    
    private final StringBuilder builder = new StringBuilder();
    
    private MiniMessageBuilder(){}
    
    public static MiniMessageBuilder get(){
        return new MiniMessageBuilder();
    }
    
    public MiniMessageBuilder append(String text){
        this.builder.append(text);
        return this;
    }
    
    public MiniMessageBuilder append(String text, String colour){
        return append("<" + colour + ">" + text + "</" + colour + ">");
    }
    
    public MiniMessageBuilder append(String text, String hover, String action){
        return append(
            "<hover:show_text:\"" + hover + "\">" +
            "<click:" + (action.startsWith("/") ? "run_command" : "open_url") + ":\"" + action + "\">" +
            text +
            "</click>" +
            "</hover>"
        );
    }
    
    public MiniMessageBuilder append(MiniMessageBuilder builder){
        return append(builder.toString());
    }
    
    public MiniMessageBuilder append(MiniMessageBuilder builder, String color){
        return append(builder.toString(), color);
    }
    
    public MiniMessageBuilder append(MiniMessageBuilder builder, String hover, String action){
        return append(builder.toString(), hover, action);
    }
    
    public MiniMessageBuilder newline(){
        return append("<newline>");
    }
    
    public MiniMessageBuilder replaceLast(String target, String replacement){
        builder.replace(builder.lastIndexOf(target), builder.lastIndexOf(target) + 1, replacement);
        return this;
    }
    
    public int length(){
        return builder.length();
    }
    
    public Component toComponent(){
        return ParseUtil.parse(toString());
    }
    
    @Override
    public String toString(){
        return builder.toString();
    }
}
