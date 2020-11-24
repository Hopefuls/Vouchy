package me.hopedev.vouchy.utils;

import com.vdurmont.emoji.EmojiParser;
import me.hopedev.vouchy.Main;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.concurrent.TimeUnit;

public class MessageUtils {


    public static void deleteAfter(Message message, long time, TimeUnit unit) {
        Main.api.getThreadPool().getScheduler().schedule((Runnable) message::delete, time, unit);
    }

    public static void updateAfter(Message message, long time, TimeUnit unit) {
        Main.api.getThreadPool().getScheduler().schedule((Runnable) () -> message.edit(message.getEmbeds().get(0).toBuilder().setDescription("><><><><>Removed for privacy<><><><><")), time, unit);
    }


    public static void expire(Message message, long time, TimeUnit unit) {
        Main.api.getThreadPool().getScheduler().schedule((Runnable) () -> message.edit(EmbedTemplates.def().setDescription(EmojiParser.parseToUnicode(":x:")+" Message expired")), time, unit);
    }

}
