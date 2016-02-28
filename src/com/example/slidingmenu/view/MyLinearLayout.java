package com.example.slidingmenu.view;

import com.example.slidingmenu.view.DragLayout.State;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {

	private DragLayout dragLayout;

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public void setDragLayout(DragLayout dragLayout) {
		this.dragLayout = dragLayout;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (dragLayout != null) {
			if (dragLayout.getState() == State.OPEN && event.getAction() == MotionEvent.ACTION_UP) {
				dragLayout.close(true);
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (dragLayout != null) {
			if (dragLayout.getState() != State.COLSE) {
				return true;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

}
