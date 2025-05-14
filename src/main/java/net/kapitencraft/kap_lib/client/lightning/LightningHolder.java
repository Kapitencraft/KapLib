package net.kapitencraft.kap_lib.client.lightning;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class LightningHolder {
    private final List<Vector3f> vertexes;
    private float alpha = .3f, alphaO;
    private final int lifetime;
    private int age;

    public LightningHolder(Vec3 start, Vec3 end, int lifetime) {
        this.lifetime = lifetime;
        this.vertexes = this.createVertexes(start, end);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private List<Vector3f> createVertexes(Vec3 start, Vec3 end) {
        Vec2 offset = MathHelper.createTargetRotationFromPos(start, end);
        Quaternionf rot = new Quaternionf().rotateX(offset.x).rotateY(offset.y);

        RandomSource pSource = RandomSource.create();
        long seed = pSource.nextLong();
        float[] afloat = new float[8];
        float[] afloat1 = new float[8];
        float f = 0.0F;
        float f1 = 0.0F;

        for (int i = 7; i >= 0; --i) {
            afloat[i] = f;
            afloat1[i] = f1;
            f += (float)(pSource.nextInt(11) - 5);
            f1 += (float)(pSource.nextInt(11) - 5);
        }

        List<Vector3f> vertexes = new ArrayList<>();

        for(int j = 0; j < 4; ++j) {
            RandomSource source1 = RandomSource.create(seed);
            for(int k = 0; k < 3; ++k) {
                int l = 7 - k;
                int i1 = 0;

                if (k > 0) {
                    i1 = l - 2;
                }

                float f2 = afloat[l] - f;
                float f3 = afloat1[l] - f1;

                for(int j1 = l; j1 >= i1; --j1) {
                    float f4 = f2;
                    float f5 = f3;
                    if (k == 0) {
                        f2 += (float)(source1.nextInt(11) - 5);
                        f3 += (float)(source1.nextInt(11) - 5);
                    } else {
                        f2 += (float)(source1.nextInt(31) - 15);
                        f3 += (float)(source1.nextInt(31) - 15);
                    }

                    float f10 = 0.1F + j * 0.2F;
                    float f11 = 0.1F + j * 0.2F;

                    if (k == 0) {
                        f10 *= j1 * 0.1F + 1.0F;
                        f11 *= (j1 - 1.0F) * 0.1F + 1.0F;
                    }

                    for (int i = 0; i < 4; i++) {
                        boolean p_115285_ = i != 0 && i != 3;
                        boolean p_115286_ = i > 1;
                        boolean p_115287_ = i < 2;
                        Vector3f pos1 = new Vector3f(
                                f2 + (p_115285_ ? f11 : -f11),
                                j1 * 8,
                                f3 + (p_115286_ ? f11 : -f11)
                        );
                        Vector3f pos2 = new Vector3f(
                                f4 + (p_115285_ ? f10 : -f10),
                                (j1 + 1) * 8,
                                f5 + (p_115286_ ? f10 : -f10)
                        );
                        Vector3f pos3 = new Vector3f(
                                f4 + (p_115287_ ? f10 : -f10),
                                (j1 + 1) * 8,
                                f5 + (p_115285_ ? f10 : -f10)
                        );
                        Vector3f pos4 = new Vector3f(
                                f2 + (p_115287_ ? f11 : -f11),
                                j1 * 8,
                                f3 + (p_115285_ ? f11 : -f11)
                        );
                        vertexes.add(pos1);
                        vertexes.add(pos2);
                        vertexes.add(pos3);
                        vertexes.add(pos4);
                    }
                }
            }
        }
        return vertexes.stream().map(v -> v.rotate(rot).add((float) start.x, (float) start.y, (float) start.z)).toList();
    }

    @SubscribeEvent
    public void renderProxy(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        this.render(event.getPoseStack().last().pose(),
                Minecraft.getInstance().renderBuffers().bufferSource(),
                event.getPartialTick()
        );
    }

    public void render(Matrix4f pose, @NotNull MultiBufferSource source, float pPartialTicks) {
        float alpha = Mth.clamp(pPartialTicks, alphaO, this.alpha);
        VertexConsumer consumer = source.getBuffer(RenderType.lightning());
        this.vertexes.stream().map(Vector3f::new)
                .forEach(vector3f ->
                        consumer.vertex(pose, vector3f.x, vector3f.y, vector3f.z)
                                .color(.45f, .45f, .5f, .3f)
                                .endVertex()
                );
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (this.age++ >= this.lifetime) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
        this.alphaO = alpha;
        this.alpha = (1 - (float) age / lifetime) * .3f;
    }
}
