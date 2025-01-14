package be.raft.premix.asm;

import be.raft.premix.mixin.MixinConfigFile;
import be.raft.premix.mixin.store.MixinDefinition;
import fr.atlasworld.common.logging.LogUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import static be.raft.premix.MixinProcessor.ASM_VERSION;

public class MixinAnnotationVisitor extends AnnotationVisitor {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String VALUE_FIELD = "value";
    private static final String TARGET_FIELD = "target";
    private static final String PRIORITY_FIELD = "priority";

    private final String className;
    private final Set<String> targets;

    private int priority;

    public MixinAnnotationVisitor(String className) {
        super(ASM_VERSION);

        this.className = className;
        this.targets = new HashSet<>();
    }

    @Override
    public void visit(String name, Object value) {
        if (!name.equals(PRIORITY_FIELD))
            return;

        this.priority = (int) value;
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        LOGGER.info("Visiting array: {}", name);
        return switch (name) {
            case VALUE_FIELD -> this.visit((annotationName, value) -> {
                if (!(value instanceof Type type)) {
                    LOGGER.error("Skipped: Unexpected value type: '{}' in '{}'", value, this.className);
                    return;
                }

                targets.add("L" + type.getClassName().replace(".", "/") + ";");
            });
            case TARGET_FIELD -> this.visit((annotationName, value) -> {
                String string = (String) value;
                if (!string.startsWith("L") || !string.endsWith(";")) {
                    LOGGER.error("Skipped: Invalid mixin target: '{}' in '{}'", string, this.className);
                    return;
                }

                this.targets.add((String) value);
            });
            default -> super.visitArray(name);
        };
    }

    private AnnotationVisitor visit(BiConsumer<String, Object> visitor) {
        return new AnnotationVisitor(ASM_VERSION) {
            @Override
            public void visit(String name, Object value) {
                visitor.accept(name, value);
            }
        };
    }

    public void validate(MixinConfigFile file) {
        for (String target : this.targets) {
            String userReadableName = target.substring(1, target.length() - 1).replace('.', '/');
            if (file.isProtected(userReadableName))
                throw new IllegalStateException("Mixin class '" + this.className + "' attempts to inject in protected class '" + userReadableName + "'!");
        }
    }

    public MixinDefinition produceDefinition() {
        return new MixinDefinition(this.targets, this.priority);
    }
}
