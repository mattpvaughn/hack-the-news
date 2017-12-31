package io.github.httpmattpvaughn.hnapp.details;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.httpmattpvaughn.hnapp.MainActivityContract;
import io.github.httpmattpvaughn.hnapp.data.HackerNewsService;
import io.github.httpmattpvaughn.hnapp.data.StoryManager;
import io.github.httpmattpvaughn.hnapp.data.StoryRepository;
import io.github.httpmattpvaughn.hnapp.data.model.Story;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    public void openArticle(Story story) {
        // Show webview part of the detailsView
        view.openArticle(story.url);
        view.loadDiscussion(story);
        loadComments(story, 0);
    }

    @Override
    public void openDiscussion(Story story) {
        view.openDiscussion(story.url);
        view.loadDiscussion(story);
        loadComments(story, 0);
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
    private void loadComments(final Story parentComment, final int depth) {
        if(storyManager == null) {
            storyManager = new StoryManager();
        }
        storyManager.getCommentsList(new StoryRepository.GetCommentsListCallback() {
            @Override
            public void onCommentsLoad(List<Story> comments, Story parent) {
                view.addComments(comments, parent);
            }
        }, parentComment, 0, new ArrayList<Story>());
    }

    @Override
    public void addView(DetailsContract.View view) {
        this.view = view;
    }
}
