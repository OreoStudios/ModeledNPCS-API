# ModeledNPCS API 8.1

Public API for ModeledNPCS. Use a plugin build bundling API 8.1 (the updated 9.4 branch); older 9.4 jars bundle 8.0 and do not provide these additions.

Build with `mvn clean install`. Depend on `fr.elias:ModeledNPCs-API:8.1` with Maven scope `provided`. Do not shade the API: ModeledNPCS supplies it at runtime. Declare `depend: [ModeledNPCS]` in your plugin.yml. Use the public Maven repository below:

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

Maintainers deploy with `mvn clean deploy` using a local Maven settings server named `oreostudios-releases`. Credentials must never be committed.

Call API operations on the Bukkit server thread after ModeledNPCS enables:

```java
ModeledNPCsAPI api = ModeledNPCsAPI.get();
INPCRoute route = api.getOrCreateRoute(npcId, "tour");
route.addWaypoint(player.getLocation());
route.setMode("LOOP");
route.setSpeed(0.25);
api.startEscort(npcId, player, new EscortOptions("tour"));
QuestStatus status = api.getQuestStatus(player, "intro");
TraderAPI traders = api.getTraderManager();
if (traders != null && traders.hasTrader(npcId)) traders.openTrade(player, npcId);
```

Import `fr.elias.npcs.api.*` and `fr.elias.npcs.data.*`. Route handles persist waypoint, mode and speed changes; `getWaypoints()` returns detached locations in an unmodifiable list. `getRoute` returns null for an unknown route. Optional ModelEngine, MythicMobs and quest operations require their corresponding integrations.

8.1 exposes named/default routes, route mode/speed, configured escorts (including arrival and walking dialog), quest status, trader operations, ModelEngine application/respawn and MythicMobs spawn/name refresh/respawn. Existing public API 8.0 methods remain available. Plugin implementation classes are not required by consumers.
