package live.turna.phenyl.listener;

import live.turna.phenyl.PhenylBase;
import live.turna.phenyl.listener.mirai.OnBotOfflineEvent;
import live.turna.phenyl.listener.mirai.OnGroupMessageEvent;
import net.md_5.bungee.api.ProxyServer;

/**
 * <b>Listener</b><br>
 * Register events to Bungeecord plugin manager.
 *
 * @since 2021/12/4 22:42
 */
public class ListenerRegisterer extends PhenylBase {
    public static void registerListeners() {
        ProxyServer.getInstance().getPluginManager().registerListener(phenyl, new OnBotOfflineEvent());
        ProxyServer.getInstance().getPluginManager().registerListener(phenyl, new OnGroupMessageEvent());
    }

    public static void unregisterListeners() {
        ProxyServer.getInstance().getPluginManager().unregisterListeners(phenyl);
    }
}