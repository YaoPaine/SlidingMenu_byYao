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
		 * child:��ǰ����������view ; pointerId:��㴥����ID ; ����ֵ�����Ƿ���Ҫ������ק
		 */
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			// Log.e("Tag", (child.getId() == R.id.main_content) + "");

			return child == mContent || child == mMenu;
		}

		/**
		 * left��view��leftֵ��viewDragHelper��Ϊ��leftֵ; dx�����ϴ�leftֵ�ò� ;
		 * ����ֵ������View��ˮƽλ��
		 */
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// Log.e("left", "left: " + left + " dx: " + dx);
			return fixLeft(left);
		}

		/**
		 * ����������View����ק��Χ��������д������ֵΪ��������ק��Χ�����������Ӱ�����ֵĶ��� ���뷵��һ������0��ֵ�����С���㣬���޷�����
		 */
		public int getViewHorizontalDragRange(View child) {
			return mDragRange;

		}

		/**
		 * ��View��λ�÷����ı��ʱ����� ; changedView��λ�øı��view ; left��viewλ�øı�֮���leftֵ ;
		 * dx��
		 */
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			Log.e("tag", "left: " + left + " oldLeft: " + changedView.getLeft() + " dx: " + dx);
			// ֻҪ���Ĳ˵���廬�����ͽ�����û�ԭ��λ��
			if (changedView == mMenu) {
				mMenu.layout(0, 0, mWidth, mHeight);
				int newLeft = mContent.getLeft() + dx;
				newLeft = fixLeft(newLeft);
				// ���·���������λ��
				mContent.layout(newLeft, 0, newLeft + mWidth, mHeight);
			}

			dispatchDragState(mContent.getLeft());
			invalidate();// ���2.3Դ�������ػ������
		}

		/**
		 * ��View�ͷ�ʱ����
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

		// ����1����Ҫ�������ĸ�view
		// ����2���ص��������ṩ��Ϣ�������¼�
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
			// ����һ��ƽ���������������һ֡
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
	 * ���涯��
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
		// continueSettling:�����Ҫ�����ػ�view���򷵻�Ϊtrue
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
		// ��viewDragHelper�����Ƿ���Ҫ�����¼�
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
	 * View��С�ı��ĵ��ã��������������Ի��view����view�Ŀ��
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mWidth = mContent.getMeasuredWidth();
		mHeight = mContent.getMeasuredHeight();
		mDragRange = (int) (mWidth * 0.6f);

	}
}
