package net.nonemc.leaf.injection.forge.mixins.performance;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.nonemc.leaf.injection.access.IBlock;
import net.nonemc.leaf.injection.access.IWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = {Block.class})
public abstract class MixinBlock implements IBlock {
    @Shadow
    public abstract int getLightValue();

    @Shadow
    public abstract int getLightOpacity();

    @Override
    public int getLightValue(IBlockAccess iBlockAccess, int n, int n2, int n3) {
        Block block = ((IWorld) iBlockAccess).getBlockState(n, n2, n3).getBlock();
        if (!this.equals(block)) {
            return ((IBlock) block).getLightValue(iBlockAccess, n, n2, n3);
        }
        return this.getLightValue();
    }

    @Override
    public int getLightOpacity(IBlockAccess iBlockAccess, int n, int n2, int n3) {
        return this.getLightOpacity();
    }
}
