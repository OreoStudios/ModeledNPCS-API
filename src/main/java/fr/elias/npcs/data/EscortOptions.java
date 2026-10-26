package fr.elias.npcs.data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Escort configuration independent of the plugin's dialog implementation.
 * A null route ID selects the default route. Walking dialog order is preserved.
 */
public record EscortOptions(String routeId, List<String> arrivalDialog,
        String arrivalTree, int arrivalTimeoutSeconds, String timeoutMessage,
        List<String> arrivalCommands, Map<String, WalkingDialogEntry> walkingDialog) {
    public EscortOptions {
        if (arrivalTimeoutSeconds < 0) throw new IllegalArgumentException("Negative timeout");
        arrivalDialog = arrivalDialog == null ? List.of() : List.copyOf(arrivalDialog);
        arrivalCommands = arrivalCommands == null ? List.of() : List.copyOf(arrivalCommands);
        walkingDialog = walkingDialog == null ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(walkingDialog));
        walkingDialog.forEach((key, value) -> {
            java.util.Objects.requireNonNull(key, "Walking dialog key");
            java.util.Objects.requireNonNull(value, "Walking dialog entry");
        });
    }
    public EscortOptions(String routeId) {
        this(routeId, List.of(), null, 10, null, List.of(), Map.of());
    }
    public record WalkingDialogEntry(List<String> lines, int intervalSeconds) {
        public WalkingDialogEntry {
            lines = List.copyOf(lines);
            if (intervalSeconds < 1) throw new IllegalArgumentException("Interval must be positive");
        }
    }
}
