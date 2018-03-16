package io.github.httpmattpvaughn.hnapp.details;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oissela.software.multilevelexpindlistview.MultiLevelExpIndListAdapter;

import io.github.httpmattpvaughn.hnapp.R;
import io.github.httpmattpvaughn.hnapp.Util;
import io.github.httpmattpvaughn.hnapp.data.model.Story;
import io.github.httpmattpvaughn.hnapp.views.MyTextView;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class CommentAdapter extends MultiLevelExpIndListAdapter {

    private final View.OnClickListener onClickListener;
    private final BetterLinkMovementMethod.OnLinkLongClickListener onLinkLongClickListener;
    private int[] colorDepthArr = new int[]{
            0xFFFFEB3B,
            0xFFFFC107,
            0xFFFF9800,
            0xFFFF5722,
            0xFFF44336,
            0xFF673AB7,
            0xFF3F51B5
    };
    private MyTextView.OnLinkClickListener onLinkClickListener;

    public CommentAdapter(MyTextView.OnLinkClickListener onLinkClickListener,
                          BetterLinkMovementMethod.OnLinkLongClickListener onLinkLongClickListener,
                          View.OnClickListener onClickListener) {
        this.onLinkClickListener = onLinkClickListener;
        this.onLinkLongClickListener = onLinkLongClickListener;
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View storyItem = inflater.inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(storyItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).setComment((Story) getItemAt(position));
    }

    public void addComment(Story comment, Story parent) {
        if (comment.parent == 0) {
            System.out.println("adding root");
            add(comment);
        } else {
            System.out.println("adding child");
            int parentPosition = indexOf(parent);
            System.out.println("parentPosition is " + parentPosition);
            parent.addChild(comment);
            insert(parentPosition + 1, comment);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView collapsedCommentCounter;
        public View root;
        public TextView author;
        public TextView time;
        public TextView text;
        public View depthMarker;
        public Story story;

        public ViewHolder(View itemView) {
            super(itemView);
            this.root = itemView.findViewById(R.id.comment_item);
            this.author = itemView.findViewById(R.id.author);
            this.time = itemView.findViewById(R.id.time);
            this.text = itemView.findViewById(R.id.text);
            this.depthMarker = itemView.findViewById(R.id.depth_marker);
            this.collapsedCommentCounter = itemView.findViewById(R.id.collapsed_comment_counter);
        }

        // Do it this way so we have an easy way to handle different types
        public void setComment(final Story comment) {
            long startTime = System.currentTimeMillis();
            this.story = comment;
            if (comment.by != null) {
                this.author.setText(comment.by);
            } else {
                this.author.setText("deleted");
            }
            this.time.setText(Util.beautifyPostAge(comment.time, System.currentTimeMillis() / 1000L));
            if (comment.text != null) {
                this.text.setText(Util.stringToHtml(comment.text, this.text, onLinkClickListener));
            } else {
                this.text.setText("[Deleted]");
            }

            int indentSize = (int) this.root.getContext().getResources().getDimension(R.dimen.depth_marker_width);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) this.root.getLayoutParams();
            if (comment.depth == 0) {
                this.depthMarker.setVisibility(View.GONE);
            } else {
                this.depthMarker.setVisibility(View.VISIBLE);
            }
            params.leftMargin = indentSize * (comment.depth - 1);
            this.root.setLayoutParams(params);

            if (comment.isByOp()) {
                this.author.setTextColor(this.root.getResources().getColor(R.color.light_theme_color_primary));
                this.author.setText(String.format("%s (OP)", this.author.getText()));
            }

            // Set marker color depending on depth
            this.depthMarker.setBackgroundColor(colorDepthArr[comment.depth % colorDepthArr.length]);

            this.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    root.callOnClick();
                }
            });
            this.root.setOnClickListener(onClickListener);

            if (comment.isGroup()) {
                this.collapsedCommentCounter.setVisibility(View.VISIBLE);
                this.collapsedCommentCounter.setText(Integer.toString(comment.getGroupSize()));
            } else {
                this.collapsedCommentCounter.setVisibility(View.INVISIBLE);
            }

            Log.i("TAG", "bindView time: " + (System.currentTimeMillis() - startTime));
        }
    }
}
