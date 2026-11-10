package illya.yandereaddon.client;

import illya.yandereaddon.entity.YandereNpc;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class YandereNpcRenderer extends MobRenderer<YandereNpc, HumanoidModel<YandereNpc>> {
    public YandereNpcRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(YandereNpc entity) {
        return ResourceLocation.fromNamespaceAndPath("yandereaddon", "textures/entity/yuno.png");
    }
}