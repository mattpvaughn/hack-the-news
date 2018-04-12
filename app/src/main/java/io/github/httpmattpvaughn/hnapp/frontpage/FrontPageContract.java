package io.github.httpmattpvaughn.hnapp.frontpage;

import java.util.List;

import io.github.httpmattpvaughn.hnapp.data.model.Story;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public interface FrontPageContract {
    // The *view* part of model-view-presenter
    interface View {
        // Adds a list of stories to the end of the current list of stories
        // on the front page
        void addStories(List<Story> stories);

        // Removes all stories from the front page
        void clearStories();

        // Show toast containing error message
        void showErrorMessage(String string);

        void setRefreshing(boolean isRefreshing);
    }

    // the *presenter* part of model-view-presenter
    interface Presenter {
        // Gets a list of the next top stories from the Model, passes them to
        // the view
        void loadStories();

        // Removes current stories from view, loads in first 20 stories from web
        void reloadStories();

        // Resets the tracker on how many stories have been read
        void resetStoriesLoadedCount();

        // Opens an article to the webview
        void openArticle(Story story);

        // Give a presenter a reference to the view
        void attachView(FrontPageContract.View view);

        // Opens article to the comments
        void openDiscussion(Story story);

        // Remove reference to view
        void detachView();
    }

    interface StoryClickListener {
        void onClick(android.view.View view);
        void onClickComment(android.view.View view);

        void onLongClick(android.view.View view);
    }
}
