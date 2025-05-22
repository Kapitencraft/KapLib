package net.kapitencraft.kap_lib.client.particle;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class LightningParticle extends Particle {
    private static final ParticleRenderType RENDER_TYPE = new SimpleParticleRenderType(RenderType.lightning());

    private final List<Vector3f> vertexes;

    protected LightningParticle(ClientLevel pLevel, Vec3 start, Vec3 end, int segments, float displacement, float width) {
        super(pLevel, 0, 0, 0);
        this.vertexes = createVertexes(start, end, segments, displacement, width);
        this.setBoundingBox(new AABB(start, end));
        this.alpha = .3f;
        this.lifetime = 1000;
    }

    private List<Vector3f> createVertexes(Vec3 start, Vec3 end, int segments, float displacement, float width) {
        List<Vec3> points = new ArrayList<>();
        points.add(start);

        Vec3 direction = end.subtract(start);
        Vec3 dirNorm = direction.normalize();

        // Create a perpendicular basis (u, v) to apply offset in a plane
        Vec3 arbitrary = new Vec3(0, 1, 0);
        if (Math.abs(dirNorm.y) > 0.99f) {
            arbitrary = new Vec3(1, 0, 0); // avoid colinearity
        }
        Vec3 u = dirNorm.cross(arbitrary).normalize();
        Vec3 v = dirNorm.cross(u).normalize();
        RandomSource source = RandomSource.create();

        for (int i = 1; i < segments; i++) {
            float t = (float) i / segments;

            // Linear interpolation
            Vec3 point = start.add(direction.scale(t));

            // Fade offset toward endpoints
            float fade = 1.0f - Math.abs(0.5f - t) * 2.0f;

            // Random offset in the plane perpendicular to the direction
            float offsetU = (source.nextFloat() - 0.5f) * 2.0f * displacement * fade;
            float offsetV = (source.nextFloat() - 0.5f) * 2.0f * displacement * fade;

            points.add(point.add(u.scale(offsetU).add(v.scale(offsetV))));
        }
        points.add(end);

        float sizeScale = width / .7f; //.7 is base size


        List<Vec3> vertexes = new ArrayList<>();
        for (int i = 1; i < points.size(); i++) {
            Vec3 oldPos = points.get(i - 1);
            Vec3 pos = points.get(i);

            for (int j = 0; j < 4; j++) {
                float f10 = (.1f + j * 0.2F) * sizeScale;

                float f11 = (.1F + j * 0.2F) * sizeScale;

                quad(vertexes, pos, oldPos, u, v, f10, f11, false, false, true, false);
                quad(vertexes, pos, oldPos, u, v, f10, f11, true, false, true, true);
                quad(vertexes, pos, oldPos, u, v, f10, f11, true, true, false, true);
                quad(vertexes, pos, oldPos, u, v, f10, f11, false, true, false, false);
            }

        }
        return vertexes.stream().map(Vec3::toVector3f).toList();
    }

    private void quad(List<Vec3> positions, Vec3 start, Vec3 stop, Vec3 u, Vec3 v, float p_115283_, float p_115284_, boolean p_115285_, boolean p_115286_, boolean p_115287_, boolean p_115288_) {
        positions.add(start.add(u.scale(p_115285_ ? p_115284_ : -p_115284_)).add(v.scale(p_115286_ ? p_115284_ : -p_115284_)));
        positions.add(stop.add(u.scale(p_115285_ ? p_115283_ : -p_115283_)).add(v.scale(p_115286_ ? p_115283_ : -p_115283_)));
        positions.add(stop.add(u.scale(p_115287_ ? p_115283_ : -p_115283_)).add(v.scale(p_115288_ ? p_115283_ : -p_115283_)));
        positions.add(start.add(u.scale(p_115287_ ? p_115284_ : -p_115284_)).add(v.scale(p_115288_ ? p_115284_ : -p_115284_)));
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        Vec3 camPos = pRenderInfo.getPosition();
        this.vertexes.forEach(vector3f ->
                        pBuffer.vertex(vector3f.x - camPos.x, vector3f.y - camPos.y, vector3f.z - camPos.z)
                                .color(.45f, .45f, .5f, alpha)
                                .endVertex()
                );
    }

    @Override
    public void tick() {
        if (this.age++ > this.lifetime) this.remove();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static class Provider implements ParticleProvider<LightningParticleOptions> {

        @Override
        public @Nullable Particle createParticle(LightningParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new LightningParticle(pLevel, pType.getStart(), pType.getEnd(), pType.getSegments(), pType.getDisplacement(), pType.getWidth());
        }
    }
}
