package lc.vq.exhaust.bungee.command;

import com.sk89q.intake.parametric.ParameterException;
import com.sk89q.intake.parametric.argument.ArgumentStack;
import com.sk89q.intake.parametric.binding.BindingBehavior;
import com.sk89q.intake.parametric.binding.BindingHelper;
import com.sk89q.intake.parametric.binding.BindingMatch;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * A BungeeCord-specific command binding.
 */
public class BungeeBinding extends BindingHelper {

    /**
     * Provide a {@link CommandSender}.
     *
     * @param stack The argument stack.
     * @return A CommandSender.
     * @throws ParameterException If the stack does not have a CommandSender.
     */
    @BindingMatch(type = CommandSender.class, behavior = BindingBehavior.PROVIDES)
    public CommandSender provideCommandSender(ArgumentStack stack) throws ParameterException {
        CommandSender sender = stack.getContext().getLocals().get(CommandSender.class);
        if(sender != null) {
            return sender;
        } else {
            throw new ParameterException("Missing CommandSender.");
        }
    }

    /**
     * Provide a {@link ProxiedPlayer}.
     *
     * @param stack The argument stack.
     * @return A Player.
     * @throws ParameterException If the stack does not have a Player.
     */
    @BindingMatch(type = ProxiedPlayer.class, behavior = BindingBehavior.PROVIDES)
    public ProxiedPlayer provideProxiedPlayer(ArgumentStack stack) throws ParameterException {
        ProxiedPlayer player = stack.getContext().getLocals().get(ProxiedPlayer.class);
        if(player != null) {
            return player;
        } else {
            throw new ParameterException("Caller is not a ProxiedPlayer.");
        }
    }
}
