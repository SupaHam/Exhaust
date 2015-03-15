/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package lc.vq.exhaust.bukkit.command;

import com.sk89q.intake.CommandException;
import com.sk89q.intake.CommandMapping;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.context.CommandLocals;
import com.sk89q.intake.util.auth.AuthorizationException;
import com.sk89q.intake.util.auth.Authorizer;
import lc.vq.exhaust.command.AbstractCommandManager;
import lc.vq.exhaust.command.AbstractDefaultExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Bukkit-oriented command manager.
 */
public class CommandManager extends AbstractCommandManager {
    /** A reference to the Bukkit {@link Plugin}. */
    private final Plugin plugin;
    /** The default command executor available to use. */
    private DefaultExecutor defaultExecutor;
    /** If there is an issue grabbing Bukkit's CommandMap, we create our own to use in our fallback command manager listener. */
    private CommandMap fallbackCommandMap;

    public CommandManager(@Nonnull final Plugin plugin) {
        super();
        checkNotNull(plugin, "plugin");
        this.plugin = plugin;

        this.builder.addBinding(new BukkitBinding());
        this.builder.setAuthorizer(new Authorizer() {
            @Override
            public boolean testPermission(CommandLocals locals, String permission) {
                CommandSender sender = locals.get(CommandSender.class);
                return sender != null && sender.hasPermission(permission);
            }
        });
    }

    /**
     * Once all of our commands have been registered, register all commands as dynamic commands with Bukkit.
     */
    @Override
    public final void build() {
        for(CommandMapping command : this.dispatcher.getCommands()) {
            DynamicCommand dynamic = new DynamicCommand(
                command.getAllAliases(),
                command.getDescription().getShortDescription(),
                command.getDescription().getUsage(),
                this.plugin, // owner
                this,
                this.plugin // plugin
            );
            this.getCommandMap().register(this.plugin.getDescription().getName(), dynamic);
        }
    }

    /**
     * If you don't wish to create your own executor, you may be lazy and use the default one.
     *
     * @return a new {@link DefaultExecutor}
     */
    @Override
    public DefaultExecutor getDefaultExecutor() {
        if(this.defaultExecutor == null) {
            this.defaultExecutor = new DefaultExecutor(this);
        }

        return this.defaultExecutor;
    }

    /** @author zml2008 */
    private CommandMap getCommandMap() {
        CommandMap map = getField(this.plugin.getServer().getPluginManager(), "commandMap");
        if(map == null) {
            if(this.fallbackCommandMap != null) {
                map = this.fallbackCommandMap;
            } else {
                this.plugin.getLogger().severe("Could not retrieve server CommandMap, using fallback instead!");
                this.fallbackCommandMap = map = new SimpleCommandMap(Bukkit.getServer());
                Bukkit.getServer().getPluginManager().registerEvents(new FallbackCommandManager(this.fallbackCommandMap), this.plugin);
            }
        }

        return map;
    }

    /** @author zml2008 */
    private static <T> T getField(Object from, String name) {
        Class<?> clazz = from.getClass();
        do {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                return (T) field.get(from);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                // ignore
            }
        } while (clazz.getSuperclass() != Object.class && ((clazz = clazz.getSuperclass()) != null));
        return null;
    }

    /**
     * A default command executor, to be used if the user of the command manager does not wish to use their own.
     */
    public final class DefaultExecutor extends AbstractDefaultExecutor<CommandSender> implements CommandExecutor {
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
            final BukkitCommandContext context = new BukkitCommandContext(sender.getServer(), sender);
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
                    String message = ue.getMessage();
                    context.respond(ChatColor.RED + (message != null ? message : "The command was not used properly (no more help available)"));
                    context.respond(ChatColor.RED + "Usage: " + ue.getSimpleUsageString("/"));
                    return true;
                }

                e.printStackTrace();
                context.respond(ChatColor.RED + "An unexpected error occurred.");
            }

            return true;
        }

        /**
         * Execute a command.
         *
         * @param sender The command sender.
         * @param command The command.
         * @param alias An alias of the command.
         * @param args The raw arguments, in String[] format.
         * @return true
         */
        @Override
        public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
            return this.onCommand(sender, alias, args);
        }
    }
}
