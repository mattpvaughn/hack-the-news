package io.github.httpmattpvaughn.hnapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.github.httpmattpvaughn.hnapp.details.DetailsContract;
import io.github.httpmattpvaughn.hnapp.details.DetailsFragment;
import io.github.httpmattpvaughn.hnapp.frontpage.FrontPageContract;
import io.github.httpmattpvaughn.hnapp.frontpage.FrontPageFragment;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final DetailsContract.Presenter detailsPresenter;
    private final FrontPageContract.Presenter frontPagePresenter;
    private static final int PAGE_COUNT = 2;

    public MainPagerAdapter(FragmentManager fm,
                            FrontPageContract.Presenter frontPagePresenter,
                            DetailsContract.Presenter detailsPresenter) {
        super(fm);
        this.detailsPresenter = detailsPresenter;
        this.frontPagePresenter = frontPagePresenter;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FrontPageFragment frontPageFragment = new FrontPageFragment();
                frontPageFragment.attachPresenter(frontPagePresenter);
                frontPageFragment.setRetainInstance(false);
                return frontPageFragment;
            case 1:
                DetailsFragment detailsPageFragment = new DetailsFragment();
                detailsPageFragment.attachPresenter(detailsPresenter);
                detailsPageFragment.setRetainInstance(false);
                return detailsPageFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
