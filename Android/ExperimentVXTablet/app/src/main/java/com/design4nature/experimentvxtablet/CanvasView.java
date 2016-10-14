package com.design4nature.experimentvxtablet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
/**
 * Created by Ruben on 14-10-2016.
 */
public class CanvasView extends View {

        private Bitmap mBitmap;
        Context context;
        private Paint circlePaint;

        public CanvasView(Context c) {
            super(c);
            context=c;
            circlePaint = new Paint();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
//
//            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
//            canvas.drawPath( mPath,  mPaint);
//            canvas.drawPath( circlePath,  circlePaint);
        }

}
