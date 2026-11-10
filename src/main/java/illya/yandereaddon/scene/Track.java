package illya.yandereaddon.scene;

import java.util.List;

public record Track(
    String name,
    boolean bedRelative,
    boolean smooth,
    int duration,
    int entryTicks,
    int playerJoinTick,
    int sceneLoopStart,
    int sceneLoopEnd,
    boolean holdSceneLoopEnd,
    boolean continuousAction,
    List<Frame> frames,
    List<Cue> cues,
    Double sceneOffsetX,
    Double sceneOffsetY,
    Double sceneOffsetZ,
    Double sceneOffsetYaw
) {}