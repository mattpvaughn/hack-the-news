package io.github.httpmattpvaughn.hnapp.details;

import android.util.Log;

import io.github.httpmattpvaughn.hnapp.MainActivityContract;
import io.github.httpmattpvaughn.hnapp.data.HackerNewsService;
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
        if (parentComment == null) {
            return;
        }
        if (parentComment.kids == null || parentComment.kids.length == 0) {
            return;
        }

        for (int i = 0; i < parentComment.kids.length; i++) {
            final int childCommentId = parentComment.kids[i];
            Call<Story> call = HackerNewsService.retrofit
                    .create(HackerNewsService.class)
                    .item(childCommentId);
            call.enqueue(new Callback<Story>() {
                @Override
                public void onResponse(Call<Story> call, Response<Story> response) {
                    Story childComment = response.body();
                    if (childComment != null) {
                        childComment.depth = depth;
                        if(view != null) {
                            view.addComment(childComment, parentComment);
                        }
                        // Recursively load children comments of this comment
                        if (childComment.kids != null && childComment.kids.length != 0) {
                            loadComments(childComment, depth + 1);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Story> call, Throwable t) {
                    view.printErrorMessage("Unable to retrieve comments. Check your internet connection.");
                    Log.e("com.mattpvaughn.hnapp", "Unable to load comment with id " + childCommentId);
                }
            });
        }
    }

    @Override
    public void addView(DetailsContract.View view) {
        this.view = view;
    }
}
