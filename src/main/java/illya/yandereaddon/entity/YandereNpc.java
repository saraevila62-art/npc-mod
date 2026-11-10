package illya.yandereaddon.entity;

import illya.yandereaddon.brain.YandereMood;
import illya.yandereaddon.brain.YandereState;
import illya.yandereaddon.brain.YandereStateMachine;
import illya.yandereaddon.entity.goal.PreventEscapeGoal;
import illya.yandereaddon.entity.goal.YandereFollowGoal;
import net.minecraft.core.particles.ParticleTypes;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class YandereNpc extends PathfinderMob {

    private static final EntityDataAccessor<Byte> D_STATE =
        SynchedEntityData.defineId(YandereNpc.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> D_JEAL =
        SynchedEntityData.defineId(YandereNpc.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> D_DOM =
        SynchedEntityData.defineId(YandereNpc.class, EntityDataSerializers.FLOAT);

    public final YandereMood mood = new YandereMood();
    public final YandereStateMachine brain = new YandereStateMachine();

    private Player owner;
    private UUID ownerUuid;
    private int rivalCooldown = 0;
    private int distantCooldown = 0;

    public YandereNpc(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(D_STATE, (byte) 0);
        builder.define(D_JEAL, 0f);
        builder.define(D_DOM, 0f);
    }

    public YandereState state() {
        return YandereState.values()[entityData.get(D_STATE)];
    }

    public Player getOwner() { return owner; }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new YandereFollowGoal(this));
        this.goalSelector.addGoal(2, new PreventEscapeGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8f));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        if ((owner == null || owner.isRemoved()) && ownerUuid != null
                && level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            owner = serverLevel.getPlayerByUUID(ownerUuid);
        }

        mood.tick();
        if (rivalCooldown > 0) rivalCooldown--;
        if (distantCooldown > 0) distantCooldown--;

        if (owner != null) {
            double dist = owner.distanceTo(this);

            if (rivalCooldown == 0) {
                boolean rival = !level().getEntitiesOfClass(LivingEntity.class,
                        getBoundingBox().inflate(5),
                        e -> e != this && e != owner && !e.isRemoved()).isEmpty();
                if (rival) {
                    mood.onRivalNearby();
                    rivalCooldown = 40;
                }
            }

            if (dist > 8 && distantCooldown == 0) {
                mood.onPlayerDistant();
                distantCooldown = 20;
            }

            if (dist < 2) {
                mood.neglectTicks = Math.max(0, mood.neglectTicks - 2);
            }
        }

        brain.update(mood);
        entityData.set(D_STATE, (byte) brain.state().ordinal());
        entityData.set(D_JEAL, mood.jealousy);
        entityData.set(D_DOM, mood.dominance);

        if (brain.changed() && owner != null) {
            String msg = "[" + brain.state() + "] J=" +
                String.format("%.0f", mood.jealousy) +
                " D=" + String.format("%.0f", mood.dominance);
            owner.displayClientMessage(Component.literal(msg), true);

            if (brain.state() == YandereState.YANDERE && level() instanceof net.minecraft.server.level.ServerLevel sl)
                sl.sendParticles(ParticleTypes.HEART, getX(), getY() + 1.5, getZ(), 12, .3, .3, .3, .01);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide) {
            mood.onPositiveContact();
            owner = player;
            ownerUuid = player.getUUID();
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUuid != null) tag.putUUID("owner", ownerUuid);
        tag.putFloat("att", mood.attachment);
        tag.putFloat("jea", mood.jealousy);
        tag.putFloat("dom", mood.dominance);
        tag.putFloat("tru", mood.trust);
        tag.putInt("neg", mood.neglectTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerUuid = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
        mood.attachment   = tag.getFloat("att");
        mood.jealousy     = tag.getFloat("jea");
        mood.dominance    = tag.getFloat("dom");
        mood.trust        = tag.getFloat("tru");
        mood.neglectTicks = tag.getInt("neg");
    }
}