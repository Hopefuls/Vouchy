package me.hopedev.vouchy;


import me.hopedev.topggwebhooks.Webhook;
import me.hopedev.topggwebhooks.WebhookBuilder;
import me.hopedev.vouchy.commands.CommandHandler;
import me.hopedev.vouchy.utils.DatabaseStorage;
import me.hopedev.vouchy.utils.WebhookHandler;
import org.discordbots.api.client.DiscordBotListAPI;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.user.User;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static DiscordApi api;
    public static DiscordBotListAPI botListAPI;



    public static void main(String[] args) {
        botListAPI = new DiscordBotListAPI.Builder()
                .token(Secrets.topGGToken())
                .botId("777993845047689226")
                .build();
    api = new DiscordApiBuilder().setToken(Secrets.getToken()).setAllIntentsExcept(Intent.GUILD_PRESENCES).login().join();
    System.out.println(api.createBotInvite(Permissions.fromBitmask(8)));

        Webhook webhook = new WebhookBuilder(new WebhookHandler()).setAuthorization(Secrets.getWebhookAuth()).setPort(5050).build();
        try {
            webhook.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        api.getThreadPool().getScheduler().scheduleAtFixedRate(() -> botListAPI.setStats(api.getServers().size()), 0, 10, TimeUnit.MINUTES);
        // Load CommandHandler
        api.addMessageCreateListener(new CommandHandler());

        // Load ServerLeaveandKick
        api.addServerJoinListener(new Events());
        api.addServerLeaveListener(new Events());
        // Start MySQL
        DatabaseStorage.start(Secrets.getHostname(), "vouchy", "root", Secrets.getPassword());

    }
}
