package com.pds.fast.ui.common.floating.other;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import com.pds.fast.ui.common.assist.NumberExKt;

// OnCombineEventListener
public class TouchProxy {
    private static final int MIN_DISTANCE_MOVE = 4;
    private static final int MIN_TAP_TIME = 1000;
    private static final int MAX_SINGLE_CLICK_TIME = 220;

    private OnTouchEventListener mEventListener;
    private int mLastX;
    private int mLastY;
    private int mStartX;
    private int mStartY;
    private TouchState mState = TouchState.STATE_STOP;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isSingleClickNotConsume = false;

    public TouchProxy(OnTouchEventListener eventListener) {
        mEventListener = eventListener;
    }

    private enum TouchState {
        STATE_MOVE,
        STATE_STOP
    }

    public boolean onTouchEvent(View v, MotionEvent event) {
        int distance = NumberExKt.dp2px(1) * MIN_DISTANCE_MOVE;
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                handler.removeCallbacks(singleClickRunnable);
                mStartX = x;
                mStartY = y;
                mLastY = y;
                mLastX = x;
                if (mEventListener != null) {
                    mEventListener.onDown(x, y);
                }
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if (Math.abs(x - mStartX) < distance && Math.abs(y - mStartY) < distance) {
                    if (mState == TouchState.STATE_STOP) {
                        break;
                    }
                } else if (mState != TouchState.STATE_MOVE) {
                    mState = TouchState.STATE_MOVE;
                }
                if (mEventListener != null) {
                    mEventListener.onMove(mLastX, mLastY, x - mLastX, y - mLastY);
                }
                mLastY = y;
                mLastX = x;
                mState = TouchState.STATE_MOVE;
            }
            break;
            case MotionEvent.ACTION_UP: {
                if (mEventListener != null) {
                    mEventListener.onUp(x, y);
                }
                if (mState != TouchState.STATE_MOVE && event.getEventTime() - event.getDownTime() < MIN_TAP_TIME) {
                    if (isSingleClickNotConsume) {
                        if (null != mEventListener) {
                            isSingleClickNotConsume = false;
                            mEventListener.onDoubleClick();
                        }
                    } else {
                        isSingleClickNotConsume = true;
                        handler.postDelayed(singleClickRunnable, MAX_SINGLE_CLICK_TIME);
                    }
                }
                mState = TouchState.STATE_STOP;
            }
            break;
            default:
                break;
        }
        return true;
    }

    private final Runnable singleClickRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != mEventListener) {
                isSingleClickNotConsume = false;
                mEventListener.onClick();
            }
        }
    };

    public interface OnTouchEventListener {
        default void onMove(int x, int y, int dx, int dy) {
        }

        default void onUp(int x, int y) {
        }

        default void onDown(int x, int y) {
        }

        default void onClick() {
        }

        default void onDoubleClick() {
        }
    }
}
