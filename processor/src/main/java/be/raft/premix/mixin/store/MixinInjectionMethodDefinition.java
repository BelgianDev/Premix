package be.raft.premix.mixin.store;

import org.objectweb.asm.ClassWriter;

import java.util.Set;

public class MixinInjectionMethodDefinition implements MixinMethodDefinition {
    private final String name;
    private final Set<String> targetMethods;

    public MixinInjectionMethodDefinition(String name, Set<String> targetMethods) {
        this.name = name;
        this.targetMethods = targetMethods;
    }

    @Override
    public String methodName() {
        return this.name;
    }

    @Override
    public boolean isInjector() {
        return true;
    }

    @Override
    public MixinAddingMethodDefinition asAddingMethodDefinition() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MixinInjectionMethodDefinition asInjectionMethodDefinition() {
        return this;
    }

    @Override
    public void process(ClassWriter writer) {

    }
}
