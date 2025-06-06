package net.nonemc.leaf.injection.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import net.nonemc.leaf.libs.asm.BytecodeLib;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class OptimizeTransformer implements IClassTransformer {

    private static final HashMap<String, String> transformMap = new HashMap<>();

    static {
        addTransform("net.minecraft.util.EnumFacing", "cq", "facings");
        addTransform("net.minecraft.util.EnumChatFormatting", "a", "chatFormatting");
        addTransform("net.minecraft.util.EnumParticleTypes", "cy", "particleTypes");
        addTransform("net.minecraft.util.EnumWorldBlockLayer", "adf", "worldBlockLayers");
    }

    /**
     * Add transform to transformMap
     *
     * @param mcpName    the normal name look like in developing env
     * @param notchName  the obfuscated name in entity env
     * @param targetName the target method in [StaticStorage]
     */
    private static void addTransform(final String mcpName, final String notchName, final String targetName) {
        transformMap.put(mcpName, targetName);
        transformMap.put(notchName, targetName);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.startsWith("net.minecraft") && basicClass != null && !transformMap.containsKey(transformedName)) {
            try {
                final ClassNode classNode = BytecodeLib.INSTANCE.toClassNode(basicClass);
                AtomicBoolean changed = new AtomicBoolean(false);

                classNode.methods.forEach(methodNode -> {
                    for (int i = 0; i < methodNode.instructions.size(); ++i) {
                        final AbstractInsnNode abstractInsnNode = methodNode.instructions.get(i);
                        if (abstractInsnNode instanceof MethodInsnNode) {
                            MethodInsnNode min = (MethodInsnNode) abstractInsnNode;
                            if (min.getOpcode() == Opcodes.INVOKESTATIC && min.name.equals("values")) {
                                final String owner = min.owner.replaceAll("/", ".");
                                if (transformMap.containsKey(owner)) {
                                    changed.set(true);
                                    min.owner = "net/nonemc/leaf/injection/access/StaticStorage";
                                    min.name = transformMap.get(owner);
                                }
                            }
                        }
                    }
                });

                if (changed.get()) {
                    return BytecodeLib.INSTANCE.toBytes(classNode);
                }
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return basicClass;
    }
}
