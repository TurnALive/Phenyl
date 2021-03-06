package live.turna.phenyl.common.database.sql;

import live.turna.phenyl.common.database.Player;

import java.util.List;

/**
 * <b>SQLQuery</b><br>
 *
 * @see AbstractSQLStorage
 * @since 2022/3/4 21:18
 */
public interface SQLQuery {
    /**
     * Initialize tables for sqlite.
     */
    void initTables();

    /**
     * Get a player instance from database.
     *
     * @param selectColumn The column name of selecting.
     * @param selectValue  The corresponding value.
     * @return A Player instance in 3 circumstances. 1). Fully bound player, with valid id, uuid, qqid, mcname; 2). Registered but not bound
     * player, with valid id, uuid but <b>NULL qqid and mcname</b>. 3). Not registered player, all 4 columns are NULL.
     */
    Player getPlayer(String selectColumn, String selectValue);

    /**
     * Get a list of players instances from database.
     *
     * @param selectColumn The column name of selecting.
     * @param selectValue  The corresponding value.
     * @return An empty list if not any player found, or a list of player instances.
     */
    List<Player> getPlayerList(String selectColumn, String selectValue);

    /**
     * Get a list of player of which the selected column IS NOT NULL.
     *
     * @param selectColumn The column to select.
     * @return An empty list if not any player found, or a list of player instances.
     */
    List<Player> getNotNUllPlayerList(String selectColumn);

    /**
     * Update players in database.
     *
     * @param setColumn    The column to set.
     * @param setValue     The value to be set.
     * @param selectColumn The selecting column.
     * @param selectValue  The selecting value.
     * @return True - the update done successfully. False - query failed.
     */
    boolean updatePlayer(String setColumn, String setValue, String selectColumn, String selectValue);

    /**
     * Insert a player.
     *
     * @param insertColumns The column to insert into.
     * @param insertValues  The value to insert.
     * @return True - the insert query was done successfully. False - query failed.
     */
    boolean insertPlayer(String insertColumns, String insertValues);

    /**
     * Insert a message.
     *
     * @param insertColumns The column to insert into.
     * @param insertValues  The value to insert.
     * @return True - the insert query was done successfully. False - query failed.
     */
    boolean insertMessage(String insertColumns, String insertValues);
}
