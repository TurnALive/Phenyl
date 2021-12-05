package live.turna.phenyl.bind;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <b>BindArray</b><br>
 * {@link java.util.ArrayList} implementation specified to store bind requests.
 *
 * @since 2021/12/5 3:36
 */
public class BindArray extends ArrayList<BindMap> {

    public ArrayList<BindMap> instance;

    public BindArray(){
        this.instance = new ArrayList<>();
    }

    /**
     * Add a BindMap object to BindArray.
     * Tries to remove all existing objects that has same elements as the provided one in array before adding to keep requests unique.
     *
     * @param map The BindMap.
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(BindMap map){
        if(map == null) return false;
        this.remove(map.getUserName());
        this.remove(map.getUserID());
        this.remove(map.getCode());
        instance.add(map);
        return true;
    }

    /**
     * Try to match the BindArray that contains all BindMaps which matches the key.
     *
     * @param key The string key to proceed search in BindArray. Could be userName or verification code.
     * @return The array object or null for no corresponding result.
     */
    @Nullable
    public BindArray get(String key){
        BindArray matches = new BindArray();
        for(BindMap entry : instance){
            matches.add(entry.match(key));
        }
        return matches.isEmpty()?null:matches;
    }

    /**
     * Try to match the BindArray that contains all BindMaps that matches the key.
     *
     * @param key The key to proceed search in BindArray, which is userID.
     * @return The array object or null for no corresponding result.
     */
    @Nullable
    public BindArray get(Long key){
        BindArray matches = new BindArray();
        for(BindMap entry : instance){
            matches.add(entry.match(key));
        }
        return matches.isEmpty()?null:matches;
    }

    /**
     * Try to remove all BindMaps that matches the key.
     *
     * @param key The string key to proceed search in BindArray. Could be userName or verification code.
     */
    public void remove(String key){
        BindArray matches = this.get(key);
        if(matches==null) return;
        for(BindMap entry : matches){
            instance.remove(entry);
        }
    }

    /**
     * Try to remove all BindMaps that matches the key.
     *
     * @param key The key to proceed search in BindArray, which is userID.
     */
    public void remove(Long key){
        BindArray matches = this.get(key);
        if(matches==null) return;
        for(BindMap entry : matches){
            instance.remove(entry);
        }
    }
}