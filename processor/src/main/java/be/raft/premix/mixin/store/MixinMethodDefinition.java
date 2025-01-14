package be.raft.premix.mixin.store;

import org.objectweb.asm.ClassWriter;

public interface MixinMethodDefinition {
    String methodName();

    boolean isInjector();

    MixinAddingMethodDefinition asAddingMethodDefinition();
    MixinInjectionMethodDefinition asInjectionMethodDefinition();

    void process(ClassWriter writer);
}
