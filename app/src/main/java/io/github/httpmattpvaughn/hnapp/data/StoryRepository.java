package io.github.httpmattpvaughn.hnapp.data;

import android.support.annotation.NonNull;

import java.util.List;

import io.github.httpmattpvaughn.hnapp.data.model.Story;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public interface StoryRepository {
    interface GetStoryIdsCallback {
        void onPostsLoaded(Integer[] storyIds);
    }

    interface GetStoryListCallback {
        void onPostsLoaded(List<Story> stories);
    }

    interface GetCommentsListCallback {
        void onCommentsLoad(List<Story> comments, Story parent);
    }

    void getStoryIdArray(@NonNull GetStoryIdsCallback callback);

    void getStoryList(@NonNull GetStoryListCallback callback);

    void getCommentsList(@NonNull GetCommentsListCallback callback,
                         Story parent,
                         int depth,
                         List<Story> comments);
}
