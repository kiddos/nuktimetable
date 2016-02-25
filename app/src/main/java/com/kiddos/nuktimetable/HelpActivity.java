package com.kiddos.nuktimetable;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class HelpActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		final int[] ids = {
				R.id.tvHelp1, R.id.tvHelp1_1, R.id.tvHelp2,
				R.id.tvHelp3, R.id.tvHelp4, R.id.tvHelp5,
				R.id.help_title, R.id.btnUnderstand
		};
		final Typeface typeface = Typeface.createFromAsset(getAssets(), "custom_font.ttf");
		for (int id : ids) {
			((TextView) findViewById(id)).setTypeface(typeface);
		}

		findViewById(R.id.btnUnderstand).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
