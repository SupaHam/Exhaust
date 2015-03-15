package lc.vq.exhaust.bukkit.command;

import com.sk89q.intake.context.CommandLocals;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Bukkit-oriented command context.
 */
public final class BukkitCommandContext {

    /** The command locals. */
    private final CommandLocals locals = new CommandLocals();
    /** The Bukkit server. */
    @Nonnull private final Server server;
    /** The command sender. */
    @Nonnull private final CommandSender sender;

    public BukkitCommandContext(@Nonnull final Server server, @Nonnull final CommandSender sender) {
        checkNotNull(server, "server");
        checkNotNull(sender, "sender");

        this.server = server;
        this.sender = sender;

        this.locals.put(Server.class, server);
        this.locals.put(CommandSender.class, sender);
        this.locals.put(Player.class, (sender instanceof Player) ? (Player) sender : null);
    }

    public CommandLocals getLocals() {
        return this.locals;
    }

    @Nonnull
    public Server getServer() {
        return this.server;
    }

    @Nonnull
    public CommandSender getSender() {
        return this.sender;
    }

    @Nullable
    public Player getPlayer() {
        final Player player = this.locals.get(Player.class);
        if(player != null) {
            return player;
        } else {
            return null;
        }
    }

    public void respond(@Nonnull final String message) {
        checkNotNull(message, "message");
        this.sender.sendMessage(message);
    }
}
