package live.turna.phenyl.common.dependency;

import com.google.common.base.Suppliers;
import live.turna.phenyl.common.config.Config;
import live.turna.phenyl.common.plugin.AbstractPhenyl;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashSet;
import java.util.Set;

import static live.turna.phenyl.common.message.I18n.i18n;

/**
 * <b>DependencyManager</b><br>
 * Manager to download, verify dependencies and load them to classpath.
 *
 * @since 2022/1/19 19:44
 */
public class DependencyManager {
    private final transient AbstractPhenyl phenyl;
    private final transient Logger LOGGER;

    public DependencyManager(AbstractPhenyl plugin) {
        phenyl = plugin;
        LOGGER = phenyl.getLogger();
    }

    /**
     * Convert a byte array to hex string.
     *
     * @param bytes The byte array to be converted.
     * @return The result string.
     */
    static String toHex(byte[] bytes) {
        char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Get and load all dependencies.
     *
     * @return Whether succeeded loading the dependencies.
     */
    public boolean onEnable() {
        Set<Dependency> dependencies = getDependencies();
        try {
            loadDependencies(dependencies);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
            if (Config.debug) e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Decide the list of dependencies to load.
     *
     * @return A set of dependencies.
     */
    private Set<Dependency> getDependencies() {
        Set<Dependency> dependencies = new LinkedHashSet<>();
        switch (Config.storage) {
            case "sqlite" -> dependencies.add(Dependency.SQLITE);
            case "mysql" -> {
                dependencies.add(Dependency.HIKARI);
                dependencies.add(Dependency.MYSQL);
            }
            case "postgresql" -> {
                dependencies.add(Dependency.HIKARI);
                dependencies.add(Dependency.POSTGRESQL);
            }
            default -> {
            }
        }

        switch (phenyl.getPlatform().toLowerCase()) {
            case "bungee" -> {
                dependencies.add(Dependency.ADVENTUREAPI);
                dependencies.add(Dependency.ADVENTUREBUNGEE);
                dependencies.add(Dependency.ADVENTURESERIALIZERBUNGEE);
            }
        }

        dependencies.add(Dependency.GENEREX);
        dependencies.add(Dependency.JACKSON);
        dependencies.add(Dependency.AUTOMATON);
        dependencies.add(Dependency.MIRAI);
        return dependencies;
    }

    /**
     * Load all dependencies to classpath.
     *
     * @param dependencies The dependencies to be loaded.
     * @throws IOException Failed retrieving a dependency from remote or local.
     */
    private void loadDependencies(Set<Dependency> dependencies) throws IOException {
        for (Dependency dependency : dependencies) {
            Suppliers.memoize(() -> URLClassLoaderAccess.create((URLClassLoader) phenyl.getClass().getClassLoader()))
                    .get().addURL(getDependency(dependency).toURI().toURL());
            LOGGER.info(i18n("libLoaded", dependency.getFileName()));
        }
    }

    /**
     * Get certain dependency file.
     *
     * @param dependency The dependency to be located.
     * @return The jar file.
     * @throws IOException Failed while processing files.<br/>
     *                     1).failLibDelete: Failed to delete an existing and broken jar file.
     *                     2).failLibDown: Failed to download the file from remote.
     */
    private File getDependency(Dependency dependency) throws IOException {
        File jarFile = new File(phenyl.getDir(), "libs/" + dependency.getFileName());
        File md5File = new File(phenyl.getDir(), "libs/" + dependency.getFileName() + ".md5");
        if (jarFile.exists() && checkDependency(dependency)) return jarFile;
        for (DependencyRepository repo : DependencyRepository.values()) {
            if (jarFile.exists() && !jarFile.delete())
                throw new IOException(i18n("failLibDelete", jarFile.getPath()));
            if (md5File.exists() && !md5File.delete())
                throw new IOException(i18n("failLibDelete", md5File.getPath()));
            if (!jarFile.createNewFile() || !md5File.createNewFile())
                throw new IOException(i18n("failLibDown", dependency.getFileName()));

            LOGGER.info(i18n("downloadingLib", repo.url + dependency.getMavenRepoPath()));
            try {
                if (!repo.download(dependency.getMavenRepoPath(), jarFile))
                    LOGGER.warn(i18n("failLibDown", jarFile.getPath()));
                if (!repo.download(dependency.getMavenRepoPath() + ".md5", md5File))
                    LOGGER.warn(i18n("failLibDown", md5File.getPath()));
            } catch (IOException e) {
                LOGGER.warn(i18n("failLibDown", dependency.getFileName()) + " " + e.getLocalizedMessage());
                if (Config.debug) e.printStackTrace();
            }
            if (checkDependency(dependency)) return jarFile;
        }
        throw new IOException(i18n("failLibDown", dependency.getFileName()));
    }

    /**
     * Check if the dependency matches the MD5 digestion from maven.
     *
     * @param dependency The dependency to be checked.
     * @return Whether matches.
     * @throws IOException Failed reading jar or md5 file.
     */
    private boolean checkDependency(Dependency dependency) throws IOException {
        File jarFile = new File(phenyl.getDir(), "libs/" + dependency.getFileName());
        File md5File = new File(phenyl.getDir(), "libs/" + dependency.getFileName() + ".md5");
        if (!jarFile.exists() || !md5File.exists()) return false;
        try {
            String jarDigest = toHex(MessageDigest.getInstance("md5").digest(Files.readAllBytes(jarFile.toPath())));
            String md5Digest = Files.readString(md5File.toPath(), StandardCharsets.UTF_8).replace("\n", "");
            return jarDigest.equals(md5Digest);
        } catch (NoSuchAlgorithmException e) {
            if (Config.debug) e.printStackTrace();
            return false;
        }
    }
}