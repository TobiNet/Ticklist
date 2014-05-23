package org.tobinet.tick;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private boolean doubleBackToExitPressedOnce;
	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMFPA;
	private static final int NUMBER_OF_PAGES = 2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mMFPA = new MyFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mMFPA);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
				super.onBackPressed();
	    finish();
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
	
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				doubleBackToExitPressedOnce=false;
			}
		}, 2000);
	}
	
	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		
		public MyFragmentPagerAdapter (FragmentManager manager){
			super(manager);
		}
		
		@Override
		public Fragment getItem(int index){
			Fragment f = null;
			switch (index){
			case 0:
				f = ItemListFragment.newInstance();
				break;
			case 1:
				f = ItemFragment.newInstance();
				break;
			}
			return f;
		}
		
		@Override
		public int getCount() {
			return NUMBER_OF_PAGES;
		}
	}
}
