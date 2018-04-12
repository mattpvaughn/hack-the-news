package io.github.httpmattpvaughn.hnapp.frontpage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.httpmattpvaughn.hnapp.MainActivityContract;
import io.github.httpmattpvaughn.hnapp.MainActivityPresenter;
import io.github.httpmattpvaughn.hnapp.data.StoryManager;
import io.github.httpmattpvaughn.hnapp.data.StoryManagerMock;
import io.github.httpmattpvaughn.hnapp.data.model.Story;
import io.github.httpmattpvaughn.hnapp.details.DetailsPresenter;

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

    private MainActivityContract.Presenter parentPresenter;

    @Mock
    DetailsPresenter detailsPresenter;

    @Mock
    MainActivityContract.View parentView;

    @Before
    public void setUpPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        parentPresenter = new MainActivityPresenter();
        parentPresenter.attachView(parentView);
        parentPresenter.addDetailsPresenter(detailsPresenter);

        presenter = new FrontPagePresenter(parentPresenter);
        presenter.attachView(view);
        parentPresenter.addFrontPagePresenter(presenter);

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
        storyManager.resetStoriesLoadedCount();
    }

    @Test
    public void openArticle() {
        Story fakeStory = StoryManagerMock.getFakeStory();
        parentPresenter.openArticle(fakeStory);

        verify(parentView).openDetailsPage();
        verify(detailsPresenter).openArticle(fakeStory);
    }

    @Test
    public void openDiscussion() {
        Story fakeStory = StoryManagerMock.getFakeStory();
        parentPresenter.openDiscussion(fakeStory);

        verify(parentView).openDetailsPage();
        verify(detailsPresenter).openDiscussion(fakeStory);
    }
}
