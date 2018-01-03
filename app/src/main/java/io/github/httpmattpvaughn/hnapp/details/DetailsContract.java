package io.github.httpmattpvaughn.hnapp.details;

import java.util.List;

import io.github.httpmattpvaughn.hnapp.data.model.Story;

/**
 * The details page- contains article webview and discussion page
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public interface DetailsContract {
    // The *view* part of model-view-presenter
    interface View {
        // Opens the webview and loads an article in it
        void openArticle(String url);

        // Begins loading the discussion page for a story
        void loadDiscussion(Story story);

        // Prints an error message as a toast
        void printErrorMessage(String string);

        // Slides to the second page, does NOT slide down webview
        void openDiscussion(String url);

        void addComments(List<Story> comments);

        void showCommentsLoading();

        void hideCommentsLoading();

        void setArticleViewLock(boolean isLocked);
    }

    // the *presenter* part of model-view-presenter
    interface Presenter {
        // Give a reference to the view
        void addView(DetailsContract.View view);

        // Open an article in the details, including comments, webview, etc.
        void openArticle(Story story);

        // Opens a link in the webview
        void openLink(String url);

        // Opens a story to the comments
        void openDiscussion(Story story);

        // Returns to the top stories page
        void closeDetailsPage();

        // Remove reference to the view
        void detachView();
    }
}
