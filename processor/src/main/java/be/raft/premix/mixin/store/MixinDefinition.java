package be.raft.premix.mixin.store;

import java.util.Set;

public record MixinDefinition(Set<String> targetClasses, int priority) {

}
