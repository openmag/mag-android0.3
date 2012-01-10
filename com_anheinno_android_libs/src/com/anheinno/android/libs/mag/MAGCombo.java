package com.anheinno.android.libs.mag;

import android.content.Context;
import android.view.View;

public class MAGCombo extends MAGContainerBase {

	public View initField(Context context) {
		MAGContainerBase.initChildFields(this);
		
		MAGLayoutManager manager = new MAGLayoutManager(context);
		manager.setContainer(this);
		
		return manager;
	}

	public void releaseResources() {
		MAGLayoutManager manager = (MAGLayoutManager)getField();
		manager.releaseResources();
		super.releaseResources();
	}

}
