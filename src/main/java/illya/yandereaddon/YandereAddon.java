package illya.yandereaddon;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import illya.yandereaddon.entity.YandereNpc;
import illya.yandereaddon.scene.SceneRegistry;

@Mod(YandereAddon.MODID)
public class YandereAddon {
    public static final String MODID = "yandereaddon";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<EntityType<?>> ENTITIES =
        DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(Registries.ITEM, MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<YandereNpc>> YANDERE_NPC =
        ENTITIES.register("yandere_npc", () -> EntityType.Builder.of(YandereNpc::new, MobCategory.CREATURE)
            .sized(0.6f, 1.8f)
            .build(MODID + ":yandere_npc"));

    public static final DeferredHolder<Item, SpawnEggItem> YUNO_SPAWN_EGG =
        ITEMS.register("yuno_spawn_egg", () -> new SpawnEggItem(
            YANDERE_NPC.get(), 0xF08AB8, 0xB52345, new Item.Properties()));

    public YandereAddon(IEventBus modEventBus, ModContainer modContainer) {
        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
        modEventBus.addListener(this::onEntityAttributes);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(YUNO_SPAWN_EGG);
        }
    }

    private void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(YANDERE_NPC.get(), net.minecraft.world.entity.Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 24.0)
            .add(Attributes.MOVEMENT_SPEED, 0.28)
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .build());
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SceneRegistry());
    }
}