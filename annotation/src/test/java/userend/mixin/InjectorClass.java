package userend.mixin;

import be.raft.premix.annotation.Inject;
import be.raft.premix.annotation.Mixin;
import be.raft.premix.annotation.Shadow;
import userend.InjectorClassInterface;

@Mixin(TargetClass.class)
public abstract class InjectorClass implements InjectorClassInterface {

    @Shadow
    private String secretMessage;

    @Inject(target = "doStuff")
    private void doStuffInjector() {
        System.out.println("Successfully injected new stuff in doStuff method!");
        System.out.println("Leaked Secret message: " + this.secretMessage);
    }
}
