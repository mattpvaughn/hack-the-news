package io.github.httpmattpvaughn.hnapp.mainactivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.httpmattpvaughn.hnapp.MainActivityContract;
import io.github.httpmattpvaughn.hnapp.MainActivityPresenter;
import io.github.httpmattpvaughn.hnapp.data.model.Story;
import io.github.httpmattpvaughn.hnapp.details.DetailsContract;
import io.github.httpmattpvaughn.hnapp.frontpage.FrontPageContract;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class MainActivityTest {

    @Mock
    MainActivityContract.View view;

    private MainActivityContract.Presenter presenter;

    @Mock
    DetailsContract.Presenter detailsPresenter;

    @Mock
    FrontPageContract.Presenter frontPagePresenter;

    @Before
    public void setUpPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        presenter = new MainActivityPresenter();
        presenter.attachView(view);

        presenter.addDetailsPresenter(detailsPresenter);
        presenter.addFrontPagePresenter(frontPagePresenter);
    }

    @Test
    public void openArticle() {
        Story fakeStory = new Story();

        presenter.openArticle(fakeStory);

        // make sure presenter does the right thing...
        verify(view).openDetailsPage();
    }

    @Test
    public void openDiscussion() {
        Story fakeStory = new Story();

        presenter.openDiscussion(fakeStory);

        // make sure presenter does the right thing...
        verify(view).openDetailsPage();
    }

    @Test
    public void closeDetailsPage() {
        presenter.closeDetailsPage();

        verify(view).closeDetailsPage();
    }

}
