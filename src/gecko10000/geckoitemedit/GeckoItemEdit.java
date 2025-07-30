package gecko10000.geckoitemedit;

import org.bukkit.plugin.java.JavaPlugin;

class GeckoItemEdit extends JavaPlugin {

    @Override
    public void onEnable() {
        new CommandHandler().register();
    }

}
