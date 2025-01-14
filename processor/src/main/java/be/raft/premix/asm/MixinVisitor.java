package be.raft.premix.asm;

import be.raft.premix.annotation.Mixin;
import be.raft.premix.mixin.MixinConfigFile;
import be.raft.premix.mixin.store.MixinDefinition;
import be.raft.premix.mixin.store.MixinStore;
import fr.atlasworld.common.logging.LogUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import static be.raft.premix.MixinProcessor.ASM_VERSION;

public class MixinVisitor extends ClassVisitor {
    private static final String ANNOTATION = "L" + Mixin.class.getName().replace('.', '/') + ";";
    private static final Logger LOGGER = LogUtils.getLogger();

    private String className;
    private boolean hasAnnotation;

    // Visitors
    private MixinAnnotationVisitor annotationVisitor;
    private Set<InjectMethodVisitor> methodVisitors;

    private MixinVisitor() {
        super(ASM_VERSION);
    }

    public static MixinStore parseMixin(MixinConfigFile file, ClassLoader loader, String className) throws IOException {
        try (InputStream stream = loader.getResourceAsStream(className.replace(".", "/") + ".class")) {
            if (stream == null)
                throw new IllegalArgumentException("Mixin marked class '" + className + "' not found!");

            ClassReader reader = new ClassReader(stream);
            MixinVisitor visitor = new MixinVisitor();

            reader.accept(visitor, 0); // Parse the actual class.
            visitor.validate(file); // Validate parsed data.

            return visitor.produceStore();
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name.replace("/", ".");
        this.annotationVisitor = new MixinAnnotationVisitor(this.className);

        LOGGER.info("Visiting: {}", this.className);

        if ((access & Opcodes.ACC_INTERFACE) != 0)
            throw new IllegalStateException("Mixin class '" + this.className + "' cannot be an interface!");

        if ((access & Opcodes.ACC_ABSTRACT) == 0)
            LOGGER.warn("Mixin class '{}' should be abstract.", this.className);

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        LOGGER.info("Visiting annotation: {}", descriptor);

        if (!descriptor.equals(ANNOTATION))
            return super.visitAnnotation(descriptor, visible);

        this.hasAnnotation = true;
        return this.annotationVisitor;
    }

    public void validate(MixinConfigFile file) {
        if (!this.hasAnnotation)
            throw new IllegalStateException("Mixin class '" + this.className + "' is not annotated with @'" + Mixin.class.getName() + "'!");

        this.annotationVisitor.validate(file); // Delegate validation to visitor
        for (InjectMethodVisitor visitor : this.methodVisitors) {
            visitor.validate();
        }
    }

    public MixinStore produceStore() {
        MixinDefinition definition = this.annotationVisitor.produceDefinition();
        return new MixinStore(definition, this.methodVisitors.stream()
                .map(InjectMethodVisitor::produceDefinition).collect(Collectors.toUnmodifiableSet()));
    }
}
