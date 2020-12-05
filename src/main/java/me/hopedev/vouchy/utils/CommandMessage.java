package me.hopedev.vouchy.utils;

import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandMessage {

    private final MessageCreateEvent event;
    private final String[] args;
    private final String callerCMD;
    private final User user;

    public CommandMessage(MessageCreateEvent event) {

        // Parse CommandMessage into objects to use later if needed
        this.event = event;
        ArrayList<String> cmdlist = new ArrayList<>(Arrays.asList(event.getMessageContent().split(" ")));
        this.callerCMD = cmdlist.get(0); // define callercmd
        cmdlist.remove(0); // then remove
        this.args = cmdlist.toArray(new String[0]); // and convert back to commandlist
        if (event.getMessageAuthor().asUser().isPresent())
        this.user = event.getMessageAuthor().asUser().get(); // define messageauthor
        else
            this.user = null;
    }

    public final String getCallerCMD() {
        return this.callerCMD;
    }

    public final String getArg(int c) {
        return c > this.args.length ? "null["+c+"]": this.args[c];
    }

    public final String[] getArgs() {
        return this.args;
    }

    public final String getFullString() {
        return this.getEvent().getMessageContent();
    }

    public final String getJoinedArgs() {
        return StringUtils.join(this.getArgs());
    }

    public final MessageCreateEvent getEvent() {
        return this.event;
    }

    public final User getMessageUser() {
        return this.user;
    }

    public final Message getMessage() {return this.event.getMessage();};

    public final ServerTextChannel getSTChannel() {return this.event.getServerTextChannel().get();}


}
