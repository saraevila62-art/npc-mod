package illya.yandereaddon.entity.goal;

import illya.yandereaddon.brain.YandereState;
import illya.yandereaddon.entity.YandereNpc;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class YandereFollowGoal extends Goal {
    private final YandereNpc npc;
    private LivingEntity target;

    public YandereFollowGoal(YandereNpc npc) {
        this.npc = npc;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        target = npc.getOwner();
        return target != null && !target.isRemoved() && target.distanceTo(npc) > 2.5;
    }

    @Override
    public void tick() {
        double speed = switch (npc.state()) {
            case CALM                   -> 0.6;
            case ATTACHED, SUSPICIOUS   -> 0.8;
            case JEALOUS, DOMINANT, YANDERE -> 1.1;
        };
        npc.getNavigation().moveTo(target, speed);
        npc.getLookControl().setLookAt(target);
    }
}