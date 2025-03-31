package net.nonemc.leaf.utils.rotation;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.nonemc.leaf.data.Rotation;
import net.nonemc.leaf.event.EventTarget;
import net.nonemc.leaf.event.Listenable;
import net.nonemc.leaf.event.PacketEvent;
import net.nonemc.leaf.event.TickEvent;
import net.nonemc.leaf.utils.MinecraftInstance;

public final class RotationUtils extends MinecraftInstance implements Listenable {
    private static int keepLength;
    private static int revTick;

    public static Rotation targetRotation;

    public static Rotation serverRotation = new Rotation(0F, 0F);
    public static boolean keepCurrentRotation = false;

    public static double getRotationDifference(final Entity entity) {
        final Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()));
        return getRotationDifference(rotation, new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
    }

    public static double getRotationDifference(final Rotation rotation) {
        return serverRotation == null ? 0D : getRotationDifference(rotation, serverRotation);
    }

    public static double getRotationDifference(final Rotation a, final Rotation b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
    }

    public static float getAngleDifference(final float a, final float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    public static void setTargetRotation(final Rotation rotation) {
        setTargetRotation(rotation, 0);
    }

    public static void setTargetRotation(final Rotation rotation, final int keepLength) {
        if (Double.isNaN(rotation.getYaw()) || Double.isNaN(rotation.getPitch())
                || rotation.getPitch() > 90 || rotation.getPitch() < -90)
            return;

        targetRotation = rotation;
        RotationUtils.keepLength = keepLength;
        RotationUtils.revTick = 0;
    }

    @EventTarget
    public void onTick(final TickEvent event) {
        if (targetRotation != null) {
            keepLength--;

            if (keepLength <= 0) {
                if (revTick > 0) {
                    revTick--;
                    resetValue();
                } else resetValue();
            }
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof C03PacketPlayer) {
            final C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;
            if (targetRotation != null && !keepCurrentRotation && (targetRotation.getYaw() != serverRotation.getYaw() || targetRotation.getPitch() != serverRotation.getPitch())) {
                packetPlayer.yaw = targetRotation.getYaw();
                packetPlayer.pitch = targetRotation.getPitch();
                packetPlayer.rotating = true;
            }

            if (packetPlayer.rotating) serverRotation = new Rotation(packetPlayer.yaw, packetPlayer.pitch);
        }
    }

    private static Rotation toRotation(final Vec3 vec) {
        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        if (mc.thePlayer.onGround) {
            eyesPos.addVector(mc.thePlayer.motionX, 0.0, mc.thePlayer.motionZ);
        } else {
            eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
        }
        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;

        return new Rotation(MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
                MathHelper.wrapAngleTo180_float((float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))));
    }

    private static Vec3 getCenter(final AxisAlignedBB bb) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
    }

    private static void resetValue() {
        keepLength = 0;
        if (revTick > 0) {
            targetRotation = new Rotation(targetRotation.getYaw() - getAngleDifference(targetRotation.getYaw(), mc.thePlayer.rotationYaw) / revTick
                    , targetRotation.getPitch() - getAngleDifference(targetRotation.getPitch(), mc.thePlayer.rotationPitch) / revTick);
        } else targetRotation = null;
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
