package illya.yandereaddon.entity.goal;

import illya.yandereaddon.brain.YandereState;
import illya.yandereaddon.entity.YandereNpc;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/**
 * В DOMINANT/YANDERE встаёт между владельцем и направлением ухода.
 * Физическое удержание — НПС перегородит путь и толкнёт.
 */
public class PreventEscapeGoal extends Goal {
    private final YandereNpc npc;

    public PreventEscapeGoal(YandereNpc npc) {
        this.npc = npc;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Player p = npc.getOwner();
        if (p == null || p.isRemoved()) return false;
        YandereState s = npc.state();
        boolean fleeing = p.getDeltaMovement().horizontalDistance() > 0.12
                       && p.distanceTo(npc) < 4;
        return (s == YandereState.DOMINANT || s == YandereState.YANDERE) && fleeing;
    }

    @Override
    public void tick() {
        Player p = npc.getOwner();
        if (p == null) return;

        // Встать на вектор «от неё к игроку», чуть впереди игрока
        double dx = p.getX() - npc.getX();
        double dz = p.getZ() - npc.getZ();
        double len = Math.max(0.001, Math.hypot(dx, dz));
        double blockX = p.getX() + (dx / len) * 0.8;
        double blockZ = p.getZ() + (dz / len) * 0.8;

        npc.getNavigation().moveTo(blockX, p.getY(), blockZ, 1.2);
        npc.mood.onEscapeAttempt();
    }
}