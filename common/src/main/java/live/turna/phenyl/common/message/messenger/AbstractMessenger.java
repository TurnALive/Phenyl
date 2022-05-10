package live.turna.phenyl.common.message.messenger;

import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.database.Player;
import live.turna.phenyl.common.instance.PSender;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <b>AbstractMessenger</b><br>
 * *
 *
 * @since 2022/4/11 10:45
 */
public abstract class AbstractMessenger<P extends AbstractPhenyl> implements ServerMessenger, MiraiMessenger {
    private static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";

    protected final transient P phenyl;

    public AbstractMessenger(P plugin) {
        phenyl = plugin;
    }

    public static String altColor(String message) {
        char[] c = message.toCharArray();
        for (int i = 0; i < c.length - 1; i++) {
            if (c[i] == '&' && ALL_CODES.indexOf(c[i + 1]) > -1) {
                c[i] = '\u00A7';
                c[i + 1] = Character.toLowerCase(c[i + 1]);
            }
        }
        return String.valueOf(c);
    }

    public static NamedTextColor altColor(char color) {
        return switch (color) {
            case '0' -> NamedTextColor.BLACK;
            case '1' -> NamedTextColor.DARK_BLUE;
            case '2' -> NamedTextColor.DARK_GREEN;
            case '3' -> NamedTextColor.DARK_AQUA;
            case '4' -> NamedTextColor.DARK_RED;
            case '5' -> NamedTextColor.DARK_PURPLE;
            case '6' -> NamedTextColor.GOLD;
            case '7' -> NamedTextColor.GRAY;
            case '8' -> NamedTextColor.DARK_GRAY;
            case '9' -> NamedTextColor.BLUE;
            case 'a' -> NamedTextColor.GREEN;
            case 'b' -> NamedTextColor.AQUA;
            case 'c' -> NamedTextColor.RED;
            case 'd' -> NamedTextColor.LIGHT_PURPLE;
            case 'e' -> NamedTextColor.YELLOW;
            case 'f' -> NamedTextColor.WHITE;
            default -> NamedTextColor.WHITE;
        };
    }

    public void sendGroup(Group group, String message) {
        group.sendMessage(message);
    }

    public void sendGroup(Group group, MessageChain message) {
        group.sendMessage(message);
    }

    public void sendAllGroup(String message) {
        sendAllGroup(new MessageChainBuilder().append(message).build());
    }

    public void sendAllGroup(MessageChain message) throws NoSuchElementException {
        for (Long id : Config.enabled_groups) {
            try {
                phenyl.getMirai().getBot().getGroupOrFail(id).sendMessage(message);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException(String.valueOf(id));
            }
        }
    }

    public void sendImage(Group group, BufferedImage image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", stream);
        } catch (IOException e) {
            if (Config.debug) e.printStackTrace();
        }
        ExternalResource resource = ExternalResource.Companion.create(stream.toByteArray());
        try {
            Image img = ExternalResource.uploadAsImage(resource, group);
            MessageChain message = new MessageChainBuilder().append(img).build();
            group.sendMessage(message);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.valueOf(group.getId()));
        }
        try {
            resource.close();
        } catch (IOException e) {
            if (Config.debug) e.printStackTrace();
        }
    }

    public void sendImageToAll(BufferedImage image) throws NoSuchElementException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", stream);
        } catch (IOException e) {
            if (Config.debug) e.printStackTrace();
        }
        ExternalResource resource = ExternalResource.Companion.create(stream.toByteArray());
        for (Long id : Config.enabled_groups) {
            try {
                Group group = phenyl.getMirai().getBot().getGroupOrFail(id);
                Image img = ExternalResource.uploadAsImage(resource, group);
                MessageChain message = new MessageChainBuilder().append(img).build();
                group.sendMessage(message);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException(String.valueOf(id));
            }
        }
        try {
            resource.close();
        } catch (IOException e) {
            if (Config.debug) e.printStackTrace();
        }
    }

    public Player getNoMessage(String uuid) {
        AtomicReference<Player> found = new AtomicReference<>(new Player(null, null, null, null));
        phenyl.getNoMessagePlayer().forEach(noMessaged -> {
            if (noMessaged.uuid() == null) return;
            if (noMessaged.uuid().equals(uuid)) found.set(noMessaged);
        });
        return found.get();
    }

    public Player getMuted(String uuid) {
        AtomicReference<Player> found = new AtomicReference<>(new Player(null, null, null, null));
        phenyl.getMutedPlayer().forEach(muted -> {
            if (muted.uuid() == null) return;
            if (muted.uuid().equals(uuid)) found.set(muted);
        });
        return found.get();
    }

    public void sendPlayer(String message, PSender player) {
        TextComponent result = Component.text(altColor("&7[Phenyl] " + message));
        player.sendMessage(result);
    }

    public void sendPlayer(Component message, PSender player) {
        TextComponent result = Component
                .text(altColor("&7[Phenyl] "))
                .append(message);
        player.sendMessage(result);
    }

    public void sendAllServer(String message) {
        TextComponent result = Component.text(altColor(message));
        sendAllServer(result);
    }
}