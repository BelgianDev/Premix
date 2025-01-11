package userend.mixin;

import be.raft.premix.annotation.Inject;
import be.raft.premix.annotation.Mixin;
import fr.atlasworld.common.file.reader.JsonFileReader;
import userend.InjectorClassInterface;

import java.io.IOException;

@Mixin(JsonFileReader.class)
public class InjectorClass implements InjectorClassInterface {

    @Inject(target = "writeRaw")
    private void injectWrite() throws IOException {
        System.out.println("Successfully injected into " + this.getClass().getSimpleName());
    }
}
