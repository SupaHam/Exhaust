package lc.vq.exhaust.command;

import com.google.common.base.Joiner;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.fluent.CommandGraph;
import com.sk89q.intake.fluent.DispatcherNode;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.handler.ExceptionConverterHelper;
import com.sk89q.intake.parametric.handler.ExceptionMatch;
import com.sk89q.intake.util.auth.AuthorizationException;

public abstract class AbstractCommandManager {
    /** The command builder. */
    protected final ParametricBuilder builder;
    /** The command graph. */
    protected final CommandGraph graph;
    /** The command dispatcher. */
    protected final Dispatcher dispatcher;

    public AbstractCommandManager() {
        this.builder = new ParametricBuilder();
        this.builder.addExceptionConverter(new ExceptionConverterHelper() {
            @ExceptionMatch
            public void match(AuthorizationException e) throws AuthorizationException {
                throw e;
            }

            @ExceptionMatch
            public void match(NumberFormatException e) {
                throw e;
            }
        });

        this.graph = new CommandGraph().builder(this.builder);
        this.dispatcher = this.graph.getDispatcher();
    }

    /**
     * Gets the command builder.
     */
    public DispatcherNode builder() {
        return this.graph.commands();
    }

    /**
     * Gets the {@link ParametricBuilder} to configure the command manager.
     */
    public ParametricBuilder config() {
        return this.builder;
    }

    public Dispatcher dispatcher() {
        return this.dispatcher;
    }

    public abstract AbstractDefaultExecutor getDefaultExecutor();

    public abstract void build();

    /**
     * Creates a single argument string, inserting the command name as the first word.
     *
     * @param args the original args
     * @param name the command name
     * @return a single argument string
     */
    public static String createArgString(String[] args, String name) {
        String[] split = new String[args.length + 1];
        System.arraycopy(args, 0, split, 1, args.length);
        split[0] = name;

        return Joiner.on(' ').join(split);
    }
}
