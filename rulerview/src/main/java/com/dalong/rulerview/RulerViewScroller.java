package com.dalong.rulerview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

/**
 * Created by dalong on 2016/12/15.
 */

public class RulerViewScroller {

    //滚动的时间
    public static final int SCROLLING_DURATION = 400;

    //用于滚动的最小增量
    public static final int MIN_DELTA_FOR_SCROLLING = 1;

    //Listener
    private ScrollingListener listener;

    //上下文
    private Context context;

    // Scrolling
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private int lastScrollX;
    private float lastTouchedX;
    private boolean isScrollingPerformed;

    private final int MESSAGE_SCROLL = 0;
    private final int MESSAGE_JUSTIFY = 1;


    public RulerViewScroller(Context context, ScrollingListener listener) {
        this.listener = listener;
        this.context = context;
        gestureDetector = new GestureDetector(context, gestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        scroller = new Scroller(context);
        scroller.setFriction(0.05f);

    }


    /**
     * 手势监听
      */
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            lastScrollX = 0;
            scroller.fling(0, lastScrollX, (int) -velocityX, 0, -0x7FFFFFFF, 0x7FFFFFFF, 0, 0);
            setNextMessage(MESSAGE_SCROLL);
            return true;
        }
    };


    /**
     * 手势处理
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchedX = event.getX();
                scroller.forceFinished(true);
                clearMessages();
                break;

            case MotionEvent.ACTION_MOVE:
                int distanceX = (int) (event.getX() - lastTouchedX);
                if (distanceX != 0) {
                    startScrolling();
                    listener.onScroll(distanceX);
                    lastTouchedX = event.getX();
                }
                break;
        }
        //当手指离开控件时
        if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
            justify();
        }

        return true;
    }
    /**
     * 发送下一步消息，清楚之前的消息
     * @param message
     */
    private void setNextMessage(int message) {
        clearMessages();
        animationHandler.sendEmptyMessage(message);
    }

    /**
     * 清楚所有的what的消息列表
     */
    private void clearMessages() {
        animationHandler.removeMessages(MESSAGE_SCROLL);
        animationHandler.removeMessages(MESSAGE_JUSTIFY);
    }


    /**
     * 滚动
     * @param distance 距离
     * @param time     时间
     */
    public void scroll(int distance, int time) {
        scroller.forceFinished(true);
        lastScrollX = 0;
        scroller.startScroll(0, 0, distance, 0, time != 0 ? time : SCROLLING_DURATION);
        setNextMessage(MESSAGE_SCROLL);
        startScrolling();
    }

    /**
     * 动画处理handler
     */
    private Handler animationHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            scroller.computeScrollOffset();
            int currX = scroller.getCurrX();
            int delta = lastScrollX - currX;
            lastScrollX = currX;
            if (delta != 0) {
                listener.onScroll(delta);
            }
            // 滚动是不是完成时，涉及到最终Y，所以手动完成
            if (Math.abs(currX - scroller.getFinalX()) < MIN_DELTA_FOR_SCROLLING) {
                lastScrollX = scroller.getFinalX();
                scroller.forceFinished(true);
            }
            if (!scroller.isFinished()) {
                animationHandler.sendEmptyMessage(msg.what);
            } else if (msg.what == MESSAGE_SCROLL) {
                justify();
            } else {
                finishScrolling();
            }
            return true;
        }
    });


    /**
     * 滚动停止时待校验
     */
    private void justify() {
        listener.onJustify();
        setNextMessage(MESSAGE_JUSTIFY);
    }

    /**
     * 开始滚动
     */
    private void startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true;
            listener.onStarted();
        }
    }

    /**
     * 滚动结束
     */
    void finishScrolling() {
        if (isScrollingPerformed) {
            listener.onFinished();
            isScrollingPerformed = false;
        }
    }

    /**
     *  滚动监听器接口
     */
    public interface ScrollingListener {
        /**
         * 正在滚动中回调
         * @param distance 滚动的距离
         */
        void onScroll(int distance);

        /**
         * 启动滚动时调用的回调函数
         */
        void onStarted();

        /**
         * 校验完成后 执行完毕后回调
         */
        void onFinished();

        /**
         * 滚动停止时待校验
         */
        void onJustify();
    }
}
