package org.androidannotations.test15.creator;

import android.app.Application;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;


@EApplication(creators = BasicCreator.class)
public class CreatorApplication extends Application {
    @Bean // no value, we want the implementing class to be retrieved from the creator
    SomeInterface injectedBean;

}
