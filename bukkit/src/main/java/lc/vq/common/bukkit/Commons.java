package lc.vq.common.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public final class Commons extends JavaPlugin {

    /** The singleton instance of this plugin. */
    private static Commons commons;

    public Commons() {
        commons = this;
    }

    @Override
    public final void onEnable() {
    }

    @Override
    public final void onDisable() {
        commons = null;
    }

    public static Commons get() {
        return commons;
    }
}
