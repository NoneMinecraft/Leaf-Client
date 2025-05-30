package net.nonemc.leaf.injection.transformers;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.IClassTransformer;
import net.nonemc.leaf.features.special.AntiForge;
import net.nonemc.leaf.libs.asm.BytecodeLib;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Transform bytecode of classes
 */
public class ForgeNetworkTransformer implements IClassTransformer {

    public static boolean returnMethod() {
        return AntiForge.INSTANCE.getEnabled() && AntiForge.INSTANCE.getBlockFML() && !Minecraft.getMinecraft().isIntegratedServerRunning();
    }

    /**
     * Transform a class
     *
     * @param name            of target class
     * @param transformedName of target class
     * @param basicClass      bytecode of target class
     * @return new bytecode
     */
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (System.getProperty("dev-mode") != null)
            return basicClass;

        if (name.equals("net.minecraftforge.fml.common.network.handshake.NetworkDispatcher")) {
            try {
                final ClassNode classNode = BytecodeLib.INSTANCE.toClassNode(basicClass);

                classNode.methods.stream().filter(methodNode -> methodNode.name.equals("handleVanilla")).forEach(methodNode -> {
                    final LabelNode labelNode = new LabelNode();

                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), BytecodeLib.INSTANCE.toNodes(
                            new MethodInsnNode(INVOKESTATIC, "net/nonemc/leaf/injection/transformers/ForgeNetworkTransformer", "returnMethod", "()Z", false),
                            new JumpInsnNode(IFEQ, labelNode),
                            new InsnNode(ICONST_0),
                            new InsnNode(IRETURN),
                            labelNode
                    ));
                });

                return BytecodeLib.INSTANCE.toBytes(classNode);
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        if (name.equals("net.minecraftforge.fml.common.network.handshake.HandshakeMessageHandler")) {
            try {
                final ClassNode classNode = BytecodeLib.INSTANCE.toClassNode(basicClass);

                classNode.methods.stream().filter(method -> method.name.equals("channelRead0")).forEach(methodNode -> {
                    final LabelNode labelNode = new LabelNode();

                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), BytecodeLib.INSTANCE.toNodes(
                            new MethodInsnNode(INVOKESTATIC,
                                    "net/nonemc/leaf/injection/transformers/ForgeNetworkTransformer",
                                    "returnMethod", "()Z", false
                            ),
                            new JumpInsnNode(IFEQ, labelNode),
                            new InsnNode(RETURN),
                            labelNode
                    ));
                });

                return BytecodeLib.INSTANCE.toBytes(classNode);
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return basicClass;
    }
}