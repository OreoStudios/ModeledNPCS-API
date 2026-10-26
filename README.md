# ModeledNPCS API 8.1

The ModeledNPCS API lets Paper plugins inspect and control ModeledNPCS NPCs without depending on ModeledNPCS implementation classes. It includes NPC data, display names, visibility, commands, effects, movement, routes, escorts, dialog, traders, quests, glow, hover, schedules, hologram animation, and optional ModelEngine, MythicMobs, and LuxDialogues integrations.

| | |
|---|---|
| API artifact | `fr.elias:ModeledNPCs-API:8.1` |
| Java | 17 bytecode; compatible with Java 17 and newer |
| Server API | Bukkit/Paper API; example targets Paper 1.21.8 |
| Maven releases | `https://maven.oreostudios.fr/releases` |
| Plugin source | [OreoStudios/ModeledNPCS](https://github.com/OreoStudios/ModeledNPCS) |

API 8.1 adds route handles, route speed and mode controls, configurable escort options, public quest status, trader access, and model integration operations. The API is supplied by ModeledNPCS at runtime. Your plugin should compile against the API as `provided`, and must not bundle or shade its classes.

## Install with Maven

Add the OreoStudios release repository and API dependency to your plugin's `pom.xml`:

```xml
<repositories>
    <repository>
        <id>oreostudios-releases</id>
        <url>https://maven.oreostudios.fr/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>fr.elias</groupId>
        <artifactId>ModeledNPCs-API</artifactId>
        <version>8.1</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Then build your plugin as usual:

```sh
mvn clean package
```

For Gradle, add the Maven repository and `compileOnly` dependency:

```kotlin
repositories {
    maven("https://maven.oreostudios.fr/releases")
}

dependencies {
    compileOnly("fr.elias:ModeledNPCs-API:8.1")
}
```

## Declare the runtime dependency

Declare ModeledNPCS as a required dependency in your `plugin.yml`. Bukkit will load ModeledNPCS first, so its API is registered before your plugin enables.

```yaml
name: ExampleIntegration
version: 1.0.0
main: com.example.integration.ExampleIntegrationPlugin
api-version: '1.21'
depend: [ModeledNPCS]
```

Install ModeledNPCS on the server. Integrations such as ModelEngine, MythicMobs, LuxDialogues, or quest plugins are only needed for the matching operations; check availability where the API offers a check. PacketEvents and other ModeledNPCS server dependencies are handled by the ModeledNPCS installation, not by shading this API into your plugin.

## Get the API

`ModeledNPCsAPI.get()` returns the registered service, or `null` if ModeledNPCS has not enabled. With `depend: [ModeledNPCS]`, check once in `onEnable` and keep the reference:

```java
package com.example.integration;

import fr.elias.npcs.api.ModeledNPCsAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExampleIntegrationPlugin extends JavaPlugin {
    private ModeledNPCsAPI modeledNPCs;

    @Override
    public void onEnable() {
        modeledNPCs = ModeledNPCsAPI.get();
        if (modeledNPCs == null) {
            getLogger().severe("ModeledNPCS did not register its API");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Connected to ModeledNPCS API");
    }
}
```

The earlier `ModeledNPCsProvider` remains available for code using the legacy `IModeledNPCsAPI`. New integrations should use `ModeledNPCsAPI`.

## Threading and return values

Call API methods that read or change Bukkit entities, NPC state, configuration, or inventories on the server's main thread. Do not call them from an asynchronous task. If work starts asynchronously, return to the server thread with your plugin's scheduler before accessing the API.

Methods that look up an NPC may return `null` when its ID is unknown; `getEntityByNPCId` also returns `null` while the NPC has no spawned entity. `getRoute` returns `null` if the requested route does not exist. `createNPC` returns `-1` for an unsupported type or failed creation. `isNPCActive` returns true for NPCs without a configured schedule. Quest status is `null` when no installed quest adapter recognizes the quest.

Returned NPC maps and lists should be treated as snapshots unless a method explicitly returns a live handle. `INPCData` is mutable. Use the API operation for the change you want where possible; after directly changing data, call the relevant update or persistence operation offered by your ModeledNPCS version.

## Quick examples

### List NPCs and inspect one

```java
for (int id : modeledNPCs.getAllNPCIds()) {
    String name = modeledNPCs.getNPCDisplayName(id);
    getLogger().info("NPC " + id + ": " + name);
}

var npc = modeledNPCs.getNPCById(42);
if (npc != null) {
    getLogger().info(npc.getName() + " uses model " + npc.getModelId());
}
```

### Move or create an NPC

```java
var target = player.getLocation().clone();
modeledNPCs.moveNPC(42, target);
modeledNPCs.respawnNPC(42);

int created = modeledNPCs.createNPC("modelengine", "merchant_blueprint", target);
if (created == -1) {
    player.sendMessage("NPC could not be created; check its type and integration.");
}
```

Supported creation types are `modelengine` and `mythicmobs`. Their matching plugin must be installed and configured. The `name` is interpreted by the selected integration.

### Set the name and visibility

```java
modeledNPCs.setNPCDisplayName(42, "&6Town Guide", 2.2);
modeledNPCs.hideNPCNametag(42);
// Later:
modeledNPCs.showNPCNametag(42);
```

`getNPCDisplayName` returns the resolved name. `getNPCDisplayNameRaw` keeps its configured formatting, while `getNPCDisplayNameFor(player, id)` applies PlaceholderAPI when available and returns a legacy formatted string.

### Run commands and effects

```java
modeledNPCs.addCommandToNPC(42, "player: warp town");
modeledNPCs.runNPCCommands(42, player);
modeledNPCs.playInteractEffects(42, player);
modeledNPCs.restartLoopedEffect(42);
```

Use command formats configured for ModeledNPCS. `getNPCCommands` and `getNPCEffects` let you inspect a NPC's configured commands and effects. `updateNPC` refreshes a NPC; `respawnNPC` respawns it.

## Routes and patrols

Routes can have a default route and named routes per NPC. The `INPCRoute` returned by `getOrCreateRoute` is a live handle: its waypoint, mode, and speed changes save through ModeledNPCS. `getRoute` only looks up a route and returns `null` when it does not exist.

```java
import fr.elias.npcs.data.INPCRoute;

INPCRoute route = modeledNPCs.getOrCreateRoute(42, "market-tour");
route.addWaypoint(player.getLocation().clone());
route.addWaypoint(secondStop);
route.setMode("LOOP"); // or "PATROL"
route.setSpeed(0.25);   // finite, positive blocks per tick

// Convenience setters for the default route:
modeledNPCs.setRouteMode(42, fr.elias.npcs.data.RouteMode.PATROL);
modeledNPCs.setRouteSpeed(42, 0.25);

// Inspect or clear a route:
INPCRoute existing = modeledNPCs.getRoute(42, "market-tour");
if (existing != null) {
    var waypoints = existing.getWaypoints(); // detached Location copies; list is unmodifiable
    existing.clearWaypoints();
}
```

Calling `getOrCreateRoute` creates the route when absent. `getRoute` does not create it. Route IDs are normalized by the plugin; use a nonblank ID for named routes. A `Location` added as a waypoint must have a loaded world. Route handles are intended for use on the server thread.

## Escorts

The basic overload starts an escort using the default route:

```java
modeledNPCs.startEscort(42, player);
// End the player's active escort:
modeledNPCs.stopEscort(player.getUniqueId());
```

For a named route and arrival behavior, use immutable `EscortOptions`:

```java
import fr.elias.npcs.data.EscortOptions;
import java.util.List;
import java.util.Map;

var options = new EscortOptions(
    "market-tour",                  // route ID; null selects the default route
    List.of("We made it!"),          // arrival dialog lines
    "market_arrival",               // arrival dialog tree key, or null
    10,                              // arrival timeout, in seconds
    "Are you still with me?",        // timeout message, or null
    List.of("console:say {player} arrived"),
    Map.of("first", new EscortOptions.WalkingDialogEntry(
        List.of("Follow me.", "The market is close."),
        8                               // seconds between lines
    ))
);
modeledNPCs.startEscort(42, player, options);
```

Pass empty lists or a null route/tree/message to omit those optional settings. Walking-dialog keys retain insertion order. Timeouts must be zero or greater; walking-dialog intervals must be positive. Command prefixes and placeholders follow the ModeledNPCS escort configuration.

`setEscortAnimation` and `getEscortAnimation` configure or inspect the ModelEngine animation used during an NPC escort.

## Traders and quests

`getTraderManager` returns `null` if the trader service is unavailable. Check that the NPC has a trader before opening it:

```java
import fr.elias.npcs.api.TraderAPI;

TraderAPI traders = modeledNPCs.getTraderManager();
if (traders != null && traders.hasTrader(42)) {
    String displayName = traders.getTraderDisplayName(42);
    traders.openTrade(player, 42);
}

modeledNPCs.openTraderGUI(player, 42); // convenience operation
```

`TraderAPI` also exposes `reload`, `getTraderHologramOffset`, and `getDefaultHologramOffset`. An unconfigured offset or missing display name returns `null`.

Quest operations are routed to whichever supported quest adapter is installed and recognizes the name:

```java
import fr.elias.npcs.data.QuestStatus;

modeledNPCs.assignQuest(player, "intro");
QuestStatus status = modeledNPCs.getQuestStatus(player, "intro");
if (status == null) {
    getLogger().fine("No installed quest adapter recognizes intro");
}
```

Possible values are `NOT_STARTED`, `IN_PROGRESS`, and `COMPLETED`. Quest status is `null` if no adapter recognizes the quest. Exact quest names and availability depend on the installed quest plugin.

## Dialogs and LuxDialogues

`openDialog(player, npcId)` opens the ModeledNPCS dialog associated with that NPC, if one is configured. LuxDialogues methods are separate and require the LuxDialogues integration:

```java
if (modeledNPCs.isLuxDialoguesAvailable()) {
    modeledNPCs.setNPCLuxDialogue(42, "welcome");
    modeledNPCs.setNPCLuxFirstPage(42, "1");
    boolean opened = modeledNPCs.triggerLuxDialogue(player, 42);
    boolean alreadyInDialogue = modeledNPCs.isPlayerInLuxDialogue(player);
}
```

`getNPCLuxDialogue` and `getNPCLuxFirstPage` return the configured values. The plugin stores these settings in its NPC extras configuration. `triggerLuxDialogue` returns `false` if no matching dialogue is configured or the integration cannot start it.

## Glow

Glow can be set for one player or all online players. Use Bukkit `ChatColor` values supported by the client:

```java
modeledNPCs.setNPCGlow(42, player, org.bukkit.ChatColor.GOLD);
modeledNPCs.removeNPCGlow(42, player);
modeledNPCs.setNPCGlowForAll(42, org.bukkit.ChatColor.GREEN);
modeledNPCs.toggleNPCGlow(42, player, false, org.bukkit.ChatColor.WHITE);
```

The implementation shares ModeledNPCS' glow manager so its scoreboard-team packets do not conflict with another manager instance. Consumers should use these API methods instead of creating a second glow manager for the same NPCs.

## Hover, states, schedules, and hologram frames

Hover settings can be controlled per NPC and are persisted by ModeledNPCS:

```java
modeledNPCs.setNPCHoverEnabled(42, true);
modeledNPCs.setNPCHoverHint(42, "&eTalk to me");
modeledNPCs.setNPCHoverAnimation(42, "wave");
int hoveredId = modeledNPCs.getHoveredNPCId(player); // -1 if none
```

`getNPCHoverHint` and `getNPCHoverAnimation` return empty strings when no per-NPC value is configured. Hover detection can also be disabled globally in ModeledNPCS configuration.

Set an NPC state to drive its configured animation and hologram suffix. Set schedule bounds using Minecraft in-game hours, from 0 through 23; pass `-1` for both values to disable the schedule. NPCs without a schedule are active:

```java
modeledNPCs.setNPCState(42, "busy");
String state = modeledNPCs.getNPCState(42);

modeledNPCs.setNPCSchedule(42, 8, 18);
boolean activeNow = modeledNPCs.isNPCActive(42);

modeledNPCs.setNPCHologramFrames(42, List.of("&aOpen", "&eWelcome"));
modeledNPCs.setNPCHologramFrameInterval(42, 20); // ticks; 20 ticks = 1 second
```

Schedule interval semantics and states depend on the plugin configuration. Hologram frame and schedule settings are persisted by the plugin. Getters expose the current configured state, hours, frames, and interval.

## Listen for API events

Register Bukkit listeners in your plugin. These API events are cancellable:

```java
import fr.elias.npcs.events.NPCInteractEvent;
import fr.elias.npcs.events.NPCHoverEvent;
import fr.elias.npcs.events.NPCStateChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class NpcListener implements Listener {
    @EventHandler
    public void onNpcInteract(NPCInteractEvent event) {
        int npcId = event.getNPCData().getId();
        switch (event.getInteractionType()) {
            case LEFT_CLICK -> { /* ... */ }
            case RIGHT_CLICK -> { /* ... */ }
            case SHIFT_LEFT_CLICK -> { /* ... */ }
            case SHIFT_RIGHT_CLICK -> { /* ... */ }
        }
        // event.setCancelled(true) cancels the interaction.
    }

    @EventHandler
    public void onNpcHover(NPCHoverEvent event) {
        if (event.getAction() == NPCHoverEvent.HoverAction.ENTER) {
            // A listener can cancel this transition's hover presentation.
        }
    }

    @EventHandler
    public void onNpcStateChange(NPCStateChangeEvent event) {
        if ("maintenance".equals(event.getNewState())) {
            event.setCancelled(true);
        }
    }
}
```

`NPCInteractEvent` provides the player, `INPCData`, and one of `LEFT_CLICK`, `RIGHT_CLICK`, `SHIFT_LEFT_CLICK`, or `SHIFT_RIGHT_CLICK`. `NPCHoverEvent` fires on `ENTER` and `LEAVE`; cancelling `ENTER` suppresses that transition's scale, sound, and hint, but does not disable later hover checks. `NPCStateChangeEvent` provides old and new state; cancellation prevents the new state from being applied. Register listeners through Bukkit's plugin manager, for example `getServer().getPluginManager().registerEvents(new NpcListener(), this)`.

## API reference by area

All methods are declared on `ModeledNPCsAPI` unless otherwise marked.

| Area | Methods |
|---|---|
| Lookup | `getNPCById`, `getEntityByNPCId`, `getAllNPCIds`, `getAllNPCs` |
| Display and nametag | `getNPCDisplayName`, `getNPCDisplayNameRaw`, `getNPCDisplayNameFor`, `setNPCDisplayName`, `hideNPCNametag`, `showNPCNametag`, `isNPCNametagHidden`, `reapplyNPCName`, `reapplyAllNPCNames` |
| Location and lifecycle | `getNPCLocation`, `moveNPC`, `createNPC`, `deleteNPC`, `updateNPC`, `respawnNPC` |
| Visibility and commands | `canPlayerViewNPC`, `getNPCCommands`, `addCommandToNPC`, `runNPCCommands` |
| Effects and auto-look | `getNPCEffects`, `playInteractEffects`, `restartLoopedEffect`, `enableAutoLook`, `disableAutoLook` |
| Routes | `addRouteWaypoint`, `clearRoute`, `getOrCreateRoute`, `getRoute`, `setRouteMode`, `setRouteSpeed`; route handle: `INPCRoute` |
| Escort | `startEscort`, `stopEscort`, `setEscortAnimation`, `getEscortAnimation`; options: `EscortOptions` |
| Trader and quests | `openTraderGUI`, `getTraderManager`, `assignQuest`, `getQuestStatus`; public types: `TraderAPI`, `QuestStatus` |
| Dialogs | `openDialog`, `isLuxDialoguesAvailable`, `triggerLuxDialogue`, `isPlayerInLuxDialogue`, `setNPCLuxDialogue`, `getNPCLuxDialogue`, `setNPCLuxFirstPage`, `getNPCLuxFirstPage` |
| Glow | `setNPCGlow`, `removeNPCGlow`, `setNPCGlowForAll`, `removeNPCGlowForAll`, `toggleNPCGlow` |
| Hover | `getHoveredNPCId`, `isPlayerHoveringNPC`, `setNPCHoverEnabled`, `isNPCHoverEnabled`, `setNPCHoverHint`, `getNPCHoverHint`, `setNPCHoverAnimation`, `getNPCHoverAnimation` |
| State and schedule | `setNPCState`, `getNPCState`, `isNPCActive`, `setNPCSchedule`, `getNPCScheduleStart`, `getNPCScheduleEnd` |
| Hologram animation | `setNPCHologramFrames`, `getNPCHologramFrames`, `setNPCHologramFrameInterval`, `getNPCHologramFrameInterval` |
| Model integrations | `applyModelEngineModel`, `respawnModelEngineNPC`, `spawnMythicMobsNPC`, `spawnMythicMobsNPCFull`, `updateMythicMobName`, `respawnMythicMobNPC` |

The standalone interfaces and data classes are under `fr.elias.npcs.data`. Events are under `fr.elias.npcs.events`. The legacy `IModeledNPCsAPI` remains under `fr.elias.npcs`.

## Building from source

For API maintainers, clone the repository and build the API artifact and documentation locally:

```sh
git clone https://github.com/OreoStudios/ModeledNPCS-API.git
cd ModeledNPCS-API
mvn clean verify
```

The artifact is compiled for Java 17. Deploying a release requires maintainer credentials kept in the local Maven `settings.xml` under the server ID `oreostudios-releases`; never commit credentials or a settings file containing them.

## Version compatibility

Compile against the API version used by the ModeledNPCS plugin on your server. API 8.1 additions require a plugin build that includes API 8.1. Older ModeledNPCS 9.4 builds bundle API 8.0, so they cannot provide the 8.1 methods even if your integration compiles against the newer Maven artifact. Deploy the matching updated ModeledNPCS build before using 8.1 calls.
