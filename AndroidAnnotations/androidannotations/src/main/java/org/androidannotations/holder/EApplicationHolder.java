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
package org.androidannotations.holder;

import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.api.CreatorFacade;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class EApplicationHolder extends EComponentHolder {

	public static final String GET_APPLICATION_INSTANCE = "getInstance";

	private JFieldVar staticInstanceField;

	private TargetAnnotationHelper annotationHelper;

	public EApplicationHolder(ProcessHolder processHolder, TypeElement annotatedElement, TargetAnnotationHelper annotationHelper) throws Exception {
		super(processHolder, annotatedElement);

		this.annotationHelper = annotationHelper;

		createSingleton();
		createOnCreate();
	}

	private void createSingleton() {
		JClass annotatedComponent = generatedClass._extends();

		staticInstanceField = generatedClass.field(PRIVATE | STATIC, annotatedComponent, "INSTANCE_");
		// Static singleton getter and setter
		JMethod getInstance = generatedClass.method(PUBLIC | STATIC, annotatedComponent, GET_APPLICATION_INSTANCE);
		getInstance.body()._return(staticInstanceField);

		JMethod setInstance = generatedClass.method(PUBLIC | STATIC, codeModel().VOID, "setForTesting");
		setInstance.javadoc().append("Visible for testing purposes");
		JVar applicationParam = setInstance.param(annotatedComponent, "application");
		setInstance.body().assign(staticInstanceField, applicationParam);
	}

	private void createOnCreate() {
		JMethod onCreate = generatedClass.method(PUBLIC, codeModel().VOID, "onCreate");
		onCreate.annotate(Override.class);
		JBlock onCreateBody = onCreate.body();
		onCreateBody.assign(staticInstanceField, _this());
		JClass creatorFacade = refClass(CreatorFacade.class);

		List<DeclaredType> creators = annotationHelper.extractAnnotationClassArrayParameter(getAnnotatedElement(), EApplication.class.getCanonicalName(), "creators");

		if (creators != null) {
			for (DeclaredType creator : creators) {
				onCreateBody.add(creatorFacade.staticInvoke("addCreator").arg(JExpr._new(refClass(creator + ModelConstants.GENERATION_SUFFIX))));
			}
		}

		onCreateBody.invoke(getInit());
		onCreateBody.invoke(_super(), onCreate);
	}

	@Override
	protected void setContextRef() {
		contextRef = JExpr._this();
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init_");
	}
}
