package com.avg.dezerboard.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import pm.me.deezerboard.R;

/**
 * Created by abby on 23/04/15.
 */
public class TrackBarView extends View{

    Context context;

    public TrackBarView(Context context) {
        super(context);
        this.context  = context;
    }

    public TrackBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context  = context;
    }

    public TrackBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context  = context;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
//        canvas.drawRect(new Rect(0, 0, width, height), new Paint());
//        canvas.drawArc(new RectF(0, 0, width, height), 0.5f, 0.5f, true, new Paint() );

        float barWidth = this.context.getResources().getDimension(R.dimen.track_bar_dimen);
        int num  = (int) (width/barWidth);
        int x = 0;


        for(int i=0; i<num; i++){
//            int s =
            int h = height / 2  * (i%2 + 1);
            canvas.drawRect(new Rect((int) (x + i*barWidth), h/2, (int) (x + i*barWidth +barWidth), h/2 +h), new Paint());
        }

        //canvas.drawCircle(width/2, height/2 , width/2, new Paint());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
