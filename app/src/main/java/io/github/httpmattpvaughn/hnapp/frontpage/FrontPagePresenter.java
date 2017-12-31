package io.github.httpmattpvaughn.hnapp.frontpage;

import java.util.List;

import io.github.httpmattpvaughn.hnapp.MainActivityContract;
import io.github.httpmattpvaughn.hnapp.data.StoryManager;
import io.github.httpmattpvaughn.hnapp.data.StoryRepository;
import io.github.httpmattpvaughn.hnapp.data.model.Story;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class FrontPagePresenter implements FrontPageContract.Presenter {

    private MainActivityContract.Presenter parentPresenter;
    private FrontPageContract.View view;
    private StoryManager storyManager;
    private boolean isRefresh = false;
    private boolean isStoryListLoaded = false;

    public FrontPagePresenter(MainActivityContract.Presenter parentPresenter) {
        this.parentPresenter = parentPresenter;
    }

    @Override
    public void loadStories() {
        System.out.println("Loading stories");
        if (storyManager == null) {
            storyManager = new StoryManager();
        }
        if (!isStoryListLoaded) {
            storyManager.getStoryIdArray(new StoryRepository.GetStoryIdsCallback() {
                @Override
                public void onPostsLoaded(Integer[] storyIds) {
                    System.out.println("Story ids loaded");
                    isStoryListLoaded = true;
                    storyManager.getStoryList(new StoryRepository.GetStoryListCallback() {
                        @Override
                        public void onPostsLoaded(List<Story> stories) {
                            if(isRefresh) {
                                view.hideRefreshLoader();
                            }
                            view.addStories(stories);
                        }
                    });
                }
            });
        } else {
            storyManager.getStoryList(new StoryRepository.GetStoryListCallback() {
                @Override
                public void onPostsLoaded(List<Story> stories) {
                    if(isRefresh) {
                        view.hideRefreshLoader();
                    }
                    view.addStories(stories);
                }
            });
        }

    }

    @Override
    public void reloadStories() {
        isRefresh = true;
        view.clearStories();
        resetStoriesLoadedCount();
        loadStories();
    }

    @Override
    public void resetStoriesLoadedCount() {
        if (storyManager == null) {
            storyManager = new StoryManager();
        }
        storyManager.resetStoriesLoadedCount();
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
