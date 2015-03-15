package lc.vq.exhaust.command;

import com.sk89q.intake.context.CommandLocals;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractCommandContext<O, S, P> {

    /** The command locals. */
    protected final CommandLocals locals = new CommandLocals();

    public AbstractCommandContext() {
    }

    public final CommandLocals getLocals() {
        return this.locals;
    }

    @Nonnull
    public abstract O getServer();

    @Nonnull
    public abstract S getSender();

    @Nullable
    public abstract P getPlayer();

    public abstract void respond(@Nonnull final String message);
}
