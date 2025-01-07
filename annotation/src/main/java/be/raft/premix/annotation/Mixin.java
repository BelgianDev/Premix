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
}
