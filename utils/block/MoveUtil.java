package net.ccbluex.liquidbounce.utils.block;


import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static net.ccbluex.liquidbounce.utils2.MinecraftInstance.mc;


public class MoveUtil {
    public static final double WALK_SPEED = 0.221;
    public static final double BUNNY_SLOPE = 0.66;
    public static final double MOD_SPRINTING = 1.3F;
    public static final double MOD_SNEAK = 0.3F;
    public static final double MOD_ICE = 2.5F;
    public static final double MOD_WEB = 0.105 / WALK_SPEED;
    public static final double JUMP_HEIGHT = 0.42F;
    public static final double BUNNY_FRICTION = 159.9F;
    public static final double Y_ON_GROUND_MIN = 0.00001;
    public static final double Y_ON_GROUND_MAX = 0.0626;

    public static final double AIR_FRICTION = 0.9800000190734863D;
    public static final double WATER_FRICTION = 0.800000011920929D;
    public static final double LAVA_FRICTION = 0.5D;
    public static final double MOD_SWIM = 0.115F / WALK_SPEED;
    public static final double[] MOD_DEPTH_STRIDER = {
            1.0F,
            0.1645F / MOD_SWIM / WALK_SPEED,
            0.1995F / MOD_SWIM / WALK_SPEED,
            1.0F / MOD_SWIM,
    };

    public static final double UNLOADED_CHUNK_MOTION = -0.09800000190735147;
    public static final double HEAD_HITTER_MOTION = -0.0784000015258789;

    /**
     * Makes the player strafe
     */

    /**
     * Makes the player strafe at the specified speed
     */

    public static void strafe(final double speed, float yaw) {
        yaw = (float) Math.toRadians(yaw);
        mc.thePlayer.motionX = -MathHelper.sin(yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos(yaw) * speed;
    }

    /**
     * Stops the player from moving
     */
    public static void stop() {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
    }

    /**
     * Gets the players' movement yaw
     */


    /**
     * Gets the players' movement yaw
     */
    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    /**
     * Used to get the players speed
     */
    public static double speed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    /**
     * Gets the players' depth strider modifier
     *
     * @return depth strider modifier
     */
    public static int depthStriderLevel() {
        return EnchantmentHelper.getDepthStriderModifier(mc.thePlayer);
    }

    /**
     * Checks if the player has enough movement input for sprinting
     *
     * @return movement input enough for sprinting
     */
    public static boolean enoughMovementForSprinting() {
        return Math.abs(mc.thePlayer.moveForward) >= 0.8F || Math.abs(mc.thePlayer.moveStrafing) >= 0.8F;
    }

    /**
     * Checks if the player is allowed to sprint
     *
     * @param legit should the player follow vanilla sprinting rules?
     * @return player able to sprint
     */
    public static boolean canSprint(final boolean legit) {
        return (legit ? mc.thePlayer.moveForward >= 0.8F
                && !mc.thePlayer.isCollidedHorizontally
                && (mc.thePlayer.getFoodStats().getFoodLevel() > 6 || mc.thePlayer.capabilities.allowFlying)
                && !mc.thePlayer.isPotionActive(Potion.blindness)
                && !mc.thePlayer.isUsingItem()
                && !mc.thePlayer.isSneaking()
                : enoughMovementForSprinting());
    }

    /**
     * Basically calculates allowed horizontal distance just like NCP does
     *
     * @return allowed horizontal distance in one tick
     */


    /**
     * Checks if the player is moving
     *
     * @return player moving
     */
    public static boolean isMoving() {
        return isMoving(mc.thePlayer);
    }

    @Contract(pure = true)
    public static boolean isMoving(@NotNull EntityLivingBase entity) {
        return entity.moveForward != 0 || entity.moveStrafing != 0;
    }



    /**
     * Modifies a selected motion with jump boost
     *
     * @param motionY input motion
     * @return modified motion
     */
    public static double jumpBoostMotion(final double motionY) {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            return motionY + (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
        }

        return motionY;
    }

    /**
     * Gets the players predicted jump motion the specified amount of ticks ahead
     *
     * @return predicted jump motion
     */


    /**
     * Calculates the default player jump motion
     *
     * @return player jump motion
     */
    public static double jumpMotion() {
        return jumpBoostMotion(JUMP_HEIGHT);
    }

    public static double predictedMotionXZ(double motion, int tick, boolean moving) {
        for (int i = 0; i < tick; i++) {
            if (!moving) motion /= 0.5;
            if (motion < 0.005)
                return 0;
        }
        return motion;
    }
}
