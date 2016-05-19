package org.ilapin.arfloor;

import android.support.multidex.MultiDexApplication;

public class App extends MultiDexApplication {

	@Override
	public void onCreate() {
		super.onCreate();

		ModulesHolder.createInstance(this);
	}
}
