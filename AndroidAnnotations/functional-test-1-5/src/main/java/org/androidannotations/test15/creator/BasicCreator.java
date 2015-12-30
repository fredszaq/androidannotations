package org.androidannotations.test15.creator;

import android.content.Context;

import org.androidannotations.annotations.Creates;
import org.androidannotations.annotations.ECreator;
import org.androidannotations.api.CreatorFacade;


@ECreator
public interface BasicCreator extends CreatorFacade.Creator {

    @Creates(SomeBean.class)
    SomeInterface getSomeInterface(Context context);

}
