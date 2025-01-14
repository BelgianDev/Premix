package be.raft.premix.asm;

import be.raft.premix.annotation.Inject;
import be.raft.premix.mixin.store.MixinAddingMethodDefinition;
import be.raft.premix.mixin.store.MixinInjectionMethodDefinition;
import be.raft.premix.mixin.store.MixinMethodDefinition;
import fr.atlasworld.common.logging.LogUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;

import java.util.Set;

import static be.raft.premix.MixinProcessor.ASM_VERSION;

public class InjectMethodVisitor extends MethodVisitor {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ANNOTATION = "L" + Inject.class.getName() + ";";
    private static final String VALUE_FIELD = "value";

    private InjectAnnotationVisitor visitor;
    private final String className;
    private final String methodName;

    public boolean injector;

    public InjectMethodVisitor(String className, String methodName) {
        super(ASM_VERSION);

        this.visitor = new InjectAnnotationVisitor();

        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        LOGGER.info("Visiting annotation: {}", descriptor);

        if (!descriptor.equals(ANNOTATION))
            return super.visitAnnotation(descriptor, visible);

        this.injector = true; // The method is an injection method
        return this.visitor;
    }

    public void validate() {

    }

    public MixinMethodDefinition produceDefinition() {
        if (this.injector)
            return this.injectionDefinition();

        return addingMethod();
    }

    public MixinInjectionMethodDefinition injectionDefinition() {
        return new MixinInjectionMethodDefinition(this.methodName, this.visitor.targetClassName);
    }

    public MixinAddingMethodDefinition addingMethod() {
        return new MixinAddingMethodDefinition(this.methodName);
    }

    public static class InjectAnnotationVisitor extends AnnotationVisitor {
        private Set<String> targetClassName;

        private InjectAnnotationVisitor() {
            super(ASM_VERSION);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            if (!name.equals(VALUE_FIELD))
                return super.visitArray(name);

            return new AnnotationVisitor(ASM_VERSION) {
                @Override
                public void visit(String name, Object value) {

                }
            }
        }
    }
}
