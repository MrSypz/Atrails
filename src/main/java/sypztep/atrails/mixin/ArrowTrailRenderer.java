package sypztep.atrails.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.atrails.client.ArrowTracked;
import sypztep.atrails.client.AtrailsClient;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;

import java.awt.*;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ProjectileEntityRenderer.class)
public abstract class ArrowTrailRenderer<T extends PersistentProjectileEntity> extends EntityRenderer<T> {
    @Unique
    private static final RenderLayer TRAIL_TYPE = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.apply(RenderTypeToken.createCachedToken(AtrailsClient.id("textures/trails/light_trail.png")));

    protected ArrowTrailRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Unique
    public RenderLayer getTrailRenderType() {
        return TRAIL_TYPE;
    }

    @Inject(method = "render(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    public void render(T entity, float entityYaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (entity instanceof ArrowEntity arrowEntity && !arrowEntity.isInvisible()) {
            matrixStack.push();
            List<TrailPoint> positions = ((ArrowTracked) arrowEntity).getPastPos();
            VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setRenderType(getTrailRenderType());

            float size = 0.25f;
            float alpha = 1;

            float x = (float) MathHelper.lerp(tickDelta, arrowEntity.prevX, arrowEntity.getX());
            float y = (float) MathHelper.lerp(tickDelta, arrowEntity.prevY, arrowEntity.getY());
            float z = (float) MathHelper.lerp(tickDelta, arrowEntity.prevZ, arrowEntity.getZ());

            matrixStack.translate(-x, -y, -z);
            builder.setColor(new Color(0xFFFFFF))
                    .setAlpha(alpha)
                    .renderTrail(matrixStack,
                            positions,
                            f -> MathHelper.sqrt(easeInOutQuad(f)) * size,
                            f -> builder.setAlpha((float) MathHelper.clamp(Math.cbrt(alpha * easeInOutQuad(f)), 0.0f, 1.0f))
                    )
                    .renderTrail(matrixStack,
                            positions,
                            f -> (MathHelper.sqrt(easeInOutQuad(f)) * size) / 1.5f,
                            f -> builder.setAlpha((float) MathHelper.clamp(Math.cbrt((alpha * easeInOutQuad(f)) / 1.5f), 0.0f, 1.0f))
                    );
            matrixStack.pop();
        }
    }

    @Unique
    public float easeInOutQuad(float t) {
        return t < 0.5f ? 2 * t * t : 1 - (float) Math.pow(-2 * t + 2, 2) / 2;
    }
}
