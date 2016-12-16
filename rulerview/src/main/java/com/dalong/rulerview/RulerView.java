package com.dalong.rulerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 刻度尺
 * Created by dalong on 2016/12/15.
 */

public class RulerView extends View {
    private  static final String TAG="RulerView";
    // 默认刻度模式
    public static final int MOD_TYPE_SCALE = 5;
    //刻度基数  每个刻度代表多少 默认为1
    public int mScaleBase=1;
    //最大刻度的颜色
    public int mMaxScaleColor;
    //中间刻度的颜色
    public int mMidScaleColor;
    //最小刻度的颜色
    public int mMinScaleColor;
    //底部线的颜色
    public int mBottomLineColor;
    //最大刻度的宽度
    public float mMaxScaleWidth;
    //中间刻度的宽度
    public float mMidScaleWidth;
    //最小刻度的宽度
    public float mMinScaleWidth;
    //底线的宽度
    public float mBottomLineWidth;
    //最大刻度的高度占控件的高度比例
    public float mMaxScaleHeightRatio;
    //中间刻度的高度占控件的高度比例
    public float mMidScaleHeightRatio;
    //最小刻度的高度占控件的高度比例
    public float mMinScaleHeightRatio;
    //是否显示刻度值
    public boolean isShowScaleValue;
    //是否刻度渐变
    public boolean isScaleGradient;
    //刻度值颜色
    public int  mScaleValueColor;
    //刻度值文字大小
    public float  mScaleValueSize;
    //当前值
    public int mCurrentValue;
    //最大值
    public int mMaxValue;
    //最小值
    public int mMinValue;
    //中间图片
    private Bitmap mMiddleImg;
    //刻度线画笔
    private  Paint mScalePaint;
    // 刻度值画笔
    private  TextPaint mScaleValuePaint;
    //中间图片画笔
    private  Paint mMiddleImgPaint;
    private  float mTpDesiredWidth;
    //最大刻度高度
    private int mMaxScaleHeight;
    //中间刻度高度
    private int mMidScaleHeight;
    //最小刻度高度
    private int mMinScaleHeight;
    // 滚动偏移量
    private int scrollingOffset;
    //间隔S
    private int mScaleSpace=20;
    // 滚动器
    private RulerViewScroller scroller;
    // 是否执行滚动
    private boolean isScrollingPerformed;

    public RulerView(Context context) {
        this(context,null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.RulerView);

        mMaxScaleColor=typedArray.getColor(R.styleable.RulerView_mMaxScaleColor, Color.BLACK);
        mMidScaleColor=typedArray.getColor(R.styleable.RulerView_mMidScaleColor, Color.BLACK);
        mMinScaleColor=typedArray.getColor(R.styleable.RulerView_mMinScaleColor, Color.BLACK);
        mMaxScaleColor=typedArray.getColor(R.styleable.RulerView_mMaxScaleColor, Color.BLACK);
        mScaleValueColor=typedArray.getColor(R.styleable.RulerView_mScaleValueColor, Color.BLACK);
        mBottomLineColor=typedArray.getColor(R.styleable.RulerView_mBottomLineColor, Color.BLACK);

        mMaxScaleWidth = typedArray.getDimensionPixelSize(R.styleable.RulerView_mMaxScaleWidth, 15);
        mMidScaleWidth = typedArray.getDimensionPixelSize(R.styleable.RulerView_mMidScaleWidth, 12);
        mMinScaleWidth = typedArray.getDimensionPixelSize(R.styleable.RulerView_mMinScaleWidth, 10);
        mBottomLineWidth = typedArray.getDimensionPixelSize(R.styleable.RulerView_mBottomLineWidth, 15);
        mScaleValueSize = typedArray.getDimensionPixelSize(R.styleable.RulerView_mScaleValueSize, 12);
        mScaleSpace = typedArray.getDimensionPixelSize(R.styleable.RulerView_mScaleSpace, 20);

        mMaxScaleHeightRatio = typedArray.getFloat(R.styleable.RulerView_mMaxScaleHeightRatio, 0.3f);
        mMidScaleHeightRatio = typedArray.getFloat(R.styleable.RulerView_mMidScaleHeightRatio, 0.2f);
        mMinScaleHeightRatio = typedArray.getFloat(R.styleable.RulerView_mMinScaleHeightRatio, 0.1f);

        isShowScaleValue = typedArray.getBoolean(R.styleable.RulerView_isShowScaleValue, true);
        isScaleGradient = typedArray.getBoolean(R.styleable.RulerView_isScaleGradient, true);

        mMaxValue = typedArray.getInteger(R.styleable.RulerView_mMaxValue, 100);
        mMinValue = typedArray.getInteger(R.styleable.RulerView_mMinValue, 0);
        mScaleBase = typedArray.getInteger(R.styleable.RulerView_mScaleBase, 1);
        mCurrentValue = typedArray.getInteger(R.styleable.RulerView_mCurrentValue, 0);
        setCurrentValue(mCurrentValue);

        mMiddleImg = BitmapFactory.decodeResource(getResources(),
                typedArray.getResourceId(R.styleable.RulerView_mMiddleImg,R.drawable.ruler_mid_arraw));
        typedArray.recycle();

        mScalePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mScalePaint.setStyle(Paint.Style.STROKE);
        mScalePaint.setAntiAlias(true);

        mScaleValuePaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mScaleValuePaint.setColor(mScaleValueColor);
        mScaleValuePaint.setTextSize(mScaleValueSize);
        mScaleValuePaint.setTextAlign(Paint.Align.CENTER);
        mTpDesiredWidth = Layout.getDesiredWidth("0", mScaleValuePaint);

        mMiddleImgPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mMiddleImgPaint.setStyle(Paint.Style.STROKE);
        mMiddleImgPaint.setAntiAlias(true);

        scroller=new RulerViewScroller(context,scrollingListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidthSize(widthMeasureSpec),measureHeightSize(heightMeasureSpec));
    }

    private int measureHeightSize(int heightMeasureSpec) {
        int result;
        int mode=MeasureSpec.getMode(heightMeasureSpec);
        int size=MeasureSpec.getSize(heightMeasureSpec);
        if(mode==MeasureSpec.EXACTLY){
            result=size;
        }else{
            result=(int) (mMiddleImg.getHeight() + getPaddingTop() + getPaddingBottom() + 2 * mScaleValuePaint.getTextSize());
            if(mode==MeasureSpec.AT_MOST){
                result=Math.min(result,size);
            }
        }
        return  result;
    }

    private int measureWidthSize(int widthMeasureSpec) {
        int result;
        int mode=MeasureSpec.getMode(widthMeasureSpec);
        int size=MeasureSpec.getSize(widthMeasureSpec);
        if(mode==MeasureSpec.EXACTLY){
            result=size;
        }else{
            result=400;
            if(mode==MeasureSpec.AT_MOST){
                result=Math.min(result,size);
            }
        }
        return  result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == 0 || h == 0)
            return;
        /**
         * 在这里根据控件高度设置三中刻度线的高度
         */
        int mHeight = h - getPaddingTop() - getPaddingBottom();
        mMaxScaleHeight = (int) (mHeight*mMaxScaleHeightRatio);
        mMidScaleHeight = (int) (mHeight*mMidScaleHeightRatio);
        mMinScaleHeight = (int) (mHeight*mMinScaleHeightRatio);
    }

    /**
     * 绘制图像
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int mDrawWidth=getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
        int mDrawHeight=getMeasuredHeight()-getPaddingTop()-getPaddingBottom();

        //绘制刻度线
        drawScaleLine(canvas,mDrawWidth,mDrawHeight);
        //绘制中间图片
        drawMiddleImg(canvas,mDrawWidth,mDrawHeight);
    }

    /**
     * 绘制中间图片
     * @param canvas
     * @param mDrawWidth
     * @param mDrawHeight
     */
    private void drawMiddleImg(Canvas canvas, int mDrawWidth, int mDrawHeight) {
        int left = (mDrawWidth - mMiddleImg.getWidth()) / 2;
        int top = (int) (mScaleValuePaint.getTextSize() / 2);
        canvas.drawBitmap(mMiddleImg, left, top, mMiddleImgPaint);
    }

    /**
     * 绘制刻度线
     * @param canvas
     * @param mDrawWidth
     * @param mDrawHeight
     */
    private void drawScaleLine(Canvas canvas, int mDrawWidth, int mDrawHeight) {
        int scaleNum= (int) (Math.ceil(mDrawWidth/2f/mScaleSpace))+2;
        int distanceX = scrollingOffset;
        int currValue = mCurrentValue;
        drawScaleLine(canvas,scaleNum,distanceX,currValue,mDrawWidth,mDrawHeight);
    }

    /**
     * 绘制刻度线
     * @param canvas
     * @param scaleNum
     * @param distanceX
     * @param currValue
     * @param mDrawWidth
     * @param mDrawHeight
     */
    private void drawScaleLine(Canvas canvas, int scaleNum, int distanceX, int currValue, int mDrawWidth, int mDrawHeight) {
        int dy = (int) (mDrawHeight - mTpDesiredWidth - mScaleValuePaint.getTextSize()) - getPaddingBottom();
        int value;
        float xPosition;
        for (int i=0;i<scaleNum;i++){
            // 右面
            xPosition=mDrawWidth/2f+i*mScaleSpace+distanceX;
            value=currValue+i;
            if(xPosition<=mDrawWidth && value>=(mMinValue/mScaleBase)&&value<=(mMaxValue/mScaleBase)){
                drawScaleLine(canvas, value,  xPosition, dy, scaleNum, i, mDrawHeight);
            }
            //绘制右面线
            if(value<(mMaxValue/mScaleBase)&&value>=(mMinValue/mScaleBase))
                drawBottomLine(canvas,getAlpha(scaleNum, i),xPosition-mMaxScaleWidth/2, dy, xPosition+mScaleSpace+mMaxScaleWidth/2, dy);

            //左面
            xPosition=mDrawWidth/2f-i*mScaleSpace+distanceX;
            value=currValue-i;
            if(xPosition>getPaddingLeft() && value>=(mMinValue/mScaleBase)&&value<=(mMaxValue/mScaleBase)){
                drawScaleLine( canvas, value,  xPosition, dy, scaleNum, i, mDrawHeight);
            }
            //绘制左面线
            if(value>=(mMinValue/mScaleBase) && value<(mMaxValue/mScaleBase))
                drawBottomLine(canvas,getAlpha(scaleNum, i),xPosition-mMaxScaleWidth/2, dy, xPosition+mScaleSpace+mMaxScaleWidth/2, dy);
        }
    }

    /**
     * 绘制底部线
     * @param canvas
     * @param alpha
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     */
    private void drawBottomLine(Canvas canvas,int alpha,float sx,float sy,float ex,float ey){
        mScalePaint.setColor(mBottomLineColor);
        mScalePaint.setStrokeWidth(mBottomLineWidth);
        mScalePaint.setAlpha(alpha);
        canvas.drawLine(sx, sy, ex, ey, mScalePaint);
    }
    /**
     * 绘制刻度尺  左  右
     * @param canvas
     * @param value
     * @param xPosition
     * @param dy
     * @param scaleNum
     * @param i
     * @param mDrawHeight
     */
    public void drawScaleLine(Canvas canvas,int value, float xPosition,int dy,int scaleNum,int i,int mDrawHeight){
        if (value % MOD_TYPE_SCALE == 0) {
            if(value % (MOD_TYPE_SCALE*2)==0){//大刻度
                drawScaleLine(canvas,mMaxScaleWidth,mMaxScaleColor,getAlpha(scaleNum, i),
                        xPosition,dy,xPosition,dy - mMaxScaleHeight);
                if (isShowScaleValue) {
                    mScaleValuePaint.setAlpha(getAlpha(scaleNum, i));
                    canvas.drawText(String.valueOf(value*mScaleBase), xPosition, mDrawHeight - mTpDesiredWidth, mScaleValuePaint);
                }
            }else{//中刻度
                drawScaleLine(canvas,mMidScaleWidth,mMidScaleColor,getAlpha(scaleNum, i),
                        xPosition,dy,xPosition,dy-mMidScaleHeight);
            }
        }else{// 小刻度
            drawScaleLine(canvas,mMinScaleWidth,mMinScaleColor,getAlpha(scaleNum, i),
                    xPosition,dy,xPosition,dy-mMinScaleHeight);
        }

    }
    /**
     * 绘制刻度尺刻度
     * @param canvas
     * @param strokeWidth
     * @param scaleColor
     * @param alpha
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     */
    private void drawScaleLine(Canvas canvas,float strokeWidth,int scaleColor,int alpha,float sx,float sy,float ex,float ey){
        mScalePaint.setStrokeWidth(strokeWidth);
        mScalePaint.setColor(scaleColor);
        mScalePaint.setAlpha(alpha);
        canvas.drawLine(sx, sy, ex, ey, mScalePaint);
    }



    /**
     * 获取透明度  通过当前index占总共数量的count的比例来设置透明度
     * @param halfCount
     * @param index
     * @return
     */
    private int getAlpha(int halfCount, int index) {
        if (isScaleGradient) {
            int MAX_ALPHA_VALUE = 255;
            return MAX_ALPHA_VALUE / halfCount * (halfCount - index);
        } else {
            return 255;
        }
    }


    /**
     * 设置最小值
     *
     * @param mMinValue
     */
    public void setMinValue(int mMinValue) {
        if (mMinValue / mScaleBase < 1) {
            mMinValue = 0;
        }
        this.mMinValue = mMinValue;
        setCurrentValue(mCurrentValue);
        invalidate();
    }

    /**
     * 设置最大值
     * @param mMaxValue
     */
    public void setMaxValue(int mMaxValue) {
        if (mMaxValue > 0) {
            this.mMaxValue = mMaxValue;
            setCurrentValue(mCurrentValue);
        }
        invalidate();
    }


    /**
     * 获取当前值
     *
     * @return
     */
    public int getCurrentValue() {
        return Math.min(Math.max(0, mCurrentValue*mScaleBase), mMaxValue);
    }

    /**
     * 设置当前值
     * @param currentValue
     */
    public void setCurrentValue(int currentValue) {
        if (currentValue < 0) {
            currentValue = 0;
        }
        if(currentValue<=mMinValue){
            currentValue=mMinValue;
        }
        if(currentValue>=mMaxValue){
            currentValue=mMaxValue;
        }
        this.mCurrentValue=currentValue/mScaleBase;
        invalidate();
    }
    /***********************************************以上是绘制部分**********************************************/


    /**
     * 滚动回调接口
     */
    RulerViewScroller.ScrollingListener scrollingListener = new RulerViewScroller.ScrollingListener() {

        /**
         * 滚动开始
         */
        @Override
        public void onStarted() {
            isScrollingPerformed = true;
            //滚动开始
            if (null != onWheelListener) {
                onWheelListener.onScrollingStarted(RulerView.this);
            }
        }

        /**
         * 滚动中
         * @param distance 滚动的距离
         */
        @Override
        public void onScroll(int distance) {
            doScroll(distance);
        }

        /**
         * 滚动结束
         */
        @Override
        public void onFinished() {
            if (outOfRange()) {
                return;
            }
            if (isScrollingPerformed) {
                //滚动结束
                if (null != onWheelListener) {
                    onWheelListener.onScrollingFinished(RulerView.this);
                }
                isScrollingPerformed = false;
            }
            scrollingOffset = 0;
            invalidate();
        }

        /**
         * 验证滚动是否在正确位置
         */
        @Override
        public void onJustify() {
            if (outOfRange()) {
                return;
            }
            if (Math.abs(scrollingOffset) > RulerViewScroller.MIN_DELTA_FOR_SCROLLING) {
                if (scrollingOffset < -mScaleSpace / 2) {
                    scroller.scroll(mScaleSpace + scrollingOffset, 0);
                } else if (scrollingOffset > mScaleSpace / 2) {
                    scroller.scroll(scrollingOffset - mScaleSpace, 0);
                } else {
                    scroller.scroll(scrollingOffset, 0);
                }
            }
        }
    };



    /**
     * 超出左右范围
     * @return
     */
    private boolean outOfRange() {
        //这个是越界后需要回滚的大小值
        int outRange = 0;
        if (mCurrentValue < mMinValue/mScaleBase) {
            outRange = (mCurrentValue - mMinValue/mScaleBase) * mScaleSpace;
        } else if (mCurrentValue > mMaxValue/mScaleBase) {
            outRange = (mCurrentValue - mMaxValue/mScaleBase) * mScaleSpace;
        }
        if (0 != outRange) {
            scrollingOffset = 0;
            scroller.scroll(-outRange, 100);
            return true;
        }
        return false;
    }

    /**
     * 滚动中回调最新值
     * @param delta
     */
    private void doScroll(int delta) {
        scrollingOffset += delta;
        int offsetCount = scrollingOffset / mScaleSpace;
        if (0 != offsetCount) {
            // 显示在范围内
            int oldValueIndex = Math.min(Math.max(mMinValue, mCurrentValue*mScaleBase), mMaxValue);
            mCurrentValue -= offsetCount;
            scrollingOffset -= offsetCount * mScaleSpace;
            if (null != onWheelListener) {
                //回调通知最新的值
                int valueIndex = Math.min(Math.max(mMinValue, mCurrentValue*mScaleBase), mMaxValue);
                onWheelListener.onChanged(this, oldValueIndex + "",valueIndex+"");
            }
        }
        invalidate();
    }

    private float mDownFocusX;
    private float mDownFocusY;
    private boolean isDisallowIntercept;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownFocusX = event.getX();
                mDownFocusY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDisallowIntercept && Math.abs(event.getY() - mDownFocusY) < Math.abs(event.getX() - mDownFocusX)) {
                    isDisallowIntercept = true;
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                isDisallowIntercept = false;
                break;
        }
        return scroller.onTouchEvent(event);
    }

    private OnRulerViewScrollListener onWheelListener;

    /**
     * 添加滚动回调
     * @param listener the listener
     */
    public void setScrollingListener(OnRulerViewScrollListener listener) {
        onWheelListener = listener;
    }


    public interface OnRulerViewScrollListener<T> {
        /**
         * 当更改选择的时候回调方法
         * @param rulerView 状态更改的view
         * @param oldValue  当前item的旧值
         * @param newValue  当前item的新值
         */
        void onChanged(RulerView rulerView, T oldValue, T newValue);

        /**
         * 滚动启动时调用的回调方法
         * @param rulerView
         */
        void onScrollingStarted(RulerView rulerView);

        /**
         * 滚动结束时调用的回调方法
         * @param rulerView
         */
        void onScrollingFinished(RulerView rulerView);
    }























}
