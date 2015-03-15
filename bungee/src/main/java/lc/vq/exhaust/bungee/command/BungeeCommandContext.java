package lc.vq.exhaust.bungee.command;

import lc.vq.exhaust.command.AbstractCommandContext;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Bukkit-oriented command context.
 */
public final class BungeeCommandContext extends AbstractCommandContext<ProxyServer, CommandSender, ProxiedPlayer> {

    /** The Bukkit server. */
    @Nonnull private final ProxyServer server;
    /** The command sender. */
    @Nonnull private final CommandSender sender;

    public BungeeCommandContext(@Nonnull final ProxyServer server, @Nonnull final CommandSender sender) {
        checkNotNull(server, "server");
        checkNotNull(sender, "sender");

        this.server = server;
        this.sender = sender;

        this.locals.put(ProxyServer.class, server);
        this.locals.put(CommandSender.class, sender);
        this.locals.put(ProxiedPlayer.class, (sender instanceof ProxiedPlayer) ? (ProxiedPlayer) sender : null);
    }

    @Nonnull
    @Override
    public ProxyServer getServer() {
        return this.server;
    }

    @Nonnull
    @Override
    public CommandSender getSender() {
        return this.sender;
    }

    @Nullable
    @Override
    public ProxiedPlayer getPlayer() {
        final ProxiedPlayer player = this.locals.get(ProxiedPlayer.class);
        if(player != null) {
            return player;
        } else {
            return null;
        }
    }

    @Override
    public void respond(@Nonnull final String message) {
        checkNotNull(message, "message");
        this.sender.sendMessage(message);
    }
}
