package com.example.administrator.hotelservice.setting_image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by boss on 11/8/2017 AD.
 */

@SuppressLint("AppCompatCustomView")
public class Customimage extends ImageView {
    private float radius = 15.0f;
    private Path path;
    private RectF rect;

    public Customimage(Context context) {
        super(context);
        init();
    }

    public Customimage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Customimage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        path = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        path.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
