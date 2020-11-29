package me.hopedev.vouchy;

import me.hopedev.vouchy.utils.EmbedTemplates;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerJoinListener;
import org.javacord.api.listener.server.ServerLeaveListener;

public class Events implements ServerJoinListener, ServerLeaveListener {

    ServerTextChannel channel = Main.api.getChannelById("782606571791188020").get().asServerTextChannel().get();
    EmbedBuilder eb = EmbedTemplates.def();

    @Override
    public void onServerJoin(ServerJoinEvent event) {

        eb.setTitle("Bot was added to Server");
        eb.setThumbnail(event.getServer().getIcon().get().getUrl().toString());
        eb.addInlineField("Server Name", event.getServer().getName());
        eb.addInlineField("Members", String.valueOf(event.getServer().getMemberCount()));
        eb.addInlineField("Owner", event.getServer().getOwner().get().getDiscriminatedName());
        channel.sendMessage(eb);
    }

    @Override
    public void onServerLeave(ServerLeaveEvent event) {

        eb.setTitle("Bot was removed to Server");
        eb.setThumbnail(event.getServer().getIcon().get().getUrl().toString());
        eb.addInlineField("Server Name", event.getServer().getName());
        eb.addInlineField("Members", String.valueOf(event.getServer().getMemberCount()));
        eb.addInlineField("Owner", event.getServer().getOwner().get().getDiscriminatedName());
        channel.sendMessage(eb);
    }
}
