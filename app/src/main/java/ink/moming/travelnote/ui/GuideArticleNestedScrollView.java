package ink.moming.travelnote.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;

/**
 * Created by jml on 2018/2/26.
 */

public class GuideArticleNestedScrollView extends NestedScrollView {
    private Callbacks mCallbacks;
    public GuideArticleNestedScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mCallbacks != null) {
            mCallbacks.onScrollChanged();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int scrollY = getScrollY();
        // hack to call onScrollChanged on screen rotate
        if (scrollY > 0 && mCallbacks != null) {
            mCallbacks.onScrollChanged();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    public void setCallbacks(Callbacks listener) {
        mCallbacks = listener;
    }

    public static interface Callbacks {
        public void onScrollChanged();
    }
}
