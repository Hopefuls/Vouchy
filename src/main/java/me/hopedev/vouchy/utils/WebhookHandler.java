package me.hopedev.vouchy.utils;

import me.hopedev.topggwebhooks.VoteData;
import me.hopedev.topggwebhooks.WebhookEvent;
import me.hopedev.topggwebhooks.WebhookListener;
import me.hopedev.vouchy.Main;
import org.discordbots.api.client.entity.Vote;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

public class WebhookHandler implements WebhookListener {

    @Override
    public void onWebhookRequest(WebhookEvent event) {
        System.out.println(event.getRequestString());
        System.out.println("Webhook received!");
    if (event.isAuthorized()) {
        VoteData data = event.getVote();
        if (data.getType().equals("test")) {
            ServerTextChannel channel = Main.api.getChannelById("783262206434738199").get().asServerTextChannel().get();
            User voteUser = Main.api.getUserById(data.getUserID()).join();
            EmbedBuilder eb = EmbedTemplates.def(voteUser);
            eb.setTitle("Woow! A Test vote came through");
            eb.setDescription("Thank you so much for Test-Voting for our bot "+voteUser.getMentionTag()+"("+data.getUserID()+")!");
            channel.sendMessage(eb);
        } else {
            ServerTextChannel channel = Main.api.getChannelById("783262206434738199").get().asServerTextChannel().get();
            User voteUser = Main.api.getUserById(data.getUserID()).join();
            EmbedBuilder eb = EmbedTemplates.def(voteUser);
            eb.setTitle("Woow! A Vote came through!");
            eb.setDescription("Thank you so much for Voting for our Bot "+voteUser.getMentionTag()+"("+data.getUserID()+")!");
            channel.sendMessage(eb);
        }

    } else {
        System.out.println("Not authorized!");
    }
    }
}
