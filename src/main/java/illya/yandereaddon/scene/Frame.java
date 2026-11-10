package illya.yandereaddon.scene;

import java.util.Map;

public record Frame(int tick, Motion motion, Map<String, float[]> pose, Items items) {}