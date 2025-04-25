package net.nonemc.leaf.injection.forge.mixins.network;

import io.netty.channel.*;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.event.PacketEvent;
import net.nonemc.leaf.features.module.modules.client.Animations;
import net.nonemc.leaf.libs.packet.PacketLib;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetAddress;
@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {

    @Inject(method = "getIsencrypted", at = @At("HEAD"), cancellable = true)
    private void getIsencrypted(CallbackInfoReturnable<Boolean> cir) {
        if (Animations.INSTANCE.getFlagRenderTabOverlay()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void read(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callback) {
        if (PacketLib.INSTANCE.getPacketType(packet) != PacketLib.PacketType.SERVERSIDE)
            return;

        final PacketEvent event = new PacketEvent(packet, PacketEvent.Type.RECEIVE);
        Leaf.eventManager.callEvent(event);

        if (event.isCancelled())
            callback.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void send(Packet<?> packet, CallbackInfo callback) {
        if (PacketLib.INSTANCE.getPacketType(packet) != PacketLib.PacketType.CLIENTSIDE)
            return;

        if (!PacketLib.INSTANCE.handleSendPacket(packet)) {
            final PacketEvent event = new PacketEvent(packet, PacketEvent.Type.SEND);
            Leaf.eventManager.callEvent(event);

            if (event.isCancelled())
                callback.cancel();
        }
    }

    @Inject(method = "createNetworkManagerAndConnect", at = @At("HEAD"), cancellable = true)
    private static void createNetworkManagerAndConnect(InetAddress address, int serverPort, boolean useNativeTransport, CallbackInfoReturnable<NetworkManager> cir) {}

    @Redirect(method = "checkDisconnected", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V"))
    public void checkDisconnectedLoggerWarn(Logger instance, String s) {}
}