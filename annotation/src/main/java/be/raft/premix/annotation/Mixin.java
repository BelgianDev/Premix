package be.raft.premix.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as an injector, takes in a target class to inject to.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Mixin {

    /**
     * Target class for mixin injection.
     */
    Class<?>[] value();

    /**
     * Sets the priority of the mixin, higher values mean it will be applied first,
     * while lower values will make the mixin apply as last.
     */
    int priority() default 1000;
}
