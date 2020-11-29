package me.hopedev.vouchy.commands;

import com.vdurmont.emoji.EmojiParser;
import me.hopedev.vouchy.Main;
import me.hopedev.vouchy.utils.*;
import me.hopedev.vouchy.utils.vouchermanager.ApartedManager;
import me.hopedev.vouchy.utils.vouchermanager.KeyValidator;
import me.hopedev.vouchy.utils.vouchermanager.Voucher;
import me.hopedev.vouchy.utils.vouchermanager.VoucherManager;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.Javacord;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CommandHandler implements MessageCreateListener {
    public static HashMap<String, Boolean> cooldown = new HashMap<>();
    public static HashMap<String, Integer> cooldownViolations = new HashMap<>();
    public static HashMap<String, Boolean> ratelimited = new HashMap<>();

    @Override
    public void onMessageCreate(MessageCreateEvent event) {

        String prefix = "vy>";
        CommandMessage commandMessage = new CommandMessage(event);

        if (!commandMessage.getCallerCMD().equalsIgnoreCase(prefix) && !commandMessage.getMessage().getMentionedUsers().contains(Main.api.getYourself()) || commandMessage.getMessageUser().isBot()) {
            return;
        }
            // System.out.println(commandMessage.getArg(1));

        // Added for debugging purposes, will be removed after approval
        System.out.println(commandMessage.getFullString());
        Message message = commandMessage.getMessage();
            if (commandMessage.getArg(0).equalsIgnoreCase("help")) {
                EmbedBuilder eb = EmbedTemplates.def(commandMessage.getMessageUser());
                eb.setTitle("Knowledgebase");

                StringBuilder sb = new StringBuilder();
                sb.append("**Prefix:** ``vy> `` or ``@mention``\n\n");

                sb.append("``vy> help`` - _Shows this Message_\n");
                sb.append("``vy> about`` - _About the Developer_\n");
                sb.append("\n**Administrative Commands** _[Do not use in public channels!]_\n");
                sb.append("``vy> create @RoleMention`` - _Create a unique role-voucher for the mentioned role_\n");
                sb.append("``vy> list`` - _List your currently active role-vouchers on this server [Do not use this command in public channels]_\n");
                sb.append("``vy> delete <code>`` - _Delete the corresponding role-voucher_\n");
                sb.append("``vy> validate <code>`` - _Check the validity of a code. Returns who created the code and for what role it is_\n");
                sb.append("\n**User-Usable Commands**\n");
                sb.append("``vy> claim/redeem <code>`` - _Claim the Role that is associated with this code. This will invalidate the code afterwards._\n");

                sb.append("\n\n*"+ EmojiParser.parseToUnicode(":notepad_spiral:") +"Note: The Bot might need the following Permissions to work properly:*");
                sb.append("\n   - Delete Messages (cleary chat, get rid of code messages) [OPTIONAL]\n");
                sb.append("   - Manage Roles (giving claimed Roles) [IMPORTANT FOR BOT MECHANICS]");
                // eb.setDescription(sb.toString()+"\n\n*"+ EmojiParser.parseToUnicode(":notepad_spiral:") +"Note: This Bot might need __Manage Roles__ permissions in order to give users their Roles*");
                eb.setDescription(sb.toString());
                commandMessage.getSTChannel().sendMessage(eb);
                return;
            }

            if (commandMessage.getArg(0).equalsIgnoreCase("about")) {
                EmbedBuilder eb = EmbedTemplates.def();

                eb.setThumbnail(Main.api.getYourself().getAvatar());
                eb.setDescription("Vouchy - Made by HopeDev with "+ EmojiParser.parseToUnicode(":heart:"));
                eb.setTitle("Vouchy - Role claiming done easy!");
                eb.addInlineField("Language", "Java");
                eb.addInlineField("Library", "Javacord");
                eb.addInlineField("Library Version", Javacord.DISPLAY_VERSION);
                eb.addInlineField("Developer", "Hope#1000 ([top.gg](https://top.gg/user/669452973755072524) | [Reddit](https://www.reddit.com/user/LessH0pe/) | [GitHub](https://github.com/Hopefuls))");
                // eb.addField("Ruri's Source Code on Github", "[/Hopefuls/Ruri](https://github.com/Hopefuls/Ruri)");
                commandMessage.getSTChannel().sendMessage(eb);
                return;
            }

            if (commandMessage.getArg(0).equalsIgnoreCase("validate")) {
                if (!commandMessage.getEvent().getMessageAuthor().canManageRolesOnServer()) {
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setTitle("Missing Permissions").setDescription("You are not authorized to use this Command!\n\n*You are not permitted to Manage Roles.*"));
                    return;
                }
                if (commandMessage.getArgs().length > 2) {
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("**Usage:** ``vy> validate <code>``\n\nExample: vy> validate wdj03t323d3"));
                    return;
                }

                if (isCooldown(commandMessage)) {
                    return;
                }

                startCooldown(commandMessage, 3, TimeUnit.SECONDS);

                VoucherManager manager = new VoucherManager(commandMessage.getArg(1), commandMessage);

                if (!manager.isValid()) {
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("This code is invalid."));
                    return;
                } else {
                    Voucher voucher = manager.getVoucher();

                    if (voucher.getServerID() != event.getServer().get().getId()) {
                        commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("This code is invalid."));
                        return;
                    }

                    EmbedBuilder eb = EmbedTemplates.def(commandMessage.getMessageUser());
                    eb.setTitle("Key found!");
                    eb.addField("Server ID", voucher.getServerID()+"");
                    eb.addField("Created by", Main.api.getUserById(voucher.getCreatorID()).join().getMentionTag());
                    // eb.addField("created At", voucher.getCreatedAt()+"");
                    eb.addField("Role", Main.api.getRoleById(voucher.getRoleID()).get().getMentionTag());

                    commandMessage.getSTChannel().sendMessage(eb);
                }

                return;
            }

            if (commandMessage.getArg(0).equalsIgnoreCase("create")) {
                if (!commandMessage.getEvent().getMessageAuthor().canManageRolesOnServer()) {
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setTitle("Missing Permissions").setDescription("You are not authorized to use this Command!\n\n*You are not permitted to Manage Roles.*"));
                    return;
                }
                if (commandMessage.getArgs().length > 2 || commandMessage.getEvent().getMessage().getMentionedRoles().size() != 1) {
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("**Usage:** ``vy> create @RoleMention``\n\nExample: vy> create @everyone"));
                    return;
                }

                if (isCooldown(commandMessage)) {
                    return;
                }

                startCooldown(commandMessage, 3, TimeUnit.SECONDS);
                Role role = commandMessage.getMessage().getMentionedRoles().get(0);


                if (!commandMessage.getMessageUser().canManageRole(role)) {
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setTitle("Missing Permissions").setDescription("I cannot create a voucher for a role that is higher than yours!"));
                    return;
                }
                String serverID = commandMessage.getEvent().getServer().get().getIdAsString();
                String userID = commandMessage.getMessageUser().getIdAsString();
                String roleID = role.getIdAsString();

                String createdKey = VoucherManager.createKey(serverID, userID, roleID, commandMessage);

                EmbedBuilder embedBuilder = EmbedTemplates.def(commandMessage.getMessageUser());
                StringBuilder sb = new StringBuilder();
                sb.append("This message expires after 15 seconds.");
                if (!Main.api.getYourself().canManageRole(role)) {
                    sb.append("\n\n*Note: I won't be able to give a user that role when trying to claim it.*\n*Make sure that the role is not higher/equal than me and that i am not missing the __Manage Roles__ Permissions*");
                }
                embedBuilder.setDescription(sb.toString());
                embedBuilder.setTitle("Role Key created!");
                embedBuilder.addField("Key", "||``" + createdKey + "``||");
                embedBuilder.addField("Role", role.getMentionTag());

                MessageUtils.expire(commandMessage.getSTChannel().sendMessage(embedBuilder).join(), 15, TimeUnit.SECONDS);



                return;
            }

            if (commandMessage.getArg(0).equalsIgnoreCase("list")) {
                if (!commandMessage.getEvent().getMessageAuthor().canManageRolesOnServer()) {
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setTitle("Missing Permissions").setDescription("You are not authorized to use this Command!\n\n*You are not permitted to Manage Roles.*"));
                    return;
                }

                if (isCooldown(commandMessage)) {
                    return;
                }

                startCooldown(commandMessage, 3, TimeUnit.SECONDS);

                String serverid = commandMessage.getEvent().getServer().get().getIdAsString();
                ResultSet set = StorageUtils.executeQuery("select * from voucherData where serverID = "+serverid);
                StringBuilder sb = new StringBuilder();
                boolean foundsome = false;
                try {
                   while (set.next()) {
                       foundsome = true;
                    sb.append("Key: ``"+set.getString("voucherKey")+"``");
                    sb.append(" | ");
                    sb.append("User: "+Main.api.getUserById(set.getLong("creatorID")).join().getMentionTag());
                    sb.append(" | ");
                    if (!Main.api.getRoleById(set.getLong("roleID")).isPresent()) {
                        sb.append("Role: **DELETED**");
                        VoucherManager voucherManager = new VoucherManager(set.getString("voucherKey"), commandMessage);
                        voucherManager.getVoucher().delete();
                    } else {
                        sb.append("Role: "+Main.api.getRoleById(set.getLong("roleID")).get().getMentionTag());
                    }
                    sb.append(" | ");
                    if (set.getBoolean("expired")) {
                        sb.append("[REDEEMED]");
                    } else {
                        sb.append("[ACTIVE]");
                    }
                    sb.append("\n");
                   }
               } catch (Exception exception) {
                   EmbedTemplates.sendError(exception, commandMessage);
               }

                if (!foundsome) {
                    EmbedBuilder eb = EmbedTemplates.def(commandMessage.getMessageUser());
                    eb.setTitle("Currently active Vouchers on " + commandMessage.getEvent().getServer().get().getName());
                    eb.setDescription("*None, yet.*");
                    commandMessage.getSTChannel().sendMessage(eb);
                    return;
                }

                EmbedBuilder eb = EmbedTemplates.def(commandMessage.getMessageUser());
                eb.setTitle("Currently active Vouchers on " + commandMessage.getEvent().getServer().get().getName());
                eb.setDescription(sb.toString()+"\n\nThis message expires after 1 minute to hide your current vouchers.");
                MessageUtils.expire(commandMessage.getSTChannel().sendMessage(eb).join(), 1, TimeUnit.MINUTES);
                // MessageUtils.deleteAfter(commandMessage.getSTChannel().sendMessage(eb).join(), 1, TimeUnit.MINUTES);

                return;
            }

            if (commandMessage.getArg(0).equalsIgnoreCase("redeem") || commandMessage.getArg(0).equalsIgnoreCase("claim")) {
                if (commandMessage.getArgs().length > 2 || commandMessage.getArgs().length == 1) {
                    deleteIfPossible(message);
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("**Usage:** ``vy> redeem <code>``\n\nExample: vy> redeem 3fu39fj039309f3"));
                    return;
                }

                if (isCooldown(commandMessage)) {
                    return;
                }

                startCooldown(commandMessage, 3, TimeUnit.SECONDS);
                VoucherManager manager = new VoucherManager(commandMessage.getArg(1), commandMessage);

                if (!manager.isValid()) {
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("Invalid code."));
                    return;
                } else {
                    Voucher voucher = manager.getVoucher();

                    if (voucher.getServerID() != event.getServer().get().getId()) {
                        commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("Invalid code."));
                        return;
                    }


                    if (voucher.isRedeemed()) {
                        deleteIfPossible(message);
                        commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("This voucher was redeemed already!"));
                        return;
                    }


                    Optional<Role> role = Main.api.getRoleById(voucher.getRoleID());


                    if (!role.isPresent()) {
                        deleteIfPossible(message);
                        commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("The Role which corresponds to your voucher doesn't seem to exist anymore :(.\nIts ID was " + voucher.getRoleID() + "\nDeleting voucher due to being invalid"));
                        voucher.delete();
                        return;
                    }
                    if (!Main.api.getYourself().canManageRole(role.get())) {
                        deleteIfPossible(message);
                        commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("*Your code is valid, but i am unable to give you that role due to it either being higher/equal than me and/or me missing __Manage Roles__ Permissions*"));

                        return;
                    }

                    if (commandMessage.getMessageUser().getRoles(commandMessage.getEvent().getServer().get()).contains(role.get())) {
                        commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setColor(Color.YELLOW).setDescription("*Your code is valid, but you already have this Role!*"));
                        deleteIfPossible(message);
                        return;
                    }

                    deleteIfPossible(message);
                    commandMessage.getMessageUser().addRole(role.get());
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.def().setColor(Color.GREEN).setAuthor(commandMessage.getMessageUser()).setDescription("You have successfully redeemed the voucher for the Role " + role.get().getMentionTag() + "!"));


                    voucher.redeem();

                }

                return;
            }


            if (commandMessage.getArg(0).equalsIgnoreCase("delete")) {
                if (!commandMessage.getEvent().getMessageAuthor().canManageRolesOnServer()) {
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setTitle("Missing Permissions").setDescription("You are not authorized to use this Command!\n\n*You are not permitted to Manage Roles.*"));
                    return;
                }
                if (commandMessage.getArgs().length > 2 || commandMessage.getArgs().length == 1) {
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("**Usage:** ``vy> delete <code>``\n\nExample: vy> delete 2d3nd2390f30v03"));
                    return;
                }

                if (isCooldown(commandMessage)) {
                    return;
                }

                startCooldown(commandMessage, 3, TimeUnit.SECONDS);

                VoucherManager manager = new VoucherManager(commandMessage.getArg(1), commandMessage);

                    if (!manager.isValid()) {
                        commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("This code does not exist."));
                        return;
                    }


                    Voucher voucher = manager.getVoucher();
                if (voucher.getServerID() != event.getServer().get().getId()) {
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setDescription("This code does not exist."));
                    return;
                }

                deleteIfPossible(message);
                    voucher.delete();
                    commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setColor(Color.green).setDescription((voucher.isRedeemed() ? "Redeemed" : "Active")+" Voucher was successfully removed!"));



            }

        if (commandMessage.getArg(0).equalsIgnoreCase("clearall")) {
            if (!commandMessage.getEvent().getMessageAuthor().canManageRolesOnServer()) {
                commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setTitle("Missing Permissions").setDescription("You are not authorized to use this Command!\n\n*You are not permitted to Manage Roles.*"));
                return;
            }
            if (isCooldown(commandMessage)) {
                return;
            }

            startCooldown(commandMessage, 3, TimeUnit.SECONDS);
            new ApartedManager(commandMessage).deleteAll();
            commandMessage.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setColor(Color.green).setDescription("All Keys cleared!"));


            return;
        }

        if (commandMessage.getArg(0).equalsIgnoreCase("servers")) {
            if (!commandMessage.getMessageUser().isBotOwner()) {
                commandMessage.getSTChannel().sendMessage(EmbedTemplates.def(commandMessage.getMessageUser()).setDescription("You cannot use this command"));
                return;
            }

            EmbedBuilder eb = EmbedTemplates.def(commandMessage.getMessageUser());
            StringBuilder sb = new StringBuilder();
            eb.setTitle("Bot is in the following Guilds");
            Main.api.getServers().forEach(server -> sb.append(server.getName()+"\n"));

            commandMessage.getSTChannel().sendMessage(eb.setDescription(sb.toString()));
            return;
        }


    }

    private void deleteIfPossible(Message message) {
        if (message.canYouDelete()) {
            message.delete("Pog");
        } else {
            System.out.println("could not delete");
        }
    }


    public static boolean isCooldown(CommandMessage message) {
        String server = message.getEvent().getServer().get().getIdAsString();
        boolean val = cooldown.getOrDefault(message.getEvent().getServer().get().getIdAsString(), false);
        int currentViolations = cooldownViolations.getOrDefault(server, 1);

        boolean hardRatelimit = ratelimited.getOrDefault(server, false);
        if (hardRatelimit) {
            message.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setAuthor(message.getMessageUser()).setDescription("You are currently hard-ratelimited! Try again in 5-10 minutes!"));
            return true;
        }
        if (val) {
                if (currentViolations == 1) {
                    Main.api.getThreadPool().getScheduler().schedule(() -> {
                        cooldownViolations.remove(server);
                    }, 1, TimeUnit.MINUTES);
                }
            if (currentViolations == 5) {
                message.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setAuthor(message.getMessageUser()).setDescription("You have been hard-ratelimited! Try again in 5-10 minutes!"));
                ratelimited.put(server, true);

                Main.api.getThreadPool().getScheduler().schedule(() -> {
                    ratelimited.remove(server);
                }, 10, TimeUnit.MINUTES);

                return true;
            } else {
                message.getSTChannel().sendMessage(EmbedTemplates.harmlessError().setAuthor(message.getMessageUser()).setDescription("Stop being so fast! You're confusing me!\n\n**Please wait a bit between commands (around 5 seconds)**\n\ncurrRL: "+currentViolations));

            }
            cooldownViolations.put(server, currentViolations+1);

        }

        return val;
    }

    public static void startCooldown(CommandMessage message, long time, TimeUnit unit) {

        String server = message.getEvent().getServer().get().getIdAsString();
        cooldown.put(server, true);
        Main.api.getThreadPool().getScheduler().schedule(() -> {
            cooldown.remove(server);
        }, time, unit);
    }
}
