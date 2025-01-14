package be.raft.premix.mixin.store;

import java.util.Set;

public record MixinStore(MixinDefinition definition, Set<MixinMethodDefinition> methods) {

}
