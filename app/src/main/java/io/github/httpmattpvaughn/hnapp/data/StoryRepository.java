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
        void onCommentsLoaded(List<Story> comments, Story parent);
    }

    interface GetCommentsCallback {
        void onCommentsLoad(List<Story> comments, Story parent);
    }

    interface LoadCommentsIndividuallyCallback {
        void onCommentsLoad(List<Story> comments, List<Story> parents);
    }

    // Obtain callback to get Integer[] of ids of the top 500 stories
    void getStoryIdArray(@NonNull GetStoryIdsCallback callback);

    // Obtain callback to get List<Story> of length 25 containing the 25 next
    // stories to be loaded
    void getStoryList(@NonNull GetStoryListCallback callback);

    // Obtain callback to get List<Story> of all comments for a post and the
    //
    void getCommentsList(@NonNull GetCommentsListCallback callback,
                         Story parent);

    // Obtain callback that allows user to load in stories to View one at a time
    void loadCommentsIndividually(@NonNull LoadCommentsIndividuallyCallback callback,
                                  Story parent,
                                  int depth);

    // Starts loading stories by the most recent again- usually used after a refresh
    void resetStoriesLoadedCount();
}
