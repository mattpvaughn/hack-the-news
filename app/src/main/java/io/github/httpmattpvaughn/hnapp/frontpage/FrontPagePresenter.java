package io.github.httpmattpvaughn.hnapp.frontpage;

import java.util.List;

import io.github.httpmattpvaughn.hnapp.MainActivityContract;
import io.github.httpmattpvaughn.hnapp.data.Injection;
import io.github.httpmattpvaughn.hnapp.data.StoryRepository;
import io.github.httpmattpvaughn.hnapp.data.model.Story;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class FrontPagePresenter implements FrontPageContract.Presenter {

    private MainActivityContract.Presenter parentPresenter;
    private FrontPageContract.View view;
    private StoryRepository storyRepository;
    private boolean isStoryListLoaded = false;

    public FrontPagePresenter(MainActivityContract.Presenter parentPresenter) {
        this.parentPresenter = parentPresenter;
    }

    @Override
    public void loadStories() {
        System.out.println("Loading stories");
        if (storyRepository == null) {
            storyRepository = Injection.provideStoryRepository();
        }
        if (!isStoryListLoaded) {
            storyRepository.getStoryIdArray(new StoryRepository.GetStoryIdsCallback() {
                @Override
                public void onPostsLoaded(Integer[] storyIds) {
                    System.out.println("Story ids loaded");
                    isStoryListLoaded = true;
                    storyRepository.getStoryList(new StoryRepository.GetStoryListCallback() {
                        @Override
                        public void onPostsLoaded(List<Story> stories) {
                            if (stories == null || stories.isEmpty()) {
                                view.showErrorMessage("Error loading stories. Maybe you've reached the end?");
                                return;
                            }
                            view.setRefreshing(false);
                            view.addStories(stories);
                            for (Story parents : stories) {
                                storyRepository.getCommentsList(new StoryRepository.GetCommentsListCallback() {
                                    @Override
                                    public void onCommentsLoaded(List<Story> comments, Story parent) {
                                        for (Story comment : comments) {
                                            if (comment.by != null) {
                                                if (comment.by.equals(parent.by)) {
                                                    comment.setIsByOp(true);
                                                }
                                            }
                                        }
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
            storyRepository.getStoryList(new StoryRepository.GetStoryListCallback() {
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
        if (storyRepository == null) {
            storyRepository = Injection.provideStoryRepository();
        }
        storyRepository.resetStoriesLoadedCount();
    }

    @Override
    public void openArticle(Story story) {
        assert story.isStory();
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
