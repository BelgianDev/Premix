package be.raft.premix;

import be.raft.premix.mixin.MixinConfigFile;
import be.raft.premix.mixin.store.MixinStore;
import fr.atlasworld.common.logging.LogUtils;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLClassLoader;

public class MixinProcessor {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int ASM_VERSION = Opcodes.ASM9;

    private final File input;
    private final File output;

    private URLClassLoader loader;

    public static void main(String[] args) throws IOException {
        LaunchArgs arguments = LaunchArgs.parse(args);
        new MixinProcessor(arguments).run();
    }

    public MixinProcessor(LaunchArgs args) throws IOException {
        this.input = args.input();
        this.output = args.output();

        this.loader = new URLClassLoader(new URL[]{ this.input.toURI().toURL() });
    }

    public void run() throws IOException {
        if (this.loader.getResource(MixinConfigFile.FILE) == null) {
            LOGGER.warn("Input jar does not contain '{}' file.", MixinConfigFile.FILE);
            this.writeOutput();
            return; // End process here
        }

        MixinConfigFile mixinFile = MixinConfigFile.loadFromCandidate(this.loader);
        mixinFile.mixinClasses().forEach(mixinClass -> {
            try {
                MixinStore store = MixinStore.load(mixinFile, this.loader, mixinClass);
                LOGGER.info("Loaded mixin class {}", store);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });
    }

    public void writeOutput() throws IOException {

    }
}

