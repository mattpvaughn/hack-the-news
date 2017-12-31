package io.github.httpmattpvaughn.hnapp.frontpage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.httpmattpvaughn.hnapp.MainActivityContract;
import io.github.httpmattpvaughn.hnapp.data.StoryManager;
import io.github.httpmattpvaughn.hnapp.data.model.Story;

import static org.mockito.Mockito.verify;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class FrontPageTest {

    @Mock
    FrontPageContract.View view;

    @Mock
    StoryManager storyManager;

    private FrontPageContract.Presenter presenter;

    @Mock
    MainActivityContract.Presenter parentPresenter;


    @Before
    public void setUpPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        presenter = new FrontPagePresenter(parentPresenter);
        presenter.attachView(view);
    }

    @Test
    public void loadStories() {
        presenter.loadStories();
    }

    @Test
    public void reloadStories() {
        presenter.reloadStories();

        verify(view).clearStories();
    }

    @Test
    public void resetStoriesLoadedCount() {

    }


//    // Gets a list of the next top stories from the Model, passes them to
//    // the view
//    void loadStories();
//
//    // Removes current stories from view, loads in first 20 stories from web
//    void reloadStories();
//
//    // Resets the tracker on how many stories have been read
//    void resetStoriesLoadedCount();
//
//    // Opens an article to the webview
//    void openArticle(Story story);
//
//    // Give a presenter a reference to the view
//    void attachView(FrontPageContract.View view);
//
//    // Opens article to the comments
//    void openDiscussion(Story story);
//
//    // Remove reference to view
//    void detachView();


}
