package org.bitwa.wallet.base.dialog;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/5/9.
 * Dialog的View处理类
 */

public class DialogViewHelper {
    private View mContentView = null;
    private SparseArray<WeakReference<View>> mViews;

    public DialogViewHelper(Context context, int layoutResId) {
        this();
        mContentView = LayoutInflater.from(context).inflate(layoutResId, null);
    }

    public DialogViewHelper() {
        mViews = new SparseArray<>();
    }

    /**
     * 设置布局
     *
     * @param contentView
     */
    public void setContentView(View contentView) {
        this.mContentView = contentView;
    }

    /**
     * 设置文本
     *
     * @param viewId
     * @param text
     */
    public void setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        if (tv != null) {
            tv.setText(text);
        }

    }

    public <T extends View> T getView(int viewId) {
        WeakReference<View> viewReference = mViews.get(viewId);
        View view = null;
        if (viewReference != null) {
            view = viewReference.get();
        }
        if (view == null) {
            view = mContentView.findViewById(viewId);
            mViews.put(viewId, new WeakReference<>(view));
        }
        return (T) view;
    }

    /**
     * 设置点击事件
     *
     * @param viewId   要设置点击事件的view的id
     * @param listener 设置的点击事件对象
     */
    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = mContentView.findViewById(viewId);//找到mContentView上的这个控件，然后给他设置点击事件
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    public View getContentView() {
        return mContentView;
    }
}