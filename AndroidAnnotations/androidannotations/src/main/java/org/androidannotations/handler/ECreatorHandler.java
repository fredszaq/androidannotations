package org.androidannotations.handler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.annotations.ECreator;
import org.androidannotations.holder.ECreatorHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.process.ProcessHolder;

public class ECreatorHandler extends BaseAnnotationHandler<ECreatorHolder> implements GeneratingAnnotationHandler<ECreatorHolder> {

	public ECreatorHandler(ProcessingEnvironment processingEnvironment) {
		super(ECreator.class, processingEnvironment);
	}

	@Override
	public void process(Element element, ECreatorHolder holder) throws Exception {

	}

	@Override
	public ECreatorHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new ECreatorHolder(processHolder, annotatedElement);
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		TypeElement typeElement = (TypeElement) element;

		validatorHelper.isInterface(typeElement, valid);

		validatorHelper.extendsCreator(typeElement, valid);
	}

}
