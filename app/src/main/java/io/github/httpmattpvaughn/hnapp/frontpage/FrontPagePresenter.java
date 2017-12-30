package io.github.httpmattpvaughn.hnapp.frontpage;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.httpmattpvaughn.hnapp.MainActivityContract;
import io.github.httpmattpvaughn.hnapp.data.HackerNewsService;
import io.github.httpmattpvaughn.hnapp.data.model.Story;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class FrontPagePresenter implements FrontPageContract.Presenter {

    private MainActivityContract.Presenter parentPresenter;
    private FrontPageContract.View view;
    private int storiesLoaded = 0;
    private Integer[] storyArr;

    private boolean isReloading = false;

    // number of stories to load at a time
    private static final int STORIES_PER_PAGE = 25;

    public FrontPagePresenter(MainActivityContract.Presenter parentPresenter) {
        this.parentPresenter = parentPresenter;
    }

    @Override
    public void loadStories() {
        if (this.storyArr == null) {
            loadStoryList();
        }
        // Never done network stuff with an api that sends everying in individual requests...
        // This may be an abomination (comments too)
        if (storyArr != null) {
            final List<Story> stories = new ArrayList<>();
            for(int i = storiesLoaded; i < storiesLoaded + STORIES_PER_PAGE; i++) {
                final int storyId = storyArr[i];
                final int index = i;
                Call<Story> storyCall = HackerNewsService.retrofit.create(HackerNewsService.class).item(storyId);
                storyCall.enqueue(new Callback<Story>() {
                    @Override
                    public void onResponse(@NonNull Call<Story> call, @NonNull Response<Story> response) {
                        Story story = response.body();
                        stories.add(story);
                        storiesLoaded++;
                        System.out.println("Story with id \"" + storyId + "\" loaded. " + "(index is " + index + ".)" );
                        if(stories.size() >= STORIES_PER_PAGE && view != null) {
                            view.addStories(stories);
                        }
                        if(isReloading) {
                            view.hideRefreshLoader();
                            isReloading = false;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Story> call, @NonNull Throwable t) {
                        Log.e("", "Error loading item with id: " + storyId);
                    }
                });
            }
        }
    }

    @Override
    public void reloadStories() {
        isReloading = true;
        view.clearStories();
        resetStoriesLoadedCount();
        loadStories();
    }

    @Override
    public void resetStoriesLoadedCount() {
        storiesLoaded = 0;
    }

    @NonNull
    private void loadStoryList() {
        Call<Integer[]> call = HackerNewsService.retrofit.create(HackerNewsService.class).topStories();
        call.enqueue(new Callback<Integer[]>() {
            @Override
            public void onResponse(@NonNull Call<Integer[]> call, @NonNull Response<Integer[]> response) {
                FrontPagePresenter.this.storyArr = response.body();
                loadStories();
            }

            @Override
            public void onFailure(@NonNull Call<Integer[]> call, @NonNull Throwable t) {
                view.showErrorMessage("Cannot load stories. Check your internet connection");
            }
        });
    }

    @Override
    public void openArticle(Story story) {
        parentPresenter.openArticle(story);
    }

    @Override
    public void openDiscussion(Story story) {
        parentPresenter.openDiscussion(story);
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void attachView(FrontPageContract.View view) {
        this.view = view;
    }
}
