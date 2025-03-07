package net.nonemc.leaf.injection.forge.mixins.block;

import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.event.BlockBBEvent;
import net.nonemc.leaf.features.module.modules.combat.Criticals;
import net.nonemc.leaf.features.module.modules.player.NoFall;
import net.nonemc.leaf.features.module.modules.render.XRay;
import net.nonemc.leaf.features.module.modules.world.NoSlowBreak;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public abstract class MixinBlock {

    @Shadow
    public abstract AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state);

    @Shadow
    @Final
    protected BlockState blockState;
    @Shadow
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return null;
    }

    @Overwrite
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        AxisAlignedBB axisalignedbb = this.getCollisionBoundingBox(worldIn, pos, state);
        final BlockBBEvent blockBBEvent = new BlockBBEvent(pos, blockState.getBlock(), axisalignedbb);
        Leaf.eventManager.callEvent(blockBBEvent);
        axisalignedbb = blockBBEvent.getBoundingBox();
        if(axisalignedbb != null && mask.intersectsWith(axisalignedbb))
            list.add(axisalignedbb);
    }

    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
    private void shouldSideBeRendered(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final XRay xray = Leaf.moduleManager.getModule(XRay.class);

        if(xray.getState())
            callbackInfoReturnable.setReturnValue(xray.getXrayBlocks().contains(this));
    }

    @Inject(method = "getAmbientOcclusionLightValue", at = @At("HEAD"), cancellable = true)
    private void getAmbientOcclusionLightValue(final CallbackInfoReturnable<Float> floatCallbackInfoReturnable) {
        if (Leaf.moduleManager.getModule(XRay.class).getState())
            floatCallbackInfoReturnable.setReturnValue(1F);
    }

    @Inject(method = "getPlayerRelativeBlockHardness", at = @At("RETURN"), cancellable = true)
    public void modifyBreakSpeed(EntityPlayer playerIn, World worldIn, BlockPos pos, final CallbackInfoReturnable<Float> callbackInfo) {
        float f = callbackInfo.getReturnValue();

        // NoSlowBreak
        final NoSlowBreak noSlowBreak = Leaf.moduleManager.getModule(NoSlowBreak.class);
        if (noSlowBreak.getState()) {
            if (noSlowBreak.getWaterValue().get() && playerIn.isInsideOfMaterial(Material.water) &&
                    !EnchantmentHelper.getAquaAffinityModifier(playerIn)) {
                f *= 5.0F;
            }

            if (noSlowBreak.getAirValue().get() && !playerIn.onGround) {
                f *= 5.0F;
            }
        } else if (playerIn.onGround) { // NoGround
            final NoFall noFall = Leaf.moduleManager.getModule(NoFall.class);
            final Criticals criticals = Leaf.moduleManager.getModule(Criticals.class);

            if (noFall.getState() && noFall.getMode().equals("NoGround")) {
                f /= 5F;
            }
        }

        callbackInfo.setReturnValue(f);
    }
}