package lc.vq.exhaust.command;

public interface CommandExecutor<T> {
    public boolean onCommand(T sender, String name, String[] args);
}
