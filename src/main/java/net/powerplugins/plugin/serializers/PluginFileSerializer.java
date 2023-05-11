package net.powerplugins.plugin.serializers;

import net.powerplugins.plugin.file.PluginFile;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class PluginFileSerializer implements TypeSerializer<PluginFile>{
    
    public static final PluginFileSerializer INSTANCE = new PluginFileSerializer();
    
    @Override
    public PluginFile deserialize(Type type, ConfigurationNode node) throws SerializationException{
        return new PluginFile.Builder()
            .setName(node.node("info", "name").getString(""))
            .setAuthors(node.node("info", "authors").getList(String.class))
            .setVersion(node.node("info", "version").getString(""))
            .setDepends(node.node("info", "depends").getList(String.class))
            .setSoftDepends(node.node("info", "softdepends").getList(String.class))
            .setUrl(node.node("info", "url").getString(""))
            .setDescription(node.node("info", "description").getString(""))
            .setCategory(node.node("info", "category").getString(""))
            .build();
    }
    
    @Override
    public void serialize(Type type, @Nullable PluginFile obj, ConfigurationNode node) throws SerializationException{
        if(obj == null){
            node.raw(null);
            return;
        }
        
        node.node("info", "name").set(obj.name());
        node.node("info", "authors").set(obj.authors());
        node.node("info", "version").set(obj.version());
        node.node("info", "depends").set(obj.depends());
        node.node("info", "softdepends").set(obj.softdepends());
        node.node("info", "url").set(obj.url());
        node.node("info", "description").set(obj.description());
        node.node("info", "category").set(obj.category().getName());
    }
}
