package be.raft.premix.processor.entity;

import be.raft.premix.processor.MixinProcessor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MixinInjector {
    private static final String VALUE_FIELD = "value";
    private static final String PRIORITY_FIELD = "priority";

    private final List<String> targetClasses;
    private final int priority;

    private MixinInjector(List<String> targetClasses, int priority) {
        this.targetClasses = targetClasses;
        this.priority = priority;
    }

    public List<String> targets() {
        return this.targetClasses;
    }

    public int priority() {
        return this.priority;
    }

    public static MixinInjector create(MixinProcessor processor, AnnotationMirror mixinAnnotation) {
        List<String> targetClasses = new ArrayList<>();
        int priority = 1000;

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mixinAnnotation.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(VALUE_FIELD)) {

                @SuppressWarnings("unchecked")
                List<? extends AnnotationValue> values = (List<? extends AnnotationValue>) entry.getValue().getValue();

                values.forEach(value -> targetClasses.add(value.toString()));
            }

            if (entry.getKey().getSimpleName().toString().equals(PRIORITY_FIELD)) {
                priority = (Integer) entry.getValue().getValue();
            }
        }

        if (targetClasses.isEmpty())
            processor.log(Diagnostic.Kind.ERROR, "No mixin target classes found.");

        return new MixinInjector(targetClasses, priority);
    }
}
