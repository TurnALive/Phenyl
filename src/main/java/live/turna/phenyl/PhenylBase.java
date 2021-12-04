package live.turna.phenyl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class with Logger and Phenyl instance.
 *
 * @author xslingcn
 * @version 1.0
 * @since 2021/12/3 22:18
 */
public class PhenylBase {
    public static final Logger LOGGER = LogManager.getLogger("Phenyl");
    public static Phenyl phenyl = Phenyl.getInstance();
}