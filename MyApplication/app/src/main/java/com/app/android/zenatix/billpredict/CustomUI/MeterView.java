package com.app.android.zenatix.billpredict.CustomUI;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.app.android.zenatix.billpredict.R;

/**
 * Created by vedantdasswain on 02/03/15.
 */
public class MeterView extends View {
    private Context mContext;
    private int mBackgroundColor,mBackgroundWidth,mPrimaryColor,mPrimaryWidth,mTargetColor,mTargetLength,
            mRegularTextSize,mValueTextSize,mTitleTextSize,mDays=0;
    private Paint mArcPaintBackground,mArcPaintPrimary,mTargetMarkPaint,
            mRegularText,mCurrentText,mPredictText,mTitleText;
    private float mPadding,mProgress=0,mTarget=0;
    private RectF mDrawingRect;
    private static double M_PI_2 = Math.PI/2;
    private String mTitle="TITLE",mUnit="";

    //MeterColours
    private  int lowMeterColour,medMeterColour,highMeterColour;
    //TargetColours
    private  int lowTargetColour,medTargetColour,highTargetColour;
    private float maxScale=100;
    private float monthScale=100;

    public MeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        init();
    }

    private void init() {
        Resources res = mContext.getResources();
        float density = res.getDisplayMetrics().density;

        lowMeterColour=res.getColor(android.R.color.holo_green_light);
        medMeterColour=res.getColor(android.R.color.holo_orange_light);
        highMeterColour=res.getColor(android.R.color.holo_red_light);

        lowTargetColour=res.getColor(android.R.color.holo_green_dark);
        medTargetColour=res.getColor(android.R.color.holo_orange_dark);
        highTargetColour=res.getColor(android.R.color.holo_red_dark);

        mBackgroundColor = res.getColor(R.color.primaryDark);
        mBackgroundWidth = (int)(20 * density); // default to 20dp
        mPrimaryColor = lowMeterColour;
        mPrimaryWidth = (int)(20 * density);  // default to 20dp
        mTargetColor = lowTargetColour;
        mTargetLength = (int)(mPrimaryWidth * 1.25); // 100% longer than arc line width
        mRegularTextSize = (int)(mBackgroundWidth * 0.5); //Double the size of the width;
        mValueTextSize=(int)(mBackgroundWidth * 3);
        mTitleTextSize=(int)(mBackgroundWidth*2);

        mArcPaintBackground = new Paint() {
            {
                setDither(true);
                setStyle(Style.STROKE);
                setStrokeCap(Cap.SQUARE);
                setStrokeJoin(Join.BEVEL);
                setAntiAlias(true);
            }
        };
        mArcPaintBackground.setColor(mBackgroundColor);
        mArcPaintBackground.setStrokeWidth(mBackgroundWidth);

        mArcPaintPrimary = new Paint() {
            {
                setDither(true);
                setStyle(Style.STROKE);
                setStrokeCap(Cap.SQUARE);
                setStrokeJoin(Join.BEVEL);
                setAntiAlias(true);
            }
        };
        mArcPaintPrimary.setColor(mPrimaryColor);
        mArcPaintPrimary.setStrokeWidth(mPrimaryWidth);

        mTargetMarkPaint = new Paint() {
            {
                setDither(true);
                setStyle(Style.STROKE);
                setStrokeCap(Cap.SQUARE);
                setStrokeJoin(Join.BEVEL);
                setAntiAlias(true);
            }
        };
        mTargetMarkPaint.setColor(mTargetColor);
        // make target tick mark 1/3 width of progress(primary) arc width
        mTargetMarkPaint.setStrokeWidth(mPrimaryWidth / 3);

        // get widest drawn element to properly pad the rect we draw inside
        float maxW = (mTargetLength >= mBackgroundWidth) ? mTargetLength : mBackgroundWidth;
        // arc is drawn with it's stroke center at the rect size provided, so we have to pad
        // it by half to bring it inside our bounding rect
        mPadding = maxW / 2;
        mProgress = 0;

        mRegularText=new Paint();
        mRegularText.setColor(res.getColor(android.R.color.black));
        mRegularText.setTextSize(mRegularTextSize);
        mRegularText.setTextAlign(Paint.Align.CENTER);

        Typeface tf = Typeface.create("Droid",Typeface.BOLD);

        mCurrentText=new Paint();
        mCurrentText.setColor(lowMeterColour);
        mCurrentText.setTextSize(mValueTextSize);
        mCurrentText.setTypeface(tf);
        mCurrentText.setTextAlign(Paint.Align.CENTER);

        mPredictText=new Paint();
        mPredictText.setColor(lowTargetColour);
        mPredictText.setTextSize(mValueTextSize);
        mPredictText.setTypeface(tf);
        mPredictText.setTextAlign(Paint.Align.CENTER);

        mTitleText=new Paint();
        mTitleText.setColor(mBackgroundColor);
        mTitleText.setTextSize(mTitleTextSize);
        mTitleText.setTextAlign(Paint.Align.CENTER);
    }

    public void setProgress(float progress) {
        mProgress = progress;

        if(progress>100)
            progress=100f;

        if(progress<33.4)
            mArcPaintPrimary.setColor(lowMeterColour);
        else if(progress<66.7)
            mArcPaintPrimary.setColor(medMeterColour);
        else
            mArcPaintPrimary.setColor(highMeterColour);

        mCurrentText.setColor(mArcPaintPrimary.getColor());
        invalidate();
        requestLayout();
    }

    public void setTarget(float target) {
        mTarget = target;

        if(target<33.4)
            mTargetMarkPaint.setColor(lowTargetColour);
        else if(target<66.7)
            mTargetMarkPaint.setColor(medTargetColour);
        else
            mTargetMarkPaint.setColor(highTargetColour);

        mPredictText.setColor(mTargetMarkPaint.getColor());
        invalidate();
        requestLayout();
    }

    public void setMonthScale(float scale){
        monthScale=scale;
    }

    public void setTitle(String title){
        mTitle=title;
        invalidate();
        requestLayout();
    }

    public void setUnit(String title){
        mUnit=title;
        invalidate();
        requestLayout();
    }

    public void setScale(Float scale){
        maxScale=scale;
        invalidate();
        requestLayout();
    }

    public void setDays(int days){
        mDays=days;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);// bound our drawable arc to stay fully within our canvas
        // full circle (start at 270, the "top")
        canvas.drawArc(mDrawingRect, 270, 360, false, mArcPaintBackground);

        float progress=mProgress;
        if(mProgress>100)
            progress=100f;

        // draw starting at top of circle com the negative (counter-clockwise) direction
        canvas.drawArc(mDrawingRect, 270 ,360*(progress/100f), false, mArcPaintPrimary);

        // draw target mark along, but perpendicular to the arc's line
        float radius = mDrawingRect.width() <= mDrawingRect.height()
                ? mDrawingRect.width()/2 : mDrawingRect.height()/2;

        float target=mTarget;
        if(mTarget>100)
            target=100f;

        // Shift cos/sin by -90 deg (M_PI_2) to put start at 0 (top) and is com radians
        float circleX = mDrawingRect.centerX() + radius *
                (float)Math.sin((Math.PI * 2 * -target/ 100f) + Math.PI);
        float circleY = mDrawingRect.centerY() + radius *
                (float)Math.cos((Math.PI * 2 * -target/100f) + Math.PI);

        float slope = circleX - mDrawingRect.centerX() == 0 ? 999999
                : (circleY - mDrawingRect.centerY())/(circleX - mDrawingRect.centerX());

        float projectedX = (float)((mTargetLength/2.0)/Math.sqrt(1 + Math.pow(slope, 2.0)));
        float projectedY = (float)(((mTargetLength/2.0)*slope)
                /Math.sqrt(1 + Math.pow(slope, 2.0)));

        canvas.drawLine(circleX - projectedX,
                circleY - projectedY,
                circleX + projectedX,
                circleY + projectedY,
                mTargetMarkPaint);

        canvas.drawText("CURRENT CONSUMPTION "+"("+mUnit+")",mDrawingRect.centerX(),mDrawingRect.centerY()-(radius*0.66f),mRegularText);
        canvas.drawText("["+mDays+" Days]",mDrawingRect.centerX(),mDrawingRect.centerY()-(radius*0.75f),mRegularText);
        String valueString = Integer.toString((int) Math.floor ((mProgress/100f)*maxScale));
        canvas.drawText(valueString,mDrawingRect.centerX(),mDrawingRect.centerY()-(radius*0.33f),mCurrentText);

        canvas.drawText("PREDICTED CONSUMPTION "+"("+mUnit+")",mDrawingRect.centerX(),mDrawingRect.centerY()+(radius*0.66f),mRegularText);
        canvas.drawText("[MONTHLY]",mDrawingRect.centerX(),mDrawingRect.centerY()+(radius*0.75f),mRegularText);
        valueString = Integer.toString((int)Math.floor((mTarget/100f)*monthScale));
        canvas.drawText(valueString,mDrawingRect.centerX(),mDrawingRect.centerY()+(radius*0.55f),mPredictText);

        canvas.drawText(mTitle,mDrawingRect.centerX(),mDrawingRect.centerY()+(radius*0.075f),mTitleText);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = 0;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        // set the dimensions
        if (widthWithoutPadding > heigthWithoutPadding) {
            size = heigthWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // bound our drawable arc to stay fully within our canvas
        mDrawingRect = new RectF(mPadding + getPaddingLeft(),
                mPadding + getPaddingTop(),
                w - mPadding - getPaddingRight(),
                h - mPadding - getPaddingBottom());
    }

}
