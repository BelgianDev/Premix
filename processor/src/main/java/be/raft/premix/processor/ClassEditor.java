package be.raft.premix.processor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;

public class ClassEditor {
    private final ClassReader reader;
    private final ClassWriter writer;

    private ClassEditor(ClassReader reader) {
        this.reader = reader;
        this.writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    }

    public static ClassEditor create(String className) throws IOException {
        return new ClassEditor(new ClassReader(className));
    }

    public static ClassEditor create(byte[] classBytes) {
        return new ClassEditor(new ClassReader(classBytes));
    }
}
