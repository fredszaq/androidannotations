package org.androidannotations.holder;

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;

public class ECreatorHolder extends BaseGeneratedClassHolder {

	private static final String GET_BEAN_TYPE_PARAM = "T";
	private static final String GET_BEAN = "getBean";

	public ECreatorHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		implementMethods();
	}

	@Override
	protected void setGeneratedClass() throws Exception {
		String annotatedComponentQualifiedName = annotatedElement.getQualifiedName().toString();
		String subComponentQualifiedName = annotatedComponentQualifiedName + ModelConstants.GENERATION_SUFFIX;
		JClass annotatedComponent = codeModel().directClass(annotatedComponentQualifiedName);
		generatedClass = codeModel()._class(PUBLIC | FINAL, subComponentQualifiedName, ClassType.CLASS);
		generatedClass._implements(annotatedComponent);
	}

	public JMethod implementMethod(ExecutableElement element) {
		List<ExecutableElement> methods = codeModelHelper.getMethods(getAnnotatedElement());
		List<? extends VariableElement> parameters = element.getParameters();
		String[] paramStrings = new String[parameters.size()];
		for (int j = 0; j < paramStrings.length; j++) {
			paramStrings[j] = parameters.get(j).asType().toString();
		}

		String methodName = element.getSimpleName().toString();

		JMethod method = codeModelHelper.implementMethod(this, methods, methodName, element.getReturnType().toString(), paramStrings);
		return method;

	}

	private void implementMethods() {
		List<ExecutableElement> methods = codeModelHelper.getMethods(getAnnotatedElement());
		implementGetBean(methods);
	}

	private void implementGetBean(List<ExecutableElement> methods) {
		JMethod getBeanMethod = codeModelHelper.implementMethod(this, methods, GET_BEAN, GET_BEAN_TYPE_PARAM, Class.class.getCanonicalName() + "<" + GET_BEAN_TYPE_PARAM + ">", CanonicalNameConstants.CONTEXT);

		for (ExecutableElement executableElement : methods) {
			if (!GET_BEAN.equals(executableElement.getSimpleName().toString())) {
				getBeanMethod.body()//
						._if(refClass(executableElement.getReturnType().toString()).dotclass().invoke("equals").arg(getBeanMethod.listParams()[0]))//
						._then()//
						._return(JExpr.cast(getBeanMethod.typeParams()[0], invoke(executableElement.getSimpleName().toString()).arg(getBeanMethod.listParams()[1])));
			}
		}
		getBeanMethod.body()._return(_null());

	}

}
