package net.nonemc.leaf.script.remapper.injection.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import net.nonemc.leaf.libs.asm.BytecodeLib;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import static org.objectweb.asm.Opcodes.*;

public class AbstractJavaLinkerTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals("jdk.internal.dynalink.beans.AbstractJavaLinker")) {
            try {
                final ClassNode classNode = BytecodeLib.INSTANCE.toClassNode(basicClass);

                classNode.methods.forEach(methodNode -> {
                    switch (methodNode.name + methodNode.desc) {
                        case "addMember(Ljava/lang/String;Ljava/lang/reflect/AccessibleObject;Ljava/util/Map;)V":
                            methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), BytecodeLib.INSTANCE.toNodes(
                                    new VarInsnNode(ALOAD, 0),
                                    new FieldInsnNode(GETFIELD, "jdk/internal/dynalink/beans/AbstractJavaLinker", "clazz", "Ljava/lang/Class;"),
                                    new VarInsnNode(ALOAD, 1),
                                    new VarInsnNode(ALOAD, 2),
                                    new MethodInsnNode(INVOKESTATIC, "net/nonemc/leaf/script/remapper/injection/transformers/handlers/AbstractJavaLinkerHandler", "addMember", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/reflect/AccessibleObject;)Ljava/lang/String;", false),
                                    new VarInsnNode(ASTORE, 1)
                            ));
                            break;
                        case "addMember(Ljava/lang/String;Ljdk/internal/dynalink/beans/SingleDynamicMethod;Ljava/util/Map;)V":
                            methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), BytecodeLib.INSTANCE.toNodes(
                                    new VarInsnNode(ALOAD, 0),
                                    new FieldInsnNode(GETFIELD, "jdk/internal/dynalink/beans/AbstractJavaLinker", "clazz", "Ljava/lang/Class;"),
                                    new VarInsnNode(ALOAD, 1),
                                    new MethodInsnNode(INVOKESTATIC, "net/nonemc/leaf/script/remapper/injection/transformers/handlers/AbstractJavaLinkerHandler", "addMember", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/String;", false),
                                    new VarInsnNode(ASTORE, 1)
                            ));
                            break;
                        case "setPropertyGetter(Ljava/lang/String;Ljdk/internal/dynalink/beans/SingleDynamicMethod;Ljdk/internal/dynalink/beans/GuardedInvocationComponent$ValidationType;)V":
                            methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), BytecodeLib.INSTANCE.toNodes(
                                    new VarInsnNode(ALOAD, 0),
                                    new FieldInsnNode(GETFIELD, "jdk/internal/dynalink/beans/AbstractJavaLinker", "clazz", "Ljava/lang/Class;"),
                                    new VarInsnNode(ALOAD, 1),
                                    new MethodInsnNode(INVOKESTATIC, "net/nonemc/leaf/script/remapper/injection/transformers/handlers/AbstractJavaLinkerHandler", "setPropertyGetter", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/String;", false),
                                    new VarInsnNode(ASTORE, 1)
                            ));
                            break;
                    }
                });

                return BytecodeLib.INSTANCE.toBytes(classNode);
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return basicClass;
    }

}