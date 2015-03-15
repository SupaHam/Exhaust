package lc.vq.exhaust.bungee.command;

import com.sk89q.intake.CommandException;
import com.sk89q.intake.CommandMapping;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.context.CommandLocals;
import com.sk89q.intake.util.auth.AuthorizationException;
import com.sk89q.intake.util.auth.Authorizer;
import lc.vq.exhaust.command.AbstractCommandManager;
import lc.vq.exhaust.command.AbstractDefaultExecutor;
import lc.vq.exhaust.command.CommandExecutor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandManager extends AbstractCommandManager {
    private static final CommandExecutor<CommandSender> NULL = new CommandExecutor<CommandSender>() {
        @Override
        public boolean onCommand(CommandSender sender, String name, String[] args) {
            return false;
        }
    };
    /** A reference to the BungeeCord {@link Plugin}. */
    private final Plugin plugin;
    private CommandExecutor<CommandSender> executor;
    /** The default command executor available to use. */
    private DefaultExecutor defaultExecutor;

    public CommandManager(@Nonnull final Plugin plugin) {
        this(plugin, NULL);
        this.setExecutor(new CommandExecutor<CommandSender>() {
            @Override
            public boolean onCommand(CommandSender sender, String name, String[] args) {
                return CommandManager.this.getDefaultExecutor().onCommand(sender, name, args);
            }
        });
    }

    public CommandManager(@Nonnull final Plugin plugin, @Nonnull final CommandExecutor<CommandSender> executor) {
        super();
        checkNotNull(plugin, "plugin");
        checkNotNull(executor, "executor");
        this.plugin = plugin;
        if(executor != NULL) {
            this.setExecutor(executor);
        }

        this.builder.addBinding(new BungeeBinding());
        this.builder.setAuthorizer(new Authorizer() {
            @Override
            public boolean testPermission(CommandLocals locals, String permission) {
                CommandSender sender = locals.get(CommandSender.class);
                return sender != null && sender.hasPermission(permission);
            }
        });
    }

    private void setExecutor(CommandExecutor<CommandSender> executor) {
        this.executor = executor;
    }

    /**
     * Once all of our commands have been registered, register all commands as dynamic commands with Bukkit.
     */
    @Override
    public final void build() {
        PluginManager pm = this.plugin.getProxy().getPluginManager();
        for(CommandMapping command : this.dispatcher.getCommands()) {
            pm.registerCommand(this.plugin, new DynamicCommand(this.executor, command.getAllAliases()[0], command.getAllAliases()));
        }
    }

    @Override
    public AbstractDefaultExecutor<CommandSender> getDefaultExecutor() {
        if(this.defaultExecutor == null) {
            this.defaultExecutor = new DefaultExecutor(this);
        }

        return this.defaultExecutor;
    }

    /**
     * A default command executor, to be used if the user of the command manager does not wish to use their own.
     */
    public final class DefaultExecutor extends AbstractDefaultExecutor<CommandSender> {
        /** A reference to the command manager. */
        private final CommandManager manager;

        public DefaultExecutor(CommandManager manager) {
            this.manager = manager;
        }

        /**
         * Execute a command.
         *
         * @param sender The command sender.
         * @param name An name of the command.
         * @param args The raw arguments, in String[] format.
         * @return true
         */
        @Override
        public boolean onCommand(CommandSender sender, String name, String[] args) {
            final BungeeCommandContext context = new BungeeCommandContext(CommandManager.this.plugin.getProxy(), sender);
            try {
                this.manager.dispatcher().call(createArgString(args, name), context.getLocals(), new String[0]);
            } catch (AuthorizationException e) {
                context.respond(ChatColor.RED + "You don't have permission.");
            } catch (CommandException e) {
                if(e instanceof InvocationCommandException) {
                    if(e.getCause() instanceof NumberFormatException) {
                        context.respond(ChatColor.RED + "Number expected, string received instead.");
                        return true;
                    }
                } else if(e instanceof InvalidUsageException) {
                    InvalidUsageException ue = (InvalidUsageException) e;
                    if(ue.isFullHelpSuggested()) {
                        context.respond(ChatColor.RED + ue.getSimpleUsageString("/"));
                    } else {
                        context.respond(ChatColor.RED + "Invalid usage.");
                    }
                    return true;
                }

                e.printStackTrace();
                context.respond(ChatColor.RED + "An unexpected error occurred.");
            }

            return true;
        }
    }
}
