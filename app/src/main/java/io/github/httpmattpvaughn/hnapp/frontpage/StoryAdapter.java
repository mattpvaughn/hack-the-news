package io.github.httpmattpvaughn.hnapp.frontpage;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.httpmattpvaughn.hnapp.R;
import io.github.httpmattpvaughn.hnapp.Util;
import io.github.httpmattpvaughn.hnapp.data.model.Story;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

// TODO- add clickability on comments, then start working on discussion/article!

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private List<Story> stories;
    private FrontPageContract.StoryClickListener storyClickListener;

    public StoryAdapter(FrontPageContract.StoryClickListener clickListener, List<Story> stories) {
        super();
        this.storyClickListener = clickListener;
        this.stories = stories;
    }

    public void addStories(List<Story> stories) {
        this.stories.addAll(stories);
        notifyDataSetChanged();
    }

    public void clearStories() {
        this.stories = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View storyItem = inflater.inflate(R.layout.story_item, parent, false);

        return new ViewHolder(storyItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setStory(stories.get(position));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView urlDrawable;
        public View root;
        public TextView title;
        public TextView author;
        public TextView url;
        public TextView score;
        public TextView time;
        public TextView comments;
        public Story story;

        public ViewHolder(View itemView) {
            super(itemView);
            this.root = itemView;
            this.score = itemView.findViewById(R.id.score);
            this.author = itemView.findViewById(R.id.author);
            this.title = itemView.findViewById(R.id.title);
            this.url = itemView.findViewById(R.id.url);
            this.time = itemView.findViewById(R.id.time);
            this.comments = itemView.findViewById(R.id.comments);
            this.urlDrawable = itemView.findViewById(R.id.url_drawable);
        }

        // Do it this way so we have an easy way to handle different types
        public void setStory(final Story story) {
            Context context = this.root.getContext();
            Resources.Theme theme = context.getTheme();
            this.story = story;
            this.score.setText(String.valueOf(story.score));
            if (story.score >= context.getResources().getInteger(R.integer.FIRE_STORY_THRESHOLD)) {
                this.score.setTextColor(ContextCompat.getColor(context, R.color.fire_story_color));
            } else {
                TypedValue typedValue = new TypedValue();
                theme.resolveAttribute(R.attr.rowTextColorPrimary, typedValue, true);
                this.score.setTextColor(typedValue.data);
            }
            // Don't show link icon is story is not a link
            if (story.isStory() && url == null) {
                this.urlDrawable.setImageResource(R.drawable.ic_text_fields_black_24dp);
                TypedValue typedValue = new TypedValue();
                theme.resolveAttribute(R.attr.rowDrawableColor, typedValue, true);
                this.urlDrawable.getDrawable().setColorFilter(typedValue.data, PorterDuff.Mode.MULTIPLY);
            }
            this.author.setText(story.by);
            this.url.setText(Util.beautifyUrl(story.url));
            this.title.setText(story.title);
            this.time.setText(Util.beautifyPostAge(story.time));
            this.comments.setText(String.valueOf(story.descendants));
            // Set the tag of the story_item view to url so we can handle
            // onclick in FrontPageFragment
            this.root.setTag(story);
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storyClickListener.onClick(root);
                }
            });
            this.comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storyClickListener.onClickComment(root);
                }
            });
        }
    }
}
