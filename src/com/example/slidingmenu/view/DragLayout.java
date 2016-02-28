package com.example.slidingmenu.view;

import com.example.slidingmenu.util.EvaluateUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DragLayout extends FrameLayout {

	private ViewDragHelper mViewDragHelper;
	private View mContent;
	private View mMenu;

	public enum State {
		COLSE, OPEN, DRAGGING
	}

	private State mState = State.COLSE;

	public State getState() {
		return mState;
	}

	private OnStateChangeListener mListener;

	public interface OnStateChangeListener {
		void onOpen();

		void onColse();

		void onDragging(float percent);
	}

	public void setOnStateChangeListener(OnStateChangeListener listener) {
		mListener = listener;
	}

	private Callback callBack = new Callback() {
		/**
		 * child:当前被触摸的子view ; pointerId:多点触摸的ID ; 返回值决定是否需要处理拖拽
		 */
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			// Log.e("Tag", (child.getId() == R.id.main_content) + "");

			return child == mContent || child == mMenu;
		}

		/**
		 * left：view的left值，viewDragHelper认为的left值; dx：与上次left值得差 ;
		 * 返回值决定了View的水平位置
		 */
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// Log.e("left", "left: " + left + " dx: " + dx);
			return fixLeft(left);
		}

		/**
		 * 不真正决定View的拖拽范围，必须重写，返回值为真正的拖拽范围，这个方法会影响松手的动画 必须返回一个大于0的值，如果小于零，则无法滑动
		 */
		public int getViewHorizontalDragRange(View child) {
			return mDragRange;

		}

		/**
		 * 当View的位置发生改变的时候调用 ; changedView：位置改变的view ; left：view位置改变之后的left值 ;
		 * dx：
		 */
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			Log.e("tag", "left: " + left + " oldLeft: " + changedView.getLeft() + " dx: " + dx);
			// 只要左侧的菜单面板滑动，就将其放置回原来位置
			if (changedView == mMenu) {
				mMenu.layout(0, 0, mWidth, mHeight);
				int newLeft = mContent.getLeft() + dx;
				newLeft = fixLeft(newLeft);
				// 重新放置主面板的位置
				mContent.layout(newLeft, 0, newLeft + mWidth, mHeight);
			}

			dispatchDragState(mContent.getLeft());
			invalidate();// 解决2.3源码中无重绘的问题
		}

		/**
		 * 当View释放时调用
		 */
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (xvel < 0.0f) {
				close(true);
			} else if (xvel == 0.0f) {
				if (mContent.getLeft() > mDragRange / 2) {
					open(true);
				} else {
					close(true);
				}
			} else {
				open(true);
			}
		}

	};

	private int mMenuWidth;
	private int mWidth;
	private int mDragRange;
	private int mHeight;

	public DragLayout(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {

		// 参数1：需要被监听的父view
		// 参数2：回调，用于提供信息，接收事件
		mViewDragHelper = ViewDragHelper.create(this, callBack);
	}

	private int fixLeft(int left) {
		if (left < 0) {
			return 0;
		} else if (left > mDragRange) {
			return mDragRange;
		}
		return left;
	}

	public void open() {
		int finalLeft = mDragRange;
		mContent.layout(finalLeft, 0, finalLeft + mWidth, mHeight);
	}

	public void open(boolean isSmooth) {
		if (isSmooth) {
			// 触发一个平滑动画，并计算第一帧
			mViewDragHelper.smoothSlideViewTo(mContent, mDragRange, 0);
			invalidate();
		} else {
			open();
		}
	}

	public void close() {
		int finalLeft = 0;
		mContent.layout(finalLeft, 0, finalLeft + mWidth, mHeight);
	}

	public void close(boolean isSmooth) {
		if (isSmooth) {
			mViewDragHelper.smoothSlideViewTo(mContent, 0, 0);
			invalidate();
		} else {
			close();
		}
	}

	/**
	 * 伴随动画
	 * 
	 * @param left
	 */
	protected void dispatchDragState(int left) {
		float percent = left * 1.0f / mDragRange;
		animViews(left);

		if (mListener != null) {
			State preState = mState;
			mState = updateState(percent);
			mListener.onDragging(percent);
			if (mState != preState) {
				if (mState == State.COLSE) {
					mListener.onColse();
				} else if (mState == State.OPEN) {
					mListener.onOpen();
				}
			}
			// if (mState != currentState) {
			// mState = currentState;
			// if (currentState == State.COLSE) {
			// mListener.onColse();
			// } else if (currentState == State.OPEN) {
			// mListener.onOpen();
			// }
			// }
			//
			// if (currentState == State.DRAGGING) {
			// mListener.onDragging(percent);
			// }
		}
	}

	private State updateState(float percent) {
		if (percent == 0.0f) {
			return State.COLSE;
		} else if (percent == 1.0f) {
			return State.OPEN;
		} else {
			return State.DRAGGING;
		}
	}

	private void animViews(int left) {
		float perContent = 1.0f - left * 0.2f / mDragRange;
		mContent.setScaleX(perContent);
		mContent.setScaleY(perContent);

		float perMenu = 0.5f + left * 0.5f / mDragRange;
		mMenu.setScaleX(perMenu);
		mMenu.setScaleY(perMenu);

		mMenu.setTranslationX(-mWidth * 0.5f + left * 0.5f * mWidth / mDragRange);
		mMenu.setAlpha(0.5f + left * 0.5f / mDragRange);

		getBackground().setColorFilter(
				(Integer) EvaluateUtil.evaluateArgb(left * 1.0f / mDragRange, Color.BLACK, Color.TRANSPARENT),
				Mode.SRC_OVER);
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		// continueSettling:如果需要不断重画view，则返回为true
		if (mViewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mViewDragHelper.processTouchEvent(event);
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 让viewDragHelper决定是否需要拦截事件
		return mViewDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (getChildCount() != 2) {
			throw new RuntimeException("You must have two child view!");
		}

		if (!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1) instanceof ViewGroup)) {
			throw new RuntimeException("Your child view must be ViewGroup!");
		}
		mMenu = getChildAt(0);
		mContent = getChildAt(1);

	}

	/**
	 * View大小改变后的调用，在这个方法里可以获得view和子view的宽高
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mWidth = mContent.getMeasuredWidth();
		mHeight = mContent.getMeasuredHeight();
		mDragRange = (int) (mWidth * 0.6f);

	}
}
