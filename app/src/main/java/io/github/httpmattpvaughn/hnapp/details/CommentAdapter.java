package io.github.httpmattpvaughn.hnapp.details;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.oissela.software.multilevelexpindlistview.MultiLevelExpIndListAdapter;

import io.github.httpmattpvaughn.hnapp.R;
import io.github.httpmattpvaughn.hnapp.Util;
import io.github.httpmattpvaughn.hnapp.data.model.Story;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class CommentAdapter extends MultiLevelExpIndListAdapter<CommentAdapter.ViewHolder> {

    private final View.OnClickListener onClickListener;
    private final Story currentStory;
    private int[] colorDepthArr = new int[]{
            0xFFFFEB3B,
            0xFFFFC107,
            0xFFFF9800,
            0xFFFF5722,
            0xFFF44336,
            0xFF673AB7,
            0xFF3F51B5
    };
    private BetterLinkMovementMethod.OnLinkClickListener onLinkClickListener;

    public CommentAdapter(Story currentStory, BetterLinkMovementMethod.OnLinkClickListener onLinkClickListener, View.OnClickListener onClickListener) {
        this.currentStory = currentStory;
        this.onLinkClickListener = onLinkClickListener;
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

    public void addComment(Story childComment, Story parentComment) {
        if (indexOf(childComment) != -1) {
            return;
        }
        // if childcomment is root comment, add directly to adapter
        if (childComment.parent == currentStory.id) {
            add(childComment);
        } else {
            int parentPosition = indexOf(parentComment);
            parentComment.addChild(childComment);
            insert(parentPosition + 1, childComment);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView collapsedCommentCounter;
        public ConstraintLayout root;
        public TextView author;
        public TextView time;
        public TextView text;
        public View depthMarker;
        public View textContainer;
        public Story story;

        public ViewHolder(View itemView) {
            super(itemView);
            this.root = itemView.findViewById(R.id.story_item);
            this.author = itemView.findViewById(R.id.author);
            this.time = itemView.findViewById(R.id.time);
            this.text = itemView.findViewById(R.id.text);
            this.depthMarker = itemView.findViewById(R.id.depth_marker);
            this.textContainer = itemView.findViewById(R.id.comment_text_container);
            this.collapsedCommentCounter = itemView.findViewById(R.id.collapsed_comment_counter);
        }

        // Do it this way so we have an easy way to handle different types
        public void setComment(final Story comment) {
            this.story = comment;
            if (comment.by != null) {
                this.author.setText(comment.by);
            } else {
                this.author.setText("deleted");
            }
            this.time.setText(Util.beautifyPostAge(comment.time));
            if (comment.text != null) {
                this.text.setText(Util.stringToHtml(comment.text));
                BetterLinkMovementMethod
                        .linkify(Linkify.ALL, this.text)
                        .setOnLinkClickListener(onLinkClickListener);
            } else {
                this.text.setText("[Deleted]");
            }

            // Indent child comments depending on depth
            int depthMarkerWidth = (int) this.root.getContext().getResources().getDimension(R.dimen.depth_marker_width);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) this.depthMarker.getLayoutParams();
            if(comment.depth == 0) {
                params.width = 0;
            } else {
                params.width = depthMarkerWidth;
            }
            params.leftMargin = depthMarkerWidth * (comment.depth - 1);
            this.depthMarker.setLayoutParams(params);

            // Set marker color depending on depth
            this.depthMarker.setBackgroundColor(colorDepthArr[comment.depth % colorDepthArr.length]);

            // HACK! Use this until we figure out BetterLinkMovementMethod stealing onClicks from parents
            this.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(root);
                }
            });
            this.root.setOnClickListener(onClickListener);

            if (comment.isGroup()) {
                this.collapsedCommentCounter.setVisibility(View.VISIBLE);
                this.collapsedCommentCounter.setText(Integer.toString(comment.getGroupSize()));
            } else {
                this.collapsedCommentCounter.setVisibility(View.INVISIBLE);
            }
        }
    }
}
