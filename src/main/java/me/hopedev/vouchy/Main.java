package me.hopedev.vouchy;

import me.hopedev.vouchy.commands.CommandHandler;
import me.hopedev.vouchy.utils.DatabaseStorage;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.permission.Permissions;

public class Main {

    public static DiscordApi api;



    public static void main(String[] args) {

    api = new DiscordApiBuilder().setToken(Secrets.getToken()).setAllIntentsExcept(Intent.GUILD_PRESENCES).login().join();
    System.out.println(api.createBotInvite(Permissions.fromBitmask(8)));


        // Load CommandHandler
        api.addMessageCreateListener(new CommandHandler());

        // Start MySQL
        DatabaseStorage.start(Secrets.getHostname(), "vouchy", "root", Secrets.getPassword());

    }
}
