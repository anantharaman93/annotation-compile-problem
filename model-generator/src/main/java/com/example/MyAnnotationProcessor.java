package com.example;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class MyAnnotationProcessor extends AbstractProcessor
{
	private static final Class<? extends Annotation> ANNOTATION_CLASS = MyAnnotation.class;
	
	private static final String ANNOTATION_CLASS_NAME = ANNOTATION_CLASS.getName();
	
	@Override
	public Set<String> getSupportedAnnotationTypes()
	{
		return Collections.singleton(ANNOTATION_CLASS_NAME);
	}
	
	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		if (annotations.stream().noneMatch(typeElement -> typeElement.toString().equals(ANNOTATION_CLASS_NAME)))
		{
			return false;
		}
		
		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ANNOTATION_CLASS);
		
		for (Element element : elements)
		{
			ExecutableElement function = element.getEnclosedElements().stream()
					.filter(e -> e.getKind() == ElementKind.METHOD)
					.map(ExecutableElement.class::cast)
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("No Function"));
			
			TypeMirror functionReturnType = function.getReturnType();
			TypeElement employeeElement = (TypeElement) processingEnv.getTypeUtils().asElement(functionReturnType);
			
			ExecutableElement constructor = employeeElement.getEnclosedElements().stream()
					.filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
					.map(ExecutableElement.class::cast)
					.filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("No PUBLIC CONSTRUCTOR"));
			
			String variableNames = constructor.getParameters().stream()
					.map(VariableElement::getSimpleName)
					.map(String::valueOf)
					.collect(Collectors.joining(", "));
			
			processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Generated variableNames :: " + variableNames, constructor);
		}
		
		return false;
	}
}
