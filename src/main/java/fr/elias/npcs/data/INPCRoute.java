package fr.elias.npcs.data;

/** Live route handle. Mutations are saved by the plugin. Use on the server thread. */
public interface INPCRoute extends NPCRouteInterface {
    int getNpcId();
    String getRouteId();
    double getSpeed();
    /** Sets a finite, positive speed in blocks per tick. */
    void setSpeed(double speed);
}
