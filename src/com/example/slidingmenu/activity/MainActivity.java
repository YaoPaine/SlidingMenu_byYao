package com.example.slidingmenu.activity;

import java.util.Random;

import com.example.slidingmenu.R;
import com.example.slidingmenu.bean.Cheeses;
import com.example.slidingmenu.view.DragLayout;
import com.example.slidingmenu.view.DragLayout.OnStateChangeListener;
import com.example.slidingmenu.view.MyLinearLayout;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ListView contentListView;
	private ListView menuListView;
	private DragLayout dragLayout;
	private ImageView contentHead;
	private MyLinearLayout ll_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		menuListView = (ListView) findViewById(R.id.lv_left);
		contentListView = (ListView) findViewById(R.id.lv_main);
		dragLayout = (DragLayout) findViewById(R.id.dl_main);
		contentHead = (ImageView) findViewById(R.id.iv_head);
		ll_main = (MyLinearLayout) findViewById(R.id.ll_main);

		menuListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
				Cheeses.sCheeseStrings));
		contentListView.setAdapter(
				new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, Cheeses.NAMES) {
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						TextView tv = (TextView) super.getView(position, convertView, parent);
						tv.setTextColor(Color.BLACK);
						return tv;
					}
				});

		dragLayout.setOnStateChangeListener(new OnStateChangeListener() {

			@Override
			public void onOpen() {
				menuListView.smoothScrollToPosition(new Random().nextInt(108));
			}

			@Override
			public void onDragging(float percent) {
				hideAnim(percent);
			}

			@Override
			public void onColse() {
				shockAnim();
			}
		});

		ll_main.setDragLayout(dragLayout);
	}

	protected void hideAnim(float percent) {
		contentHead.setAlpha(1.0f - percent);

	}

	protected void shockAnim() {
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(contentHead, "translationX", 15f).setDuration(600);
		objectAnimator.setInterpolator(new CycleInterpolator(6.0f));
		objectAnimator.start();
	}
}
