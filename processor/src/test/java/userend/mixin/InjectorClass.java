package userend.mixin;

import be.raft.premix.annotation.Inject;
import be.raft.premix.annotation.Mixin;
import fr.atlasworld.common.file.reader.JsonFileReader;
import userend.InjectorClassInterface;

import java.io.IOException;

@Mixin(value = JsonFileReader.class, priority = 1001)
public class InjectorClass implements InjectorClassInterface {

    @Inject("")
    private void injectWrite() throws IOException {
        System.out.println("Successfully injected into " + this.getClass().getSimpleName());
    }
}
