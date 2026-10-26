package fr.elias.npcs.api;

import fr.elias.npcs.data.INPCData;
import fr.elias.npcs.data.INPCEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ModeledNPCs Public API
 *
 * Complete API for NPC management, AI, pathfinding, escorts,
 * ModelEngine/MythicMobs integration, traders, quests, and dialogs.
 *
 * Call operations on the Bukkit server thread. Optional integrations must be installed.
 * API 8.1 additions require a plugin build that bundles API 8.1.
 *
 * @version 8.1
 * @author Elias
 */
public abstract class ModeledNPCsAPI {

    private static ModeledNPCsAPI instance;

    /**
     * Get the singleton API instance
     * @return The global ModeledNPCsAPI instance
     */
    public static ModeledNPCsAPI get() {
        return instance;
    }

    /**
     * Internal: Register implementation (called by plugin)
     */
    protected static void setInstance(ModeledNPCsAPI api) {
        instance = api;
    }

    // ========================================================================
    // BASIC NPC ACCESS
    // ========================================================================

    /**
     * Get NPC data by ID
     * @param id The NPC ID
     * @return NPCData or null if not found
     */
    public abstract INPCData getNPCById(int id); //  Changed from NPCData to INPCData

    /**
     * Get the in-game entity for an NPC
     * @param id The NPC ID
     * @return The Entity or null if not spawned
     */
    public abstract Entity getEntityByNPCId(int id);

    /**
     * Get all registered NPC IDs
     * @return List of all NPC IDs
     */
    public abstract List<Integer> getAllNPCIds();

    /**
     * Get all loaded NPCs
     * @return Map of NPC ID to NPCData
     */
    public abstract Map<Integer, INPCData> getAllNPCs(); // Changed from NPCData to INPCData

    // ========================================================================
    // DISPLAY NAMES & NAMETAGS
    // ========================================================================

    /**
     * Get NPC display name (priority: customDisplayName > customName > name)
     * @param npcId The NPC ID
     * @return Display name or null
     */
    public abstract String getNPCDisplayName(int npcId);

    /**
     * Get raw display name (may contain color codes like <green>, &a)
     * @param npcId The NPC ID
     * @return Raw display name or null
     */
    public abstract String getNPCDisplayNameRaw(int npcId);

    /**
     * Get formatted display name for a specific player (with PlaceholderAPI)
     * @param viewer The player viewing the name
     * @param npcId The NPC ID
     * @return Formatted display name or null
     */
    public abstract String getNPCDisplayNameFor(Player viewer, int npcId);

    /**
     * Set NPC display name and hologram height
     * @param id The NPC ID
     * @param name The new display name
     * @param height Hologram height offset
     */
    public abstract void setNPCDisplayName(int id, String name, double height);

    /**
     * Hide NPC nametag completely
     * @param npcId The NPC ID
     */
    public abstract void hideNPCNametag(int npcId);

    /**
     * Show NPC nametag with original name
     * @param npcId The NPC ID
     */
    public abstract void showNPCNametag(int npcId);

    /**
     * Check if NPC nametag is hidden
     * @param npcId The NPC ID
     * @return true if hidden, false otherwise
     */
    public abstract boolean isNPCNametagHidden(int npcId);

    /**
     * Reapply custom names and holograms to a specific NPC
     * @param npcId The NPC ID
     */
    public abstract void reapplyNPCName(int npcId);

    /**
     * Reapply custom names and holograms to all NPCs
     */
    public abstract void reapplyAllNPCNames();

    // ========================================================================
    // LOCATION & MOVEMENT
    // ========================================================================

    /**
     * Get NPC location
     * @param npcId The NPC ID
     * @return Location or null if not found
     */
    public abstract Location getNPCLocation(int npcId);

    /**
     * Move NPC to a new location
     * @param npcId The NPC ID
     * @param newLocation The target location
     */
    public abstract void moveNPC(int npcId, Location newLocation);

    // ========================================================================
    // CREATION & DELETION
    // ========================================================================

    /**
     * Create a new NPC
     * @param type "modelengine" or "mythicmobs"
     * @param name The model/mob name
     * @param location Spawn location
     * @return New NPC ID, or -1 if failed
     */
    public abstract int createNPC(String type, String name, Location location);

    /**
     * Delete an NPC completely
     * @param npcId The NPC ID
     */
    public abstract void deleteNPC(int npcId);

    // ========================================================================
    // PERMISSIONS & VISIBILITY
    // ========================================================================

    /**
     * Check if player can view NPC
     * @param npcId The NPC ID
     * @param player The player
     * @return true if player has permission
     */
    public abstract boolean canPlayerViewNPC(int npcId, Player player);

    // ========================================================================
    // COMMANDS
    // ========================================================================

    /**
     * Get NPC commands
     * @param npcId The NPC ID
     * @return List of commands
     */
    public abstract List<String> getNPCCommands(int npcId);

    /**
     * Add command to NPC
     * @param id The NPC ID
     * @param command Command string (e.g., "player: warp spawn")
     */
    public abstract void addCommandToNPC(int id, String command);

    /**
     * Run all NPC commands for a player
     * @param id The NPC ID
     * @param player The player
     */
    public abstract void runNPCCommands(int id, Player player);

    // ========================================================================
    // EFFECTS
    // ========================================================================

    /**
     * Get NPC effects
     * @param npcId The NPC ID
     * @return List of NPCEffect
     */
    public abstract List<INPCEffect> getNPCEffects(int npcId); // Changed from NPCEffect to INPCEffect

    /**
     * Play interaction effects
     * @param id The NPC ID
     * @param player The player who interacted
     */
    public abstract void playInteractEffects(int id, Player player);

    /**
     * Restart looped effects
     * @param id The NPC ID
     */
    public abstract void restartLoopedEffect(int id);

    // ========================================================================
    // UPDATE & RESPAWN
    // ========================================================================

    /**
     * Update NPC state and visuals
     * @param id The NPC ID
     */
    public abstract void updateNPC(int id);

    /**
     * Respawn NPC
     * @param id The NPC ID
     */
    public abstract void respawnNPC(int id);

    // ========================================================================
    // AUTO-LOOK
    // ========================================================================

    /**
     * Enable auto-look (NPC looks at nearby players)
     * @param id The NPC ID
     */
    public abstract void enableAutoLook(int id);

    /**
     * Disable auto-look
     * @param id The NPC ID
     */
    public abstract void disableAutoLook(int id);

    // ========================================================================
    // AI & PATHFINDING
    // ========================================================================

    /**
     * Add waypoint to NPC route
     * @param npcId The NPC ID
     * @param location Waypoint location
     */
    public abstract void addRouteWaypoint(int npcId, Location location);

    /**
     * Clear all route waypoints
     * @param npcId The NPC ID
     */
    public abstract void clearRoute(int npcId);

    // ========================================================================
    // ESCORT SYSTEM
    // ========================================================================

    /**
     * Start escort for player (uses default route)
     * @param npcId The NPC ID
     * @param player The player to escort
     */
    public abstract void startEscort(int npcId, Player player);

    /**
     * Stop escort for player
     * @param playerId The player UUID
     */
    public abstract void stopEscort(UUID playerId);

    /**
     * Set escort animation for NPC
     * @param npcId The NPC ID
     * @param animationName ModelEngine animation name
     */
    public abstract void setEscortAnimation(int npcId, String animationName);

    /**
     * Get escort animation for NPC
     * @param npcId The NPC ID
     * @return Animation name or null
     */
    public abstract String getEscortAnimation(int npcId);

    // ========================================================================
    // TRADER SYSTEM
    // ========================================================================

    /**
     * Open trader GUI for player
     * @param player The player
     * @param npcId The trader NPC ID
     */
    public abstract void openTraderGUI(Player player, int npcId);

    // ========================================================================
    // QUEST SYSTEM
    // ========================================================================

    /**
     * Assign quest to player
     * @param player The player
     * @param questName Quest name/ID
     */
    public abstract void assignQuest(Player player, String questName);

    // ========================================================================
    // DIALOG SYSTEM
    // ========================================================================

    /**
     * Open dialog for player
     * @param player The player
     * @param npcId The NPC ID
     */
    public abstract void openDialog(Player player, int npcId);

    // ========================================================================
    // GLOWING SYSTEM
    // ========================================================================

    /**
     * Set NPC glow color for a specific player
     * @param npcId The NPC ID
     * @param player The player who will see the glow
     * @param color The glow color (e.g., ChatColor.RED, ChatColor.YELLOW, ChatColor.GREEN)
     */
    public abstract void setNPCGlow(int npcId, Player player, org.bukkit.ChatColor color);

    /**
     * Remove NPC glow for a specific player
     * @param npcId The NPC ID
     * @param player The player
     */
    public abstract void removeNPCGlow(int npcId, Player player);

    /**
     * Set NPC glow color for all online players
     * @param npcId The NPC ID
     * @param color The glow color
     */
    public abstract void setNPCGlowForAll(int npcId, org.bukkit.ChatColor color);

    /**
     * Remove NPC glow for all online players
     * @param npcId The NPC ID
     */
    public abstract void removeNPCGlowForAll(int npcId);

    /**
     * Toggle NPC glow on/off for a specific player
     * @param npcId The NPC ID
     * @param player The player
     * @param enabled Whether to enable or disable glow
     * @param color The glow color (only used if enabled is true)
     */
    public abstract void toggleNPCGlow(int npcId, Player player, boolean enabled, org.bukkit.ChatColor color);

    // ========================================================================
    // HOVER SYSTEM
    // ========================================================================

    /**
     * Get the NPC the player is currently aiming at (hover detection).
     * @param player The player
     * @return NPC ID, or -1 if the player is not hovering any NPC
     */
    public abstract int getHoveredNPCId(Player player);

    /**
     * Check if a player is currently hovering a specific NPC.
     * @param player The player
     * @param npcId The NPC ID
     * @return true if the player's crosshair is aimed at this NPC
     */
    public abstract boolean isPlayerHoveringNPC(Player player, int npcId);

    /**
     * Enable or disable hover detection for a specific NPC.
     * Overrides the global npc.hover.enabled config on a per-NPC basis.
     * Persisted to npc-extras.yml.
     * @param npcId The NPC ID
     * @param enabled true to enable, false to disable
     */
    public abstract void setNPCHoverEnabled(int npcId, boolean enabled);

    /**
     * Check if hover detection is enabled for a specific NPC.
     * @param npcId The NPC ID
     * @return true if hover is enabled for this NPC
     */
    public abstract boolean isNPCHoverEnabled(int npcId);

    /**
     * Set a per-NPC action-bar hint shown when a player hovers this NPC.
     * Pass an empty string to fall back to the global hint_text in config.yml.
     * Persisted to npc-extras.yml.
     * @param npcId The NPC ID
     * @param hint Hint text (supports &amp; color codes). Empty = use global default.
     */
    public abstract void setNPCHoverHint(int npcId, String hint);

    /**
     * Get the per-NPC hover hint text.
     * @param npcId The NPC ID
     * @return hint text, or empty string if using the global default
     */
    public abstract String getNPCHoverHint(int npcId);

    /**
     * Set a ModelEngine animation to loop while this NPC is hovered.
     * The animation starts when the first player begins hovering and reverts to
     * the normal idle animation when all players stop hovering.
     * Pass an empty string to disable (keep idle animation during hover).
     * Persisted to npc-extras.yml.
     * @param npcId     The NPC ID
     * @param animation ModelEngine animation name (e.g. "wave"), or empty string to disable
     */
    public abstract void setNPCHoverAnimation(int npcId, String animation);

    /**
     * Get the ModelEngine animation configured to play while this NPC is hovered.
     * @param npcId The NPC ID
     * @return animation name, or empty string if none configured
     */
    public abstract String getNPCHoverAnimation(int npcId);

    // ========================================================================
    // LUXDIALOGUES INTEGRATION
    // ========================================================================

    /**
     * Check if the LuxDialogues plugin is installed and the integration is active.
     * @return true if available
     */
    public abstract boolean isLuxDialoguesAvailable();

    /**
     * Open the LuxDialogues dialogue configured for this NPC.
     * Reads lux_dialogue and lux_first_page from npc-extras.yml.
     * @param player The player
     * @param npcId The NPC ID
     * @return true if the dialogue was triggered, false if not configured or unavailable
     */
    public abstract boolean triggerLuxDialogue(Player player, int npcId);

    /**
     * Check if a player is currently inside a LuxDialogues dialogue.
     * @param player The player
     * @return true if in dialogue
     */
    public abstract boolean isPlayerInLuxDialogue(Player player);

    /**
     * Set the LuxDialogues dialogue ID for an NPC.
     * Persisted to npc-extras.yml.
     * @param npcId The NPC ID
     * @param dialogueId The LuxDialogues dialogue ID
     */
    public abstract void setNPCLuxDialogue(int npcId, String dialogueId);

    /**
     * Get the LuxDialogues dialogue ID configured for an NPC.
     * @param npcId The NPC ID
     * @return dialogue ID, or empty string if not configured
     */
    public abstract String getNPCLuxDialogue(int npcId);

    /**
     * Set the first page of the LuxDialogues dialogue for an NPC.
     * Persisted to npc-extras.yml.
     * @param npcId The NPC ID
     * @param page Page identifier (e.g. "1")
     */
    public abstract void setNPCLuxFirstPage(int npcId, String page);

    /**
     * Get the LuxDialogues first page configured for an NPC.
     * @param npcId The NPC ID
     * @return page identifier, defaults to "1"
     */
    public abstract String getNPCLuxFirstPage(int npcId);

    // ========================================================================
    // NPC STATES
    // ========================================================================

    /**
     * Set the active state for an NPC (e.g. "idle", "busy", "combat").
     * The state drives the animation and hologram suffix defined in npc-extras.yml.
     * Persisted to npc-extras.yml.
     * @param npcId The NPC ID
     * @param state State name. Use "default" to reset.
     */
    public abstract void setNPCState(int npcId, String state);

    /**
     * Get the active state for an NPC.
     * @param npcId The NPC ID
     * @return state name, defaults to "default"
     */
    public abstract String getNPCState(int npcId);

    // ========================================================================
    // SCHEDULE
    // ========================================================================

    /**
     * Check if an NPC is currently within its configured active schedule window.
     * Returns true if no schedule is configured (always active).
     * @param npcId The NPC ID
     * @return true if the NPC should be visible/interactable right now
     */
    public abstract boolean isNPCActive(int npcId);

    /**
     * Set the in-game hour schedule window for an NPC.
     * Outside this window the NPC is treated as inactive.
     * Persisted to npc-extras.yml. Pass -1 for both to disable scheduling.
     * @param npcId The NPC ID
     * @param startHour In-game hour to become active (0–23)
     * @param endHour   In-game hour to become inactive (0–23)
     */
    public abstract void setNPCSchedule(int npcId, int startHour, int endHour);

    /**
     * Get the configured schedule start hour for an NPC.
     * @param npcId The NPC ID
     * @return start hour (0–23), or -1 if no schedule is set
     */
    public abstract int getNPCScheduleStart(int npcId);

    /**
     * Get the configured schedule end hour for an NPC.
     * @param npcId The NPC ID
     * @return end hour (0–23), or -1 if no schedule is set
     */
    public abstract int getNPCScheduleEnd(int npcId);

    // ========================================================================
    // HOLOGRAM ANIMATION
    // ========================================================================

    /**
     * Set animated hologram frames for an NPC.
     * The plugin cycles through these frames at the configured interval.
     * Persisted to npc-extras.yml.
     * @param npcId  The NPC ID
     * @param frames List of text frames (supports &amp; color codes)
     */
    public abstract void setNPCHologramFrames(int npcId, List<String> frames);

    /**
     * Get the configured hologram animation frames for an NPC.
     * @param npcId The NPC ID
     * @return list of frames, or empty list if none configured
     */
    public abstract List<String> getNPCHologramFrames(int npcId);

    /**
     * Set the interval in ticks between hologram frame changes.
     * Persisted to npc-extras.yml.
     * @param npcId  The NPC ID
     * @param ticks  Interval in ticks (e.g. 20 = 1 second)
     */
    public abstract void setNPCHologramFrameInterval(int npcId, int ticks);

    /**
     * Get the hologram animation frame interval for an NPC.
     * @param npcId The NPC ID
     * @return interval in ticks, defaults to 20
     */
    public abstract int getNPCHologramFrameInterval(int npcId);

    // Extended API (8.1). Public types do not depend on implementation packages.

    /** Gets or creates the default route. Mutations on the handle are persisted. */
    public abstract fr.elias.npcs.data.INPCRoute getOrCreateRoute(int npcId);
    /** Gets or creates a named route. */
    public abstract fr.elias.npcs.data.INPCRoute getOrCreateRoute(int npcId, String routeId);
    /** Returns the named route, or null if it does not exist. */
    public abstract fr.elias.npcs.data.INPCRoute getRoute(int npcId, String routeId);
    /** Sets the default route's traversal mode and saves it. */
    public abstract void setRouteMode(int npcId, fr.elias.npcs.data.RouteMode mode);
    /** Sets a finite positive default-route speed in blocks per tick and saves it. */
    public abstract void setRouteSpeed(int npcId, double speed);
    /** Starts an escort with route, arrival and walking-dialog options. */
    public abstract void startEscort(int npcId, Player player, fr.elias.npcs.data.EscortOptions options);
    /** Applies a ModelEngine blueprint to an entity. Requires ModelEngine. */
    public abstract void applyModelEngineModel(org.bukkit.entity.LivingEntity entity, String blueprint, int npcId);
    /** Respawns an existing ModelEngine NPC; ignores missing or other-type NPCs. */
    public abstract void respawnModelEngineNPC(int id);
    /** Spawns a MythicMobs NPC. Caller must supply an unused ID and UUID. */
    public abstract void spawnMythicMobsNPC(int id, String mobName, Location location,
            List<String> commands, String permission, UUID internalUUID);
    /** Spawns a MythicMobs NPC with display and auto-look settings. */
    public abstract void spawnMythicMobsNPCFull(int id, String mobName, Location location,
            List<String> commands, String permission, UUID internalUUID, String customName,
            String displayName, double hologramHeight, boolean autoLook);
    /** Reapplies a MythicMobs NPC's current names using its registered data. */
    public abstract void updateMythicMobName(int id);
    /** Respawns an existing MythicMobs NPC; ignores missing or other-type NPCs. */
    public abstract void respawnMythicMobNPC(int id);
    /** Returns trader operations, or null while the trader service is unavailable. */
    public abstract TraderAPI getTraderManager();
    /** Gets quest status; returns null if no integration recognises the quest. */
    public abstract fr.elias.npcs.data.QuestStatus getQuestStatus(Player player, String questName);
}
