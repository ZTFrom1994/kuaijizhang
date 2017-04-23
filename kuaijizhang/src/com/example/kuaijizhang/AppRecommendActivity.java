package com.example.kuaijizhang;

import net.youmi.android.offers.OffersManager;
import android.app.Activity;
import android.os.Bundle;

public class AppRecommendActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OffersManager.getInstance(this).showOffersWall();
	}
}
