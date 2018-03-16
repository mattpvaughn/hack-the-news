package io.github.httpmattpvaughn.hnapp.details;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.github.httpmattpvaughn.hnapp.MainActivityContract;
import io.github.httpmattpvaughn.hnapp.MainActivityPresenter;
import io.github.httpmattpvaughn.hnapp.data.StoryManager;
import io.github.httpmattpvaughn.hnapp.data.model.Story;

import static org.mockito.Mockito.verify;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class DetailsPageTest {

    @Mock
    DetailsContract.View view;

    private DetailsContract.Presenter presenter;

    private MainActivityContract.Presenter parentPresenter;

    @Mock
    MainActivityContract.View parentView;

    @Before
    public void setUpPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        parentPresenter = new MainActivityPresenter();
        parentPresenter.attachView(parentView);

        presenter = new DetailsPresenter(parentPresenter);
        presenter.addView(view);
        parentPresenter.addDetailsPresenter(presenter);

    }

    @Test
    public void openArticle() {
        Story fakeStory = StoryManager.getFakeStory();
        presenter.openArticle(fakeStory);

        verify(view).openArticle(fakeStory.url);
        verify(view).loadDiscussion(fakeStory);
        verify(view).loadDiscussion(fakeStory);
        verify(view).showCommentsLoading();
        verify(view).setArticleViewLock(false);
    }

    @Test
    public void openLink() {
        Story fakeStory = StoryManager.getFakeStory();
        presenter.openLink(fakeStory.url);

        verify(view).openArticle(fakeStory.url);
    }

    @Test
    public void openDiscussion() {
        Story fakeStory = StoryManager.getFakeStory();
        fakeStory.type = "-1";
        presenter.openDiscussion(fakeStory);

        verify(view).setArticleViewLock(true);
        verify(view).openDiscussion(fakeStory.url);
        verify(view).loadDiscussion(fakeStory);
        verify(view).showCommentsLoading();

    }

    @Test
    public void closeDetailsPage() {
        presenter.closeDetailsPage();

        verify(parentPresenter).closeDetailsPage();
    }

    // TODO- inject StoryManager so we can test it!
    @Test
    public void loadComments() {
        Story fakeStory = StoryManager.getFakeStory();
        presenter.loadComments(fakeStory);

        verify(parentPresenter).closeDetailsPage();
    }

    @Test
    public void setComments() {
        List<Story> fakeStoryList = StoryManager.getFakeStoryList();
        presenter.setComments(fakeStoryList);

        verify(view).addComments(fakeStoryList);
        verify(view).hideCommentsLoading();
    }


//    // Set comments in the discussion
//    void setComments(List<Story> comments);

}
