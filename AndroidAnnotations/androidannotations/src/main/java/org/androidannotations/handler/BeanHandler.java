/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.handler;

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.ref;
import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.api.CreatorFacade;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.holder.EBeanHolder;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;

public class BeanHandler extends BaseAnnotationHandler<EComponentHolder> {

	private final TargetAnnotationHelper annotationHelper;

	public BeanHandler(ProcessingEnvironment processingEnvironment) {
		super(Bean.class, processingEnvironment);
		annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.typeOrTargetValueHasAnnotation(EBean.class, element, valid);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		TypeMirror elementType = annotationHelper.extractAnnotationClassParameter(element);
		boolean implicit = false;
		if (elementType == null) {
			elementType = element.asType();
			elementType = holder.processingEnvironment().getTypeUtils().erasure(elementType);
			implicit = true;
		}

		String fieldName = element.getSimpleName().toString();
		String typeQualifiedName = elementType.toString();

		JFieldRef beanField = ref(fieldName);
		JBlock block = holder.getInitBody();
		JClass injectedClass = processHolder.definedClass(typeQualifiedName);

		JClass creatorFacade = refClass(CreatorFacade.class);

		boolean hasNonConfigurationInstanceAnnotation = element.getAnnotation(NonConfigurationInstance.class) != null;
		if (hasNonConfigurationInstanceAnnotation) {
			block = block._if(beanField.eq(_null()))._then();
		}

		JInvocation getInstance;
		Logger logger = LoggerFactory.getLogger(getClass());
		logger.debug("BEGIN");
		logger.debug("" + element);
		logger.debug("" + holder);
		logger.debug("" + injectedClass);
		logger.debug("" + implicit);
		logger.debug("" + injectedClass.isAbstract());
		logger.debug("" + injectedClass.isInterface());

		if (implicit && (injectedClass.isAbstract() || injectedClass.isInterface())) {
			getInstance = creatorFacade.staticInvoke("getBean").arg(injectedClass.dotclass()).arg(holder.getContextRef());
			logger.debug("implicit");
		} else {
			logger.debug("explicit");
			JClass injectedAAClass = refClass(typeQualifiedName + GENERATION_SUFFIX);
			getInstance = injectedAAClass.staticInvoke(EBeanHolder.GET_INSTANCE_METHOD_NAME).arg(holder.getContextRef());
		}
		logger.debug("END");

		block.assign(beanField, getInstance);
	}
}
