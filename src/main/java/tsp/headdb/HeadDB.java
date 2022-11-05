package tsp.headdb;

import tsp.headdb.core.command.CommandCategory;
import tsp.headdb.core.command.CommandHelp;
import tsp.headdb.core.command.CommandMain;
import tsp.headdb.core.command.CommandManager;
import tsp.headdb.core.command.CommandSearch;

import tsp.headdb.core.listener.PlayerJoinListener;
import tsp.headdb.core.storage.Storage;
import tsp.headdb.core.task.UpdateTask;

import tsp.smartplugin.SmartPlugin;
import tsp.smartplugin.inventory.PaneListener;
import tsp.smartplugin.localization.TranslatableLocalization;
import tsp.smartplugin.logger.PluginLogger;

import java.io.IOException;
import java.net.URISyntaxException;

public class HeadDB extends SmartPlugin {

    private static HeadDB instance;
    private PluginLogger logger;
    private TranslatableLocalization localization;
    private CommandManager commandManager;
    private Storage storage;

    @Override
    public void onStart() {
        instance = this;
        instance.logger = new PluginLogger(this, getConfig().getBoolean("debug"));
        instance.logger.info("Loading HeadDB - " + instance.getDescription().getVersion());
        instance.saveDefaultConfig();
        instance.commandManager = new CommandManager();

        new UpdateTask(getConfig().getLong("refresh", 3600L)).schedule(this);

        instance.logger.info("Loaded " + loadLocalization() + " languages!");

        instance.storage = new Storage();
        instance.storage.load();

        new PaneListener(this);
        new PlayerJoinListener();

        loadCommands();
        //noinspection ConstantConditions
        instance.getCommand("headdb").setExecutor(new CommandMain());

        new Metrics(this, 9152);
        instance.logger.info("Done!");
    }

    @Override
    public void onDisable() {
        //instance.storage.save();
    }

    private int loadLocalization() {
        instance.localization = new TranslatableLocalization(this, "messages");
        try {
            instance.localization.createDefaults();
            return instance.localization.load();
        } catch (URISyntaxException | IOException ex) {
            instance.logger.error("Failed to load localization!");
            ex.printStackTrace();
            this.setEnabled(false);
            return 0;
        }
    }

    private void loadCommands() {
        new CommandHelp().register();
        new CommandCategory().register();
        new CommandSearch().register();
    }

    public Storage getStorage() {
        return storage;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public TranslatableLocalization getLocalization() {
        return localization;
    }

    public PluginLogger getLog() {
        return logger;
    }

    public static HeadDB getInstance() {
        return instance;
    }

}
