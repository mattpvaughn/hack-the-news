package io.github.httpmattpvaughn.hnapp.views;

import android.support.v4.view.GestureDetectorCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

// Base found on stackoverflow somewhere

public abstract class TextViewLinkHandler extends LinkMovementMethod {

    GestureDetectorCompat gestureDetector;

    public boolean onTouchEvent(final TextView widget, final Spannable buffer, final MotionEvent event) {
        if (gestureDetector == null) {
            gestureDetector = new GestureDetectorCompat(widget.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    handleLongClick(event, widget, buffer);
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    handleClick(event, widget, buffer);
                    return super.onSingleTapUp(event);
                }
            });
        }

        gestureDetector.setIsLongpressEnabled(true);
        return gestureDetector.onTouchEvent(event);
    }

    private void handleLongClick(MotionEvent event, TextView widget, Spannable buffer) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
        if (link.length != 0) {
            onLinkLongClick(link[0].getURL());
        } else {
            View parent = (View) widget.getParent();
            parent.callOnClick();
        }
    }

    private void handleClick(MotionEvent event, TextView widget, Spannable buffer) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
        if (link.length != 0) {
            onLinkClick(link[0].getURL());
        } else {
            View parent = (View) widget.getParent();
            parent.callOnClick();
        }
    }

    abstract public void onLinkClick(String url);

    abstract public void onLinkLongClick(String url);

    public interface OnLinkClickListener {
        void onClickLink(TextView textView, String url);
    }

    public interface OnLinkLongClickListener {
        void onLongClickLink(TextView textView, String url);
    }

}