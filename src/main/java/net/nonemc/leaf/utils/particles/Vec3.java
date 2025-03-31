package net.nonemc.leaf.utils.particles;

import net.minecraft.util.Vec3i;

public class Vec3 {
    public double xCoord;

    public double yCoord;

    public double zCoord;

    public Vec3(double x, double y, double z) {
        if (x == -0.0D) {
            x = 0.0D;
        }

        if (y == -0.0D) {
            y = 0.0D;
        }

        if (z == -0.0D) {
            z = 0.0D;
        }

        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public Vec3(Vec3i p_i46377_1_) {
        this(p_i46377_1_.getX(), p_i46377_1_.getY(), p_i46377_1_.getZ());
    }

    public Vec3 add(Vec3 vec) {
        return this.addVector(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public Vec3 addVector(double x, double y, double z) {
        return new Vec3(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }

    public String toString() {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }

}
