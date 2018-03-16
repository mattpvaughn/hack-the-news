package io.github.httpmattpvaughn.hnapp.details;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.httpmattpvaughn.hnapp.R;
import io.github.httpmattpvaughn.hnapp.Util;
import io.github.httpmattpvaughn.hnapp.data.model.Story;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class UncollapsibleCommentAdapter extends RecyclerView.Adapter {

    List<Story> data;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View storyItem = inflater.inflate(R.layout.comment_item, parent, false);
        return new UncollapsibleCommentAdapter.ViewHolder(storyItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((UncollapsibleCommentAdapter.ViewHolder) holder).setComment(data.get(position));
    }

    public void setData(List<Story> data) {
        this.data = data;
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    public void setList(List<Story> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    //
    private void addComment(List<Story> storyList, Story comment, Story parent) {
        if (storyList == null) {
            storyList = new ArrayList<>();
        }
        int parentPosition = storyList.indexOf(parent);
        if (parentPosition == -1) {
            throw new IllegalArgumentException("Attempted to call addComment on a comment when parent is not added!");
        }
        parent.addChild(comment);
        storyList.add(parentPosition + 1, comment);

    }

    public void setComments(List<Story> comments, List<Story> parents) {
        // Update list with all of these comments, trigger recyclerview redraw
        if (comments == null || parents == null || comments.size() != parents.size()) {
            throw new IllegalArgumentException("There must be a parent for every comment!");
        }

        List<Story> dataCopy = new ArrayList<>();
        if (data == null) {
            setList(comments);
            return;
        } else {
            dataCopy.addAll(data);
        }
        // Add all comments to our fake list of comments
        for (int i = 0; i < comments.size(); i++) {
            addComment(dataCopy, comments.get(i), parents.get(i));
        }

        // run DiffUtils on new/old list, update what needs to be updated
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CommentDiffCallback(dataCopy, data));
        this.data.clear();
        this.data.addAll(dataCopy);
        diffResult.dispatchUpdatesTo(this);
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
                this.text.setText(Util.stringToHtml(comment.text, this.text, null));
//                BetterLinkMovementMethod
//                        .linkify(Linkify.ALL, this.text)
//                        .setOnLinkClickListener(new BetterLinkMovementMethod.OnLinkClickListener() {
//                            @Override
//                            public boolean onClick(TextView textView, String url) {
//                                return false;
//                            }
//                        })
//                        .setOnLinkLongClickListener(new BetterLinkMovementMethod.OnLinkLongClickListener() {
//                            @Override
//                            public boolean onLongClick(TextView textView, String url) {
//                                return false;
//                            }
//                        });
            } else {
                this.text.setText("[Deleted]");
            }

            // Indent child comments depending on depth
            int depthMarkerWidth = (int) this.root.getContext().getResources().getDimension(R.dimen.depth_marker_width);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) this.depthMarker.getLayoutParams();
            if (comment.depth == 0) {
                params.width = 0;
            } else {
                params.width = depthMarkerWidth;
            }
            params.leftMargin = depthMarkerWidth * (comment.depth - 1);
            this.depthMarker.setLayoutParams(params);

            // Set marker color depending on depth
            this.depthMarker.setBackgroundColor(Color.RED);

            // Hack alert! Use this until we figure out BetterLinkMovementMethod stealing onClicks from parents
            this.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    onClickListener.onClick(root);
                }
            });
            this.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            if (comment.isGroup()) {
                this.collapsedCommentCounter.setVisibility(View.VISIBLE);
                this.collapsedCommentCounter.setText(Integer.toString(comment.getGroupSize()));
            } else {
                this.collapsedCommentCounter.setVisibility(View.INVISIBLE);
            }
            Log.i("TAG", "bindView time: " + (System.currentTimeMillis() - startTime));
        }
    }

    public class CommentDiffCallback extends DiffUtil.Callback {
        List<Story> oldComments;
        List<Story> newComments;

        public CommentDiffCallback(List<Story> newComments, List<Story> oldComments) {
            this.newComments = newComments;
            this.oldComments = oldComments;
        }

        @Override
        public int getOldListSize() {
            return oldComments.size();
        }

        @Override
        public int getNewListSize() {
            return newComments.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldComments.get(oldItemPosition).id == newComments.get(newItemPosition).id;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldComments.get(oldItemPosition).equals(newComments.get(newItemPosition));
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            //you can return particular field for changed item.
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }
}
