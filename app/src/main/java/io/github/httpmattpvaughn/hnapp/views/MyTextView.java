package io.github.httpmattpvaughn.hnapp.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class MyTextView extends android.support.v7.widget.AppCompatTextView {

    private boolean linksActive = true;
    private OnLinkClickListener linkClickListener = null;

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnLinkClickListener(OnLinkClickListener listener) {
        this.linkClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (linksActive) {
            Layout layout = this.getLayout();
            if (layout != null) {
                int line = layout.getLineForVertical((int) event.getY());
                int offset = layout.getOffsetForHorizontal(line, event.getX());

                if (getText() != null && getText() instanceof Spanned) {
                    Spanned spanned = (Spanned) getText();

                    ClickableSpan[] links = spanned.getSpans(offset, offset, ClickableSpan.class);

                    if (links.length > 0) {
                        System.out.println("IN HERE");
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            return true;
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            System.out.println("BADA BING");
                            links[0].onClick(this);
                            return false;
                        } else {
                            System.out.println("OOF");
                            return super.onTouchEvent(event);
                        }
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    public interface OnLinkClickListener {
        void onClickLink(TextView textView, String url);
    }
}