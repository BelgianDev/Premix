package be.raft.premix.mixin.store;

import org.objectweb.asm.ClassWriter;

public class MixinAddingMethodDefinition implements MixinMethodDefinition {
    private final String name;

    public MixinAddingMethodDefinition(String name) {
        this.name = name;
    }

    @Override
    public String methodName() {
        return this.name;
    }

    @Override
    public boolean isInjector() {
        return false;
    }

    @Override
    public MixinAddingMethodDefinition asAddingMethodDefinition() {
        return this;
    }

    @Override
    public MixinInjectionMethodDefinition asInjectionMethodDefinition() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void process(ClassWriter writer) {
        // TODO: Process Classes
    }
}
