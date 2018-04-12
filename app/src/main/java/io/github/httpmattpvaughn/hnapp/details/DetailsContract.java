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

        // Sets a list of comments as the primary list in a recyclerview
        void addComments(List<Story> comments);

        // Appends a list of comments to their parent
        void addComments(List<Story> comments, Story parent);

        // Appends a list of comments to their parent
        void addComment(Story comment, Story parent);

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

        // Returns the story currently open
        Story getCurrentStory();

        // Set the current story
        void setCurrentStory(Story story);

        // Load comments
        void loadComments(Story story);

        // Remove reference to the view
        void detachView();

        // Set comments in the discussion
        void setComments(List<Story> comments);
    }
}
