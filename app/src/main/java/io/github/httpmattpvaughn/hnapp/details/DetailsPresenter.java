package io.github.httpmattpvaughn.hnapp.details;

import java.util.List;

import io.github.httpmattpvaughn.hnapp.MainActivityContract;
import io.github.httpmattpvaughn.hnapp.data.StoryManager;
import io.github.httpmattpvaughn.hnapp.data.StoryRepository;
import io.github.httpmattpvaughn.hnapp.data.model.Story;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class DetailsPresenter implements DetailsContract.Presenter {

    private MainActivityContract.Presenter parentPresenter;
    private DetailsContract.View view;
    private StoryManager storyManager;
    private Story currentStory;
    private boolean isLoading = false;

    public DetailsPresenter(MainActivityContract.Presenter parentPresenter) {
        this.parentPresenter = parentPresenter;
    }

    @Override
    public void openArticle(Story item) {
        this.currentStory = item;
        // Show webview part of the detailsView
        if (item.isStory()) {
            view.openArticle(item.url);
            view.loadDiscussion(item);
            view.showCommentsLoading();
            view.setArticleViewLock(false);
            loadComments(item);
        } else {
            // the article is not a story (e.g. it's just a text post or something...)
            openDiscussion(item);
            view.setArticleViewLock(true);
        }
    }

    @Override
    public void openDiscussion(Story item) {
        this.currentStory = item;
        if (!item.isStory()) {
            // the article is not a story (e.g. it's just a text post or something...)
            view.setArticleViewLock(true);
        } else {
            view.setArticleViewLock(false);
        }
        view.openDiscussion(item.url);
        view.loadDiscussion(item);
        view.showCommentsLoading();
        loadComments(item);
    }

    @Override
    public void closeDetailsPage() {
        parentPresenter.closeDetailsPage();
    }

    @Override
    public Story getCurrentStory() {
        return currentStory;
    }

    @Override
    public void setCurrentStory(Story story) {
        this.currentStory = story;
        if (view != null) {
            loadComments(story);
        }
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void setComments(List<Story> comments) {
        isLoading = false;
        view.addComments(comments);
        view.hideCommentsLoading();
    }

    @Override
    public void openLink(String url) {
        view.openArticle(url);
    }


    // Load all comments and set in the view
    public void loadComments(final Story parentComment) {
        isLoading = true;
        if (storyManager == null) {
            storyManager = new StoryManager();
        }
//        Load comments together
        storyManager.getCommentsList(new StoryRepository.GetCommentsListCallback() {
            @Override
            public void onCommentsLoaded(List<Story> comments, Story parent) {
                if (isLoading) {
                    setComments(comments);
                }
            }
        }, parentComment);
//        Load comments individually
//        storyManager.loadCommentsIndividually(new StoryRepository.GetCommentsListCallback() {
//            @Override
//            public void onCommentsLoaded(List<Story> comments, Story parent) {
//                view.addComments(comments, parent);
//            }
//        }, parentComment, 0, new ArrayList<Story>());
//        Load fake comments
//        storyManager.getFakeCommentsIndividually(new StoryRepository.LoadCommentsIndividuallyCallback() {
//            @Override
//            public void onCommentsLoaded(List<Story> comments, List<Story> parents) {
//                view.addFakeComments(comments, parents);
//            }
//        });
    }

    @Override
    public void addView(DetailsContract.View view) {
        this.view = view;
    }
}
