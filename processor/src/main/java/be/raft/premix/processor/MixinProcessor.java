package be.raft.premix.processor;

import be.raft.premix.annotation.Mixin;
import be.raft.premix.processor.entity.MixinInjector;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"be.raft.premix.annotation.Mixin"})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class MixinProcessor extends AbstractProcessor {
    private final Map<String, List<MixinInjector>> targets;

    public MixinProcessor() {
        this.targets = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Collect all elements
        roundEnv.getElementsAnnotatedWith(Mixin.class).parallelStream()
                .filter(this::mixinInjectorClassValid)
                .map(element -> (TypeElement) element)
                .forEach(this::processMixin);

        Elements elements = processingEnv.getElementUtils();
        TypeElement element = elements.getTypeElement("fr.atlasworld.common.file.reader.JsonFileReader");
        this.log(Diagnostic.Kind.NOTE, String.valueOf(element));

        try {
            byte[] bytes = getClassBytecode("fr.atlasworld.common.file.reader.JsonFileReader");

            System.out.println(bytes.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.targets.forEach((target, injectors) -> {
            try {
                ClassEditor editor = ClassEditor.create(target);
                this.log(Diagnostic.Kind.NOTE, "Opened '" + target + "' for injection.");
            } catch (IOException ex) {
                this.log(Diagnostic.Kind.ERROR, "Failed to inject into class  '" + target + "': " + ex.getMessage());
            }
        });

        return true;
    }

    public static byte[] getClassBytecode(String className) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String classFileName = className.replace('.', '/') + ".class";
        InputStream inputStream = classLoader.getResourceAsStream(classFileName);

        if (inputStream == null) {
            throw new ClassNotFoundException("Class not found lol: " + className);
        }

        byte[] bytecode = inputStream.readAllBytes();
        inputStream.close();
        return bytecode;
    }

    private boolean mixinInjectorClassValid(Element element) {
        if (element.getKind() != ElementKind.CLASS) {
            this.log(Diagnostic.Kind.ERROR, "@Mixin can only be applied to classes: " + element);
            return false;
        }

        if (element.getKind() == ElementKind.INTERFACE) {
            this.log(Diagnostic.Kind.ERROR, "@Mixin cannot be applied to interfaces: " + element);
        }

        if (!element.getModifiers().contains(Modifier.ABSTRACT))
            this.log(Diagnostic.Kind.WARNING, element + " should be abstract.");

        return true;
    }

    @SuppressWarnings("unchecked")
    private void processMixin(TypeElement element) {
        AnnotationMirror mixinAnnotation = element.getAnnotationMirrors().stream()
                .filter(annotation -> annotation.getAnnotationType().toString().equals(Mixin.class.getName()))
                .findFirst().orElseThrow(() -> new IllegalStateException("No @Mixin annotation found: " + element));

        MixinInjector injector = MixinInjector.create(this, mixinAnnotation);

        synchronized (this.targets) {
            injector.targets().forEach(target -> {
                this.targets.computeIfAbsent(target, unused -> new ArrayList<>()).add(injector);
            });
        }
    }

    public synchronized void log(Diagnostic.Kind kind, String message) {
        this.processingEnv.getMessager().printMessage(kind, message);
    }
}
