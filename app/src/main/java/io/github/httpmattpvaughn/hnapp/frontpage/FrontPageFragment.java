package io.github.httpmattpvaughn.hnapp.frontpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.httpmattpvaughn.hnapp.R;
import io.github.httpmattpvaughn.hnapp.SettingsActivity;
import io.github.httpmattpvaughn.hnapp.data.model.Story;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class FrontPageFragment extends Fragment implements FrontPageContract.View,
        NavigationView.OnNavigationItemSelectedListener,
        FrontPageContract.StoryClickListener {

    private FrontPageContract.Presenter presenter;
    private StoryAdapter storyAdapter;
    private boolean isViewCreated = false;
    private boolean isAttached = false;
    private RecyclerView frontPageRecyclerView;
    private LinearLayoutManager layoutManager;

    public void attachPresenter(final FrontPageContract.Presenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);
        this.isAttached = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frontpage, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getActivity() == null && getActivity() instanceof AppCompatActivity;
        this.isViewCreated = true;

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        DrawerLayout drawer = view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        frontPageRecyclerView = view.findViewById(R.id.frontpage_recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        frontPageRecyclerView.setLayoutManager(layoutManager);
        storyAdapter = new StoryAdapter(this, new ArrayList<Story>());
        frontPageRecyclerView.setAdapter(storyAdapter);

        if (isAttached) {
            EndlessRecyclerViewScrollListener listener = new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    presenter.loadStories();
                }
            };
            frontPageRecyclerView.addOnScrollListener(listener);

            SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.drag_refresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    presenter.reloadStories();
                }
            });

            // Get data for front page
            presenter.loadStories();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.stories) {
            // close drawer
        }

        DrawerLayout drawer = (DrawerLayout) getView().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void addStories(List<Story> stories) {
        storyAdapter.addStories(stories);
    }

    @Override
    public void clearStories() {
        storyAdapter.clearStories();
    }

    @Override
    public void showErrorMessage(String string) {
        Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideRefreshLoader() {
        SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.drag_refresh);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View view) {
        // call openDetailsPage() here...
        Story story = (Story) view.getTag();
        presenter.openArticle(story);
    }

    @Override
    public void onClickComment(View view) {
        Story story = (Story) view.getTag();
        presenter.openDiscussion(story);
    }
}
