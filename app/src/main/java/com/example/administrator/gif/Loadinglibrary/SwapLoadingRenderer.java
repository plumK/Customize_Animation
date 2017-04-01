package com.example.administrator.gif.Loadinglibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;


public class SwapLoadingRenderer extends LoadingRenderer {
    private static final Interpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    private static final long ANIMATION_DURATION = 9500;

    private static final int DEFAULT_CIRCLE_COUNT = 3;

    private static final float DEFAULT_BALL_RADIUS = 7.5f;
    private static final float DEFAULT_WIDTH = 35.0f * 11;
    private static final float DEFAULT_HEIGHT = 35.0f * 5;
    private static final float DEFAULT_STROKE_WIDTH = 1.5f;

    private static final int DEFAULT_COLOR = Color.WHITE;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mColor;

    private int mSwapIndex;
    private int mBallCount;

    private float mBallSideOffsets;
    private float mBallCenterY;
    private float mBallRadius;
    private float mBallInterval;
    private float mSwapBallOffsetX;
    private float mSwapBallOffsetY;
    private float mASwapThreshold;

    private float mStrokeWidth;

    private SwapLoadingRenderer(Context context) {
        super(context);

        init(context);
        adjustParams();
        setupPaint();
    }

    private void init(Context context) {
        mWidth = DensityUtil.dip2px(context, DEFAULT_WIDTH);
        Log.e("mwidth", mWidth + "");
        mHeight = DensityUtil.dip2px(context, DEFAULT_HEIGHT);
        Log.e("mHeight", mHeight + "");
        mBallRadius = DensityUtil.dip2px(context, DEFAULT_BALL_RADIUS);
        mStrokeWidth = DensityUtil.dip2px(context, DEFAULT_STROKE_WIDTH);
        mColor = DEFAULT_COLOR;
        mDuration = ANIMATION_DURATION;
        mBallCount = DEFAULT_CIRCLE_COUNT;
        mBallInterval = mBallRadius;
    }

    private void adjustParams() {
        mBallCenterY = mHeight / 2.0f;
        mBallSideOffsets = (mWidth - mBallRadius * 2 * mBallCount - mBallInterval * (mBallCount - 1)) / 2.0f;
        Log.e("mBallSideOffsetS", mBallSideOffsets + "");

        mASwapThreshold = 1.0f / (mBallCount - 1);
        Log.e("mASwapThreshold", mASwapThreshold + "");
    }

    private void setupPaint() {
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void draw(Canvas canvas) {
        int saveCount = canvas.save();

        for (int i = 0; i < mBallCount; i++) {
            if (i == mSwapIndex) {
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mBallSideOffsets + mBallRadius * (i * 2 + 1) + i * mBallInterval + mSwapBallOffsetX
                        , mBallCenterY - mSwapBallOffsetY,  mBallRadius - mStrokeWidth / 2, mPaint);


            } else if (i == (mSwapIndex + 1) % mBallCount) {
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mBallSideOffsets + mBallRadius * (i * 2 + 1) + i * mBallInterval - mSwapBallOffsetX
                        , mBallCenterY + mSwapBallOffsetY, mBallRadius - mStrokeWidth / 2, mPaint);

            } else {
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mBallSideOffsets + mBallRadius * (i * 2 + 1) + i * mBallInterval, mBallCenterY
                        , mBallRadius - mStrokeWidth / 2, mPaint);
            }
        }

        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void computeRender(float renderProgress) {
        mSwapIndex = (int) (renderProgress / mASwapThreshold);
        Log.e("swapTracesProgrss", mSwapIndex + "");

        // Swap trace : x^2 + y^2 = r ^ 2
        float swapTraceProgress = ACCELERATE_DECELERATE_INTERPOLATOR.getInterpolation(
                (renderProgress - mSwapIndex * mASwapThreshold) / mASwapThreshold);
        Log.e("swapTracesProgrss", swapTraceProgress + "");

        float swapTraceRadius = mSwapIndex == mBallCount - 1
                ? (mBallRadius * 2 * (mBallCount - 1) + mBallInterval * (mBallCount - 1)) / 2
                : (mBallRadius * 2 + mBallInterval) / 2;
        Log.e("swapTracesProgrss", swapTraceRadius + "");


        // Calculate the X offset of the swap ball
        mSwapBallOffsetX = mSwapIndex == mBallCount - 1
                ? -swapTraceProgress * swapTraceRadius * 2
                : swapTraceProgress * swapTraceRadius * 2;

        // if mSwapIndex == mBallCount - 1 then (swapTraceRadius, swapTraceRadius) as the origin of coordinates
        // else (-swapTraceRadius, -swapTraceRadius) as the origin of coordinates
        float xCoordinate = mSwapIndex == mBallCount - 1
                ? mSwapBallOffsetX + swapTraceRadius
                : mSwapBallOffsetX - swapTraceRadius;

        // Calculate the Y offset of the swap ball
        mSwapBallOffsetY = (float) (mSwapIndex % 2 == 0 && mSwapIndex != mBallCount - 1
                ? Math.sqrt(Math.pow(swapTraceRadius, 2.0f) - Math.pow(xCoordinate, 2.0f))
                : Math.sqrt(Math.pow(swapTraceRadius, 2.0f) - Math.pow(xCoordinate, 2.0f)));

    }

    @Override
    protected void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    protected void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    protected void reset() {
    }


}
