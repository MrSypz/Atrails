package sypztep.atrails.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.atrails.client.ArrowTracked;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowPos extends ProjectileEntity implements ArrowTracked {
    @Unique
    public final TrailPointBuilder trailPointBuilder = TrailPointBuilder.create(15);

    public ArrowPos(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        Vec3d position = this.getCameraPosVec(MinecraftClient.getInstance().getTickDelta()).add(0, -.2f, 0f);
        trailPointBuilder.addTrailPoint(position);
        trailPointBuilder.tickTrailPoints();
    }

    @Override
    public List<TrailPoint> getPastPos() {
        return trailPointBuilder.getTrailPoints();
    }
}