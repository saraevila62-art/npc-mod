package illya.yandereaddon.scene;

public record Cue(
    String kind,
    boolean speech,
    String expression,
    int tick,
    int length,
    String text,
    String source,
    String label,
    int offset,
    float volume,
    int color,
    int screenY
) {}