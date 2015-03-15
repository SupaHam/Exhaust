package lc.vq.exhaust.bukkit.command;

import com.sk89q.intake.parametric.ParameterException;
import com.sk89q.intake.parametric.argument.ArgumentStack;
import com.sk89q.intake.parametric.binding.BindingBehavior;
import com.sk89q.intake.parametric.binding.BindingHelper;
import com.sk89q.intake.parametric.binding.BindingMatch;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A Bukkit-specific command binding.
 */
public class BukkitBinding extends BindingHelper {

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
            throw new ParameterException("Uh oh! The sender could not be found.");
        }
    }

    /**
     * Provide a {@link Player}.
     *
     * @param stack The argument stack.
     * @return A Player.
     * @throws ParameterException If the stack does not have a Player.
     */
    @BindingMatch(type = Player.class, behavior = BindingBehavior.PROVIDES)
    public Player providePlayer(ArgumentStack stack) throws ParameterException {
        Player player = stack.getContext().getLocals().get(Player.class);
        if(player != null) {
            return player;
        } else {
            throw new ParameterException("Uh oh! The player could not be found.");
        }
    }
}
