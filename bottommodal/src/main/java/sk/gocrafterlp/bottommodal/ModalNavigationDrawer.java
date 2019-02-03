package sk.gocrafterlp.bottommodal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

public class ModalNavigationDrawer extends FrameLayout {

    private CoordinatorLayout tmp;
    private View dim;
    private ViewGroup content;

    private ModalDrawerParameters params;
    private ModalActionAdapter actionAdapter;

    private float defaultContentY;

    public ModalNavigationDrawer(@NonNull Context context) {
        super(context);

        init(null);
    }

    public ModalNavigationDrawer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public ModalNavigationDrawer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public ModalNavigationDrawer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        params = ModalDrawerParameters.create();
        create();

        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ModalNavigationDrawer,
                    0, 0);

            try {
                int contentLayout = a.getResourceId(R.styleable.ModalNavigationDrawer_modal_layout, 0);

                if (contentLayout != 0) {
                    View contentView = LayoutInflater.from(getContext()).inflate(contentLayout, null);

                    setContent(contentView);
                }

                params.showing = a.getBoolean(R.styleable.ModalNavigationDrawer_shown, false);
                params.dismissible = a.getBoolean(R.styleable.ModalNavigationDrawer_dismissible, true);

            } finally {
                a.recycle();
            }
        }
    }

    private void create() {
        tmp = (CoordinatorLayout) LayoutInflater.from(getContext()).inflate(R.layout.base_modal_drawer, null);
        dim = tmp.getChildAt(0);
        content = (ViewGroup) tmp.getChildAt(1);

        removeAllViews();
        addView(tmp);

        content.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        content.setClipToOutline(true);

        /*dim.setOnClickListener(v -> {
            if (!params.animating && params.showing && params.dismissible) {
                hide();
            }
        });*/
        dim.setOnTouchListener(new OnTouchListener() {

            float dY;
            float y;
            boolean moved;

            private ModalActionAdapter.SwipeState state;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (defaultContentY == 0)
                    return true;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dY = content.getY() - event.getRawY();
                        moved = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        y = event.getRawY() + dY;
                        content.setY(Math.max(y, defaultContentY));
                        moved = true;
                        calculateState();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!moved || (y - defaultContentY <= params.pressDeflectionTolerance && y - defaultContentY >= -params.pressDeflectionTolerance)) {
                            if (!params.animating && params.showing && params.dismissible) {

                                if (actionAdapter != null)
                                    actionAdapter.onUserDismiss(ModalNavigationDrawer.this);
                                hide();

                            } else if (!params.animating) animateBack();
                        } else if (y - defaultContentY > (long) NumbersUtil.fractionDouble(content.getMeasuredHeight(), params.getDismissPoint())) {
                            if (!params.animating && params.showing && params.dismissible) {

                                if (actionAdapter != null) actionAdapter.onUserDismiss(ModalNavigationDrawer.this);
                                hide();

                            } else if (!params.animating) animateBack();
                        } else
                            animateBack();
                        break;
                    default:
                        return false;
                }
                return true;
            }

            private void calculateState() {
                ModalActionAdapter.SwipeState swipeState = null;

                if (!moved || (y - defaultContentY <= params.pressDeflectionTolerance && y - defaultContentY >= -params.pressDeflectionTolerance)) {
                    if (!params.animating && params.showing && params.dismissible) swipeState = ModalActionAdapter.SwipeState.DISMISS;
                    else if (!params.animating) swipeState = ModalActionAdapter.SwipeState.RETURN;
                } else if (y - defaultContentY > (long) NumbersUtil.fractionDouble(content.getMeasuredHeight(), params.getDismissPoint())) {
                    if (!params.animating && params.showing && params.dismissible) swipeState = ModalActionAdapter.SwipeState.DISMISS;
                    else if (!params.animating) swipeState = ModalActionAdapter.SwipeState.RETURN;
                } else
                    swipeState = ModalActionAdapter.SwipeState.RETURN;

                if (swipeState != state) {
                    state = swipeState;
                    if (actionAdapter != null) actionAdapter.onSwipeStateChange(ModalNavigationDrawer.this, swipeState);
                }
            }
        });
        content.post(new Runnable() {
            @Override
            public void run() {
                defaultContentY = content.getY();
            }
        });

        setVisibility(params.showing ? VISIBLE : INVISIBLE);
    }

    private void animateBack() {
        float bb = content.getTranslationY();
        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                content.setTranslationY(NumbersUtil.animate(bb, 0f, interpolatedTime));

                if (interpolatedTime == 1) {
                    params.animating = false;
                }
            }
        };
        a.setDuration(params.animationDurationLong);
        a.setInterpolator(new DecelerateInterpolator());
        params.animating = true;
        startAnimation(a);
    }

    public void hide() {
        setVisibility(VISIBLE);

        dim.setAlpha(1);
        float bb = content.getTranslationY();

        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                content.setTranslationY(NumbersUtil.animate(bb, content.getMeasuredHeight(), interpolatedTime));

                if (interpolatedTime == 1) {

                    Animation a = new Animation() {

                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            dim.setAlpha(NumbersUtil.animate(1f, 0f, interpolatedTime));

                            if (interpolatedTime == 1) {
                                setVisibility(INVISIBLE);
                                params.animating = false;
                                params.showing = false;

                                if (actionAdapter != null)
                                    actionAdapter.onHide(ModalNavigationDrawer.this);
                            }
                        }
                    };
                    a.setDuration(params.animationDurationShort);
                    a.setInterpolator(new AccelerateDecelerateInterpolator());
                    dim.startAnimation(a);
                }
            }
        };
        a.setDuration(params.animationDurationLong);
        a.setInterpolator(new DecelerateInterpolator());
        params.animating = true;
        startAnimation(a);
    }

    public void show() {
        setVisibility(VISIBLE);

        dim.setAlpha(0);
        content.setTranslationY(999);

        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                dim.setAlpha(NumbersUtil.animate(0f, 1f, interpolatedTime));

                if (interpolatedTime == 1) {
                    Animation a = new Animation() {

                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            content.setTranslationY(NumbersUtil.animate(content.getMeasuredHeight(), 0, interpolatedTime));

                            if (interpolatedTime == 1) {
                                params.animating = false;
                                params.showing = true;

                                if (actionAdapter != null)
                                    actionAdapter.onShow(ModalNavigationDrawer.this);
                            }
                        }
                    };
                    a.setDuration(params.animationDurationLong);
                    a.setInterpolator(new DecelerateInterpolator());
                    startAnimation(a);
                }
            }
        };
        a.setDuration(params.animationDurationShort);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        params.animating = true;
        dim.startAnimation(a);
    }

    public boolean isAnimating() {
        return params.animating;
    }

    public boolean isShowing() {
        return params.showing;
    }

    public boolean isDismissible() {
        return params.dismissible;
    }

    public void setContent(View view) {
        content.removeAllViews();
        content.addView(view);
    }

    public void setParams(ModalDrawerParameters params) {
        if (params != null) {
            this.params.inheritFrom(params);
        } else
            this.params.clear();
    }

    public ModalNavigationDrawer setActionAdapter(ModalActionAdapter actionAdapter) {
        this.actionAdapter = actionAdapter;
        return this;
    }

    public void releaseToDecor(Activity activity) {
        releaseToDecor(activity, false);
    }

    public void releaseToDecor(Activity activity, boolean fullscreen) {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }

        if (fullscreen) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View content = decorView.getChildAt(0);
            if (content instanceof ViewGroup) {
                ((ViewGroup) content).addView(this);
            } else
                decorView.addView(this);
        } else {
            View content = activity.findViewById(android.R.id.content);
            if (content instanceof ViewGroup) {
                ((ViewGroup) content).addView(this);
            } else
                releaseToDecor(activity, true);
        }
    }

    @ColorInt
    public int getDim() {
        return getProtected(dim, new ProtectedGetter<View, Integer>() {
            @Override
            public Integer getInternal(ModalNavigationDrawer a, View object) {
                if (object.getBackground() instanceof ColorDrawable)
                    return ((ColorDrawable) object.getBackground()).getColor();
                else
                    return null;
            }
        });
    }

    public void setDim(@ColorInt int color) {
        doProtected(dim, (a, object) -> object.setBackgroundColor(color));
    }

    public void setContentBackgroundColor(@ColorInt int color) {
        doProtected(content, (a, object) -> object.setBackgroundColor(color));
    }

    public void setContentBackgroundTint(ColorStateList stateList) {
        doProtected(content, (a, object) -> object.setBackgroundTintList(stateList));
    }

    public Drawable getContentBackground() {
        return getProtected(content, (a, object) -> object.getBackground());
    }

    public void setContentBackground(Drawable drawable) {
        doProtected(content, (a, object) -> object.setBackground(drawable));
    }

    public void setContentBackground(Bitmap bitmap) {
        doProtected(content, (a, object) -> object.setBackground(new BitmapDrawable(getResources(), bitmap)));
    }

    public void setContentBackground(@DrawableRes int drawableRes) {
        doProtected(content, (a, object) -> object.setBackgroundResource(drawableRes));
    }

    public <T extends View> void setupLayout(LayoutManager<T> manager) {
        doProtected(content, (a, content) -> manager.onCreate((T) content.getChildAt(0)));
    }

    public <T extends View> void findViewById(int id, LayoutManager<T> manager) {
        doProtected(content, (a, content) -> manager.onCreate((T) content.getChildAt(0).findViewById(id)));
    }

    public void findViewsByIds(LayoutArrayManager manager, int... ids) {
        doProtected(content, (a, content) -> {
            View[] arr = new View[ids.length];
            for (int i = 0; i < ids.length; i++)
                arr[i] = content.getChildAt(0).findViewById(ids[i]);

            manager.sendViews(arr);
        });
    }

    public void findViewsByIds(int[] ids, LayoutArrayManager manager) {
        findViewsByIds(manager, ids);
    }

    protected <T> void doProtected(T focus, ProtectedOperation<T> operation) {
        if (focus != null && Looper.myLooper() == Looper.getMainLooper())
            operation.doInternal(this, focus);
        else
            post(() -> operation.doInternal(this, focus));
    }

    protected <T, U> U getProtected(T focus, ProtectedGetter<T, U> operation) {
        if (focus != null && Looper.myLooper() == Looper.getMainLooper())
            return operation.getInternal(this, focus);
        else {
            ThreadTransport<U> threadTransport = new ThreadTransport<>();
            post(() -> threadTransport.setObject(operation.getInternal(this, focus)));

            return threadTransport.getObject();
        }
    }

    protected interface ProtectedOperation<Object> {
        void doInternal(ModalNavigationDrawer a, Object object);
    }

    protected interface ProtectedGetter<Object, Result> {
        Result getInternal(ModalNavigationDrawer a, Object object);
    }

    public interface LayoutManager<T extends View> {
        void onCreate(T layout);
    }

    public static abstract class LayoutArrayManager {

        private View[] views;

        public final void sendViews(View[] views) {
            this.views = views;
            onCreate(views);
        }

        protected abstract void onCreate(View[] views);

        protected final void ensureViewsLength(int len) {
            if (views.length != len)
                throw new IllegalStateException("Unexpected views array size");
        }

        @SuppressWarnings("unchecked")
        protected final <T extends View> T getView(int pos) {
            try {
                T t = (T) views[pos];
                if (t != null)
                    return t;
                else
                    throw new IllegalStateException("View not found at pos " + pos);
            } catch (ArrayIndexOutOfBoundsException | ClassCastException e) {
                throw new IllegalStateException("Unexpected view at pos " + pos, e);
            }
        }
    }

    private class ThreadTransport<T> {
        private final Object lock = new Object();
        private T object;
        private boolean received;

        public T getObject() {
            if (received) return object;

            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return object;
            }
        }

        public ThreadTransport setObject(T object) {
            this.object = object;
            this.received = true;
            synchronized (lock) {
                lock.notifyAll();
            }
            return this;
        }

        public void clear() {
            object = null;
            received = false;
        }
    }
}
