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
                            if (stories == null || stories.isEmpty()) {
                                view.showErrorMessage("Sorry! HN API only provides up to 500 stories");
                                return;
                            }
                            view.setRefreshing(false);
                            view.addStories(stories);
                            for (Story parents : stories) {
                                storyManager.getCommentsList(new StoryRepository.GetCommentsListCallback() {
                                    @Override
                                    public void onCommentsLoaded(List<Story> comments, Story parent) {
                                        System.out.println("Cached " + parent.title);
                                        if (parent.equals(parentPresenter.getCurrentStory())) {
                                            parentPresenter.setComments(comments);
                                        }
                                    }
                                }, parents);
                            }
                        }
                    });
                }
            });
        } else {
            storyManager.getStoryList(new StoryRepository.GetStoryListCallback() {
                @Override
                public void onPostsLoaded(List<Story> stories) {
                    view.setRefreshing(false);
                    view.addStories(stories);
                }
            });
        }

    }

    @Override
    public void reloadStories() {
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
