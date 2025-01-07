package be.raft.premix.processor;

import be.raft.premix.annotation.Mixin;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"be.raft.premix.annotation.Mixin"})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class MixinProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Mixin.class);

        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, annotatedElements.toString());

        annotatedElements.forEach(element -> {
            if (element.getKind().isInterface()) {
                this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Interfaces cannot be annotated with @Mixin: ", element);
                return;
            }


        });

        return true;
    }

    private void processMixin(String className, Mixin modifier) {

    }
}
