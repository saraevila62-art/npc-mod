package illya.yandereaddon.brain;

public final class YandereStateMachine {
    private YandereState current  = YandereState.CALM;
    private YandereState previous = YandereState.CALM;

    public YandereState state()   { return current; }
    public boolean changed()      { return current != previous; }

    public void update(YandereMood m) {
        previous = current;

        // Взрыв яндере — проверяем ПЕРВЫМ
        boolean burst =
            (m.jealousy >= m.thYandereJ && m.dominance >= m.thYandereD) ||
            (m.jealousy >= m.thYandereJ && m.trust < m.lowTrust) ||
            (m.neglectTicks >= m.neglectBurst && m.trust < m.lowTrust);
        if (burst) { current = YandereState.YANDERE; return; }

        if (m.dominance >= m.thDominant)   { current = YandereState.DOMINANT;   return; }
        if (m.jealousy  >= m.thJealous)    { current = YandereState.JEALOUS;    return; }
        if (m.jealousy  >= m.thSuspicious) { current = YandereState.SUSPICIOUS; return; }
        if (m.attachment >= m.thAttach)    { current = YandereState.ATTACHED;   return; }

        current = YandereState.CALM;
    }
}