package com.satispay.capri.builder.processor;

import com.satispay.capri.builder.annotation.Builder;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.satispay.capri.builder.annotation.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class BuilderProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.NOTE,
            "BuilderProcessor: Processing round with annotations: " + annotations
        );

        for (Element element : roundEnv.getElementsAnnotatedWith(Builder.class)) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "BuilderProcessor: Found @Builder on element: " + element.getSimpleName()
            );

            if (element.getKind() != ElementKind.RECORD) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "@Builder annotation can only be applied to records",
                    element
                );
                continue;
            }

            try {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "BuilderProcessor: Generating builder for: " + element.getSimpleName()
                );
                generateBuilder((TypeElement) element);
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "BuilderProcessor: Successfully generated builder for: " + element.getSimpleName()
                );
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed to generate builder: " + e.getMessage(),
                    element
                );
            }
        }
        return true;
    }

    private void generateBuilder(TypeElement recordElement) throws IOException {
        Builder builderAnnotation = recordElement.getAnnotation(Builder.class);
        String packageName = processingEnv.getElementUtils().getPackageOf(recordElement).getQualifiedName().toString();
        String recordName = recordElement.getSimpleName().toString();
        String builderClassName = builderAnnotation.builderClassName().isEmpty()
            ? recordName + "Builder"
            : builderAnnotation.builderClassName();
        String builderMethodName = builderAnnotation.builderMethodName();

        List<? extends Element> recordComponents = recordElement.getEnclosedElements().stream()
            .filter(e -> e.getKind() == ElementKind.RECORD_COMPONENT)
            .toList();

        TypeSpec.Builder builderClassBuilder = TypeSpec.classBuilder(builderClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        // Add fields for each record component
        for (Element component : recordComponents) {
            String fieldName = component.getSimpleName().toString();
            TypeMirror fieldType = component.asType();

            builderClassBuilder.addField(
                FieldSpec.builder(TypeName.get(fieldType), fieldName)
                    .addModifiers(Modifier.PRIVATE)
                    .build()
            );
        }

        // Add setter methods for each component
        for (Element component : recordComponents) {
            String fieldName = component.getSimpleName().toString();
            TypeMirror fieldType = component.asType();

            MethodSpec setterMethod = MethodSpec.methodBuilder(fieldName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(fieldType), fieldName)
                .returns(ClassName.get("", builderClassName))
                .addStatement("this.$N = $N", fieldName, fieldName)
                .addStatement("return this")
                .build();

            builderClassBuilder.addMethod(setterMethod);
        }

        // Add build method
        StringBuilder buildParameters = new StringBuilder();
        for (int i = 0; i < recordComponents.size(); i++) {
            if (i > 0) buildParameters.append(", ");
            buildParameters.append(recordComponents.get(i).toString());
        }

        MethodSpec buildMethod = MethodSpec.methodBuilder("build")
            .addModifiers(Modifier.PUBLIC)
            .returns(ClassName.get(packageName, recordName))
            .addStatement("return new $T($L)",
                ClassName.get(packageName, recordName),
                buildParameters.toString())
            .build();

        builderClassBuilder.addMethod(buildMethod);

        // Create the builder class
        TypeSpec builderClass = builderClassBuilder.build();

        // Add static builder method to the record (as a separate class)
        TypeSpec builderHelperClass = TypeSpec.classBuilder(recordName + "BuilderHelper")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(MethodSpec.methodBuilder(builderMethodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("", builderClassName))
                .addStatement("return new $T()", ClassName.get("", builderClassName))
                .build())
            .addType(builderClass)
            .build();

        JavaFile javaFile = JavaFile.builder(packageName, builderHelperClass)
            .build();

        javaFile.writeTo(processingEnv.getFiler());
    }
}