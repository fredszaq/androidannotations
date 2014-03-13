package org.androidannotations.handler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.Creates;
import org.androidannotations.annotations.EBean;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.holder.EBeanHolder;
import org.androidannotations.holder.ECreatorHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JMethod;

public class CreatesHandler extends BaseAnnotationHandler<ECreatorHolder> {

	private TargetAnnotationHelper annotationHelper;

	public CreatesHandler(ProcessingEnvironment processingEnvironment) {
		super(Creates.class, processingEnvironment);
		annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public void process(Element element, ECreatorHolder holder) throws Exception {
		JMethod method = holder.implementMethod((ExecutableElement) element);
		method.body()._return(refClass(annotationHelper.extractAnnotationClassParameter(element) + ModelConstants.GENERATION_SUFFIX).staticInvoke(EBeanHolder.GET_INSTANCE_METHOD_NAME).arg(method.listParams()[0]));
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.targetValueHasAnnotationAndIsCompatible(EBean.class, (ExecutableElement) element, valid);
		validatorHelper.methodTakesArgument(CanonicalNameConstants.CONTEXT, (ExecutableElement) element, valid);
	}
}
