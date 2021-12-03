package live.turna.phenyl;

import live.turna.phenyl.commands.CommandHandler;
import live.turna.phenyl.config.PhenylConfiguration;
import live.turna.phenyl.message.I18n;

import static live.turna.phenyl.message.I18n.i18n;

import live.turna.phenyl.mirai.MiraiEvent;
import live.turna.phenyl.mirai.MiraiHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Logger;

public final class Phenyl extends Plugin {
    static final Logger LOGGER = Logger.getLogger("Phenyl");

    private static Phenyl instance;
    private transient I18n i18nInstance;
    private transient MiraiEvent miraiEventInstance;

    public static Phenyl getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        instance = this;
        PhenylConfiguration.loadPhenylConfiguration();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandHandler("phenyl"));
        i18nInstance = new I18n();
        i18nInstance.onEnable();
        i18nInstance.updateLocale(PhenylConfiguration.locale);
        LOGGER.info(i18n("configLoaded"));
        new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
        MiraiHandler.logIn();
        miraiEventInstance = new MiraiEvent();
        miraiEventInstance.listenEvents();
    }

    public void reload() {
        PhenylConfiguration.loadPhenylConfiguration();
        i18nInstance.updateLocale(PhenylConfiguration.locale);
        miraiEventInstance.removeListeners();
        MiraiHandler.logOut();
        new MiraiHandler(PhenylConfiguration.user_id, PhenylConfiguration.user_pass, PhenylConfiguration.login_protocol);
        MiraiHandler.logIn();
        miraiEventInstance.listenEvents();
    }

    @Override
    public void onDisable() {
        if (i18nInstance != null) {
            i18nInstance.onDisable();
        }
        MiraiHandler.logOut();
        miraiEventInstance.removeListeners();
    }
}
