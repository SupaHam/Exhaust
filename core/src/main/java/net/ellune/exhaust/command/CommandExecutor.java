package net.ellune.exhaust.command;

public interface CommandExecutor<T> {

    boolean onCommand(T sender, String name, String[] args);

}
