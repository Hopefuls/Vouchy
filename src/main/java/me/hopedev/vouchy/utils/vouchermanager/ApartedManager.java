package me.hopedev.vouchy.utils.vouchermanager;

import me.hopedev.vouchy.utils.CommandMessage;
import me.hopedev.vouchy.utils.DatabaseStorage;
import me.hopedev.vouchy.utils.EmbedTemplates;

import java.sql.PreparedStatement;

public class ApartedManager {

    private final CommandMessage message;


    public ApartedManager(CommandMessage message) {
        this.message = message;
    }


    public final void deleteAll() {
        try {
            PreparedStatement statement = DatabaseStorage.getConnection().prepareStatement("DELETE FROM voucherData WHERE serverID = ?");
            statement.setLong(1, message.getEvent().getServer().get().getId());
            statement.executeUpdate();
        } catch (Exception e) {
            EmbedTemplates.sendError(e, this.message);
        }
    }
}
