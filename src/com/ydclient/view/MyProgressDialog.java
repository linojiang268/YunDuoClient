package com.ydclient.view;

import android.app.ProgressDialog;
import android.content.Context;

public abstract class MyProgressDialog extends ProgressDialog {

	public MyProgressDialog(Context context) {
		super(context);
	}

	@Override
	public void onBackPressed() {
		exit();
		super.onBackPressed();
	}

	protected abstract void exit();
}
