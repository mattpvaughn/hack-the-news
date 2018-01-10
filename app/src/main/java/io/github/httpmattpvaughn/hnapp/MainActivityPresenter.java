package io.github.httpmattpvaughn.hnapp;

import java.util.List;

import io.github.httpmattpvaughn.hnapp.data.model.Story;
import io.github.httpmattpvaughn.hnapp.details.DetailsContract;
import io.github.httpmattpvaughn.hnapp.frontpage.FrontPageContract;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 * Presenter for the mainActivity
 * NOTE: "make sure that presenter doesn’t depend on Android classes"
 * --> stuff that depends on context (sharedprefs, etc.) should be handled in activity
 */

public class MainActivityPresenter implements MainActivityContract.Presenter {

    private MainActivityContract.View view;
    private FrontPageContract.Presenter frontPagePresenter;
    private DetailsContract.Presenter detailsPresenter;

    @Override
    public void openArticle(Story story) {
        this.detailsPresenter.openArticle(story);

        // Slide over to the right
        view.openDetailsPage();
    }

    @Override
    public void openDiscussion(Story story) {
        detailsPresenter.openDiscussion(story);

        // Slide over to the right
        view.openDetailsPage();
    }

    @Override
    public void addDetailsPresenter(DetailsContract.Presenter detailsPresenter) {
        this.detailsPresenter = detailsPresenter;
    }

    @Override
    public void addFrontPagePresenter(FrontPageContract.Presenter frontPagePresenter) {
        this.frontPagePresenter = frontPagePresenter;
    }

    @Override
    public void closeDetailsPage() {
        view.closeDetailsPage();
    }

    @Override
    public Story getCurrentStory() {
        if (view.isDetailsPageOpen()) {
            return detailsPresenter.getCurrentStory();
        }
        return null;
    }

    @Override
    public void attachView(MainActivityContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void setComments(List<Story> comments) {
        detailsPresenter.setComments(comments);
    }
}
