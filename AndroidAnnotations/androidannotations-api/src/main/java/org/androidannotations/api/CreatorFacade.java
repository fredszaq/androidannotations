package org.androidannotations.api;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class CreatorFacade {

	public interface Creator {
		<T> T getBean(Class<T> clazz, Context context);
	}

	private static final List<Creator> creators = new ArrayList<Creator>();

	public static void addCreator(Creator creator) {
		creators.add(creator);
	}

	public static <T> T getBean(Class<T> clazz, Context context) {
		T result = null;
		for (Creator creator : creators) {
			T bean = creator.getBean(clazz, context);
			if (result == null) {
				result = bean;
			} else {
				throw new IllegalStateException("More than one creator for class " + clazz);
			}
		}
		if (result == null) {
			throw new IllegalStateException("No creator found for class " + clazz);
		}
		return result;
	}

}
