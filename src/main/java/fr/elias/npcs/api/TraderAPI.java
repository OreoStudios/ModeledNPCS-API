package fr.elias.npcs.api;

import org.bukkit.entity.Player;

/** Trader operations. Call on the server thread; requires configured traders. */
public interface TraderAPI {
    void reload();
    boolean hasTrader(int npcId);
    /** Returns null if no trader exists. */
    String getTraderDisplayName(int npcId);
    void openTrade(Player player, int npcId);
    /** Returns null if no offset is configured. */
    Double getTraderHologramOffset(int npcId);
    Double getDefaultHologramOffset();
}
