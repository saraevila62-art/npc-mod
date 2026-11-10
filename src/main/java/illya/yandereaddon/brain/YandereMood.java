package illya.yandereaddon.brain;

public final class YandereMood {
    public float attachment = 30f;
    public float jealousy   = 0f;
    public float dominance  = 0f;
    public float trust      = 70f;
    public int neglectTicks = 0;

    // Пороги — крути под жёсткость яндере
    public float thAttach     = 55f;
    public float thSuspicious = 25f;
    public float thJealous    = 55f;
    public float thDominant   = 50f;
    public float thYandereJ   = 80f;
    public float thYandereD   = 70f;
    public float lowTrust     = 35f;
    public int neglectBurst   = 600;

    public void tick() {
        jealousy  = clamp(jealousy  - 0.03f);
        dominance = clamp(dominance - 0.02f);
        if (neglectTicks > 200) attachment = clamp(attachment - 0.01f);
        neglectTicks++;
    }

    public void onPositiveContact() {
        attachment = clamp(attachment + 6f);
        jealousy   = clamp(jealousy   - 8f);
        dominance  = clamp(dominance  - 4f);
        trust      = clamp(trust      + 2f);
        neglectTicks = 0;
    }

    public void onRivalNearby() {
        jealousy = clamp(jealousy + 12f);
        trust    = clamp(trust    - 3f);
    }

    public void onPlayerDistant() {
        jealousy  = clamp(jealousy  + 2f);
        dominance = clamp(dominance + 3f);
    }

    public void onEscapeAttempt() {
        dominance = clamp(dominance + 15f);
        trust     = clamp(trust     - 5f);
    }

    private static float clamp(float v) {
        return Math.max(0f, Math.min(100f, v));
    }
}