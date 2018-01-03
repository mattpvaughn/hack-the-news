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

    public DetailsPresenter(MainActivityContract.Presenter parentPresenter) {
        this.parentPresenter = parentPresenter;
    }

    @Override
    public void openArticle(Story item) {
        // Show webview part of the detailsView
        if(item.isStory()) {
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
        if(!item.isStory()) {
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
    public void detachView() {
        view = null;
    }

    @Override
    public void openLink(String url) {
        view.openArticle(url);
    }


    // Recursively load all comments depth-first
    private void loadComments(final Story parentComment) {
        if (storyManager == null) {
            storyManager = new StoryManager();
        }
        storyManager.getCommentsList(new StoryRepository.GetCommentsListCallback() {
            @Override
            public void onCommentsLoad(List<Story> comments, Story parent) {
                view.addComments(comments);
                view.hideCommentsLoading();
            }
        }, parentComment);
    }

    @Override
    public void addView(DetailsContract.View view) {
        this.view = view;
    }
}
