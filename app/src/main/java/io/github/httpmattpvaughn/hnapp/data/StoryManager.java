package io.github.httpmattpvaughn.hnapp.data;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.httpmattpvaughn.hnapp.data.model.Story;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class StoryManager implements StoryRepository {

    private Integer[] storyIdArr;

    // Number of stories to be loaded at a time
    private static final int STORIES_PER_PAGE = 25;

    private int storiesLoaded = 0;

    @Override
    public void getStoryIdArray(@NonNull final GetStoryIdsCallback callback) {
        Call<Integer[]> call = HackerNewsService.retrofit.topStories();
        call.enqueue(new Callback<Integer[]>() {
            @Override
            public void onResponse(@NonNull Call<Integer[]> call, @NonNull Response<Integer[]> response) {
                storyIdArr = response.body();
                callback.onPostsLoaded(storyIdArr);
            }

            @Override
            public void onFailure(@NonNull Call<Integer[]> call, @NonNull Throwable t) {
                Log.e("HNapp", "Error loading story list.");
            }
        });
    }

    @Override
    public void getStoryList(@NonNull final GetStoryListCallback callback) {
        final List<Story> storyList = new ArrayList<>();
        for (int i = storiesLoaded; i < storiesLoaded + STORIES_PER_PAGE; i++) {
            Call<Story> call = HackerNewsService.retrofit.item(storyIdArr[i]);
            call.enqueue(new Callback<Story>() {
                @Override
                public void onResponse(@NonNull Call<Story> call, @NonNull Response<Story> response) {
                    Story story = response.body();
                    storyList.add(story);
                    storiesLoaded++;
                    if (storyList.size() == STORIES_PER_PAGE) {
                        callback.onPostsLoaded(storyList);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Story> call, @NonNull Throwable t) {
                    Log.e("HNapp", "Error loading story list. " + t);
                }
            });
        }
    }

    @Override
    public void getCommentsList(@NonNull final GetCommentsListCallback callback,
                                final Story parent) {
        FetchCommentsTask task = new FetchCommentsTask();
        task.execute(new ArrayList<>(), parent, callback);
    }

    private static class FetchCommentsTask extends AsyncTask<Object, Object, List<Story>> {
        private GetCommentsListCallback callback;
        private Story parent;

        @Override
        protected void onPostExecute(List<Story> story) {
            super.onPostExecute(story);
            callback.onCommentsLoad(story, parent);
        }

        @Override
        protected List<Story> doInBackground(Object[] objects) {
            List<Story> comments = (List<Story>) objects[0];
            parent = (Story) objects[1];
            callback = (GetCommentsListCallback) objects[2];
            return getAllComments(parent, comments, 0);
        }
    }

    // Synchronously get comments
    public static List<Story> getAllComments(Story parent, List<Story> comments, int depth) {
        if (parent.kids == null || parent.kids.length == 0) {
            return comments;
        }
        for (int i = 0; i < parent.kids.length; i++) {
            final int childCommentId = parent.kids[i];
            try {
                Response response = HackerNewsService.retrofit.item(childCommentId).execute();
                Story childComment = (Story) response.body();
                if (childComment != null) {
                    parent.addChild(childComment);
                    childComment.depth = depth;
                    int parentPosition = comments.indexOf(parent);
                    comments.add(parentPosition + 1, childComment);
                    // Recursively load children comments of this comment
                    if (childComment.kids != null && childComment.kids.length != 0) {
                        getAllComments(childComment, comments, depth + 1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return comments;
    }

    public void resetStoriesLoadedCount() {
        this.storiesLoaded = 0;
    }


}
