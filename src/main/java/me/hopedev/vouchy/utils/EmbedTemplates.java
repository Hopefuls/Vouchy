package me.hopedev.vouchy.utils;

import me.hopedev.vouchy.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.Arrays;

public class EmbedTemplates {


    public static EmbedBuilder def(User author) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.gray);
        eb.setFooter("Vouchy | Role codes done easy! | by Hope#1000", Main.api.getYourself().getAvatar());
        eb.setTimestampToNow();

        if (author != null) {
            eb.setAuthor(author);
        }
        return eb;
    }


    public static EmbedBuilder harmlessError() {
        EmbedBuilder eb = def();
        eb.setColor(Color.red);
        return eb;
    }
    public static EmbedBuilder def() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.gray);
        eb.setFooter("Vouchy | Role codes done easy! | by Hope#1000", Main.api.getYourself().getAvatar());
        eb.setTimestampToNow();

        return eb;
    }
    public static void sendError(Exception e, CommandMessage message) {



        String userID = message.getMessageUser().getIdAsString();
        String serverID = message.getEvent().getServer().get().getIdAsString();
        String causingCommand = message.getFullString();
        String errorcode = ErrorLogging.createHasteLog(e, causingCommand, serverID, userID);
        EmbedBuilder eb = def();
        eb.setColor(Color.red);
        StringBuilder sb = new StringBuilder();
        Arrays.stream(e.getStackTrace()).iterator().forEachRemaining(stackTraceElement -> sb.append("at ").append(stackTraceElement.getMethodName()).append("\n").append(stackTraceElement.getMethodName()).append("\n"));
        eb.setDescription("There seems to be an error happening. Please report the following error:\n\n```"+e.getMessage()+"```\n**Error-Locator Code:** ``"+errorcode+"``\n_You may be able to continue using the bot normally after this error, but i appreciate these reports._");


        EmbedBuilder ebReport = EmbedTemplates.harmlessError();
        ebReport.setTitle("Error occured in Server "+Main.api.getServerById(serverID).get().getName());
        ebReport.addField("Log URL", "[Error](https://paste.hopefuls.de/"+errorcode+")");

        Main.api.getChannelById("780627429609439233").get().asServerTextChannel().get().sendMessage(ebReport);

        message.getSTChannel().sendMessage(eb);
    }
 }
