package sk.gocrafterlp.bottommodal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

public class ClippingLinearLayout extends LinearLayout {

    private float[] radii;

    public ClippingLinearLayout(Context context) {
        super(context);
        loadCorners();
    }

    public ClippingLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadCorners();
    }

    public ClippingLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadCorners();
    }

    public ClippingLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        loadCorners();
    }

    Path clipPath;
    Rect rect;
    RectF rectF;

    private void loadCorners() {
        if (getBackground() instanceof GradientDrawable) {
            GradientDrawable d = (GradientDrawable) getBackground();
            try {
                Field mGradientState = d.getClass().getDeclaredField("mGradientState");
                mGradientState.setAccessible(true);

                Object o = mGradientState.get(d);
                Field mRadiusArray = o.getClass().getDeclaredField("mRadiusArray");

                radii = (float[]) mRadiusArray.get(o);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            clipPath = new Path();
            rect = new Rect();
            rectF = new RectF();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (clipPath != null && radii != null) {
            canvas.getClipBounds(rect);
            rectF.set(rect);
            clipPath.addRoundRect(rectF, radii, Path.Direction.CW);

            canvas.clipPath(clipPath);
        }
        super.onDraw(canvas);
    }
}
