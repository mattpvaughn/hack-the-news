package io.github.httpmattpvaughn.hnapp.frontpage;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import io.github.httpmattpvaughn.hnapp.data.model.Story;
import io.github.httpmattpvaughn.hnapp.settings.SettingsActivity;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class FrontPageFragment extends Fragment implements FrontPageContract.View,
        NavigationView.OnNavigationItemSelectedListener,
        FrontPageContract.StoryClickListener {

    private FrontPageContract.Presenter presenter;
    private StoryAdapter storyAdapter;
    private boolean isAttached = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    public void attachPresenter(final FrontPageContract.Presenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);
        this.isAttached = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frontpage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getActivity() == null && getActivity() instanceof AppCompatActivity;

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        DrawerLayout drawer = view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView frontPageRecyclerView = view.findViewById(R.id.frontpage_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        frontPageRecyclerView.setLayoutManager(layoutManager);
        storyAdapter = new StoryAdapter(this, new ArrayList<Story>());
        frontPageRecyclerView.setAdapter(storyAdapter);

        swipeRefreshLayout = view.findViewById(R.id.drag_refresh);

        setRefreshing(true);

        if (isAttached) {
            EndlessRecyclerViewScrollListener listener = new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    presenter.loadStories();
                }
            };
            frontPageRecyclerView.addOnScrollListener(listener);

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

        DrawerLayout drawer = getView().findViewById(R.id.drawer_layout);
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
    public void setRefreshing(boolean isRefreshing) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(isRefreshing);
        }
    }

    @Override
    public void onClick(View view) {
        // call openDetailsPage() here...
        Story story = (Story) view.getTag();
        if (story.isStory()) {
            presenter.openArticle(story);
        } else {
            presenter.openDiscussion(story);
        }
    }

    @Override
    public void onClickComment(View view) {
        Story story = (Story) view.getTag();
        presenter.openDiscussion(story);
    }

    @Override
    public void onLongClick(View view) {
        // open up a dialog with options
        final CharSequence[] actions = new CharSequence[]{
                "Open in browser",
                "Share",
                "Copy link"
        };
        final Story story = (Story) view.getTag();
        final String url = story.url;
        AlertDialog.OnClickListener listener =
                new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String action = (String) actions[which];
                        switch (action) {
                            case "Open in browser":
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                                break;
                            case "Share":
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                break;
                            case "Copy":
                                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("URL", url);
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(getContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                };
        new AlertDialog.Builder(getContext())
                .setItems(actions, listener)
                .setCancelable(true)
                .setNegativeButton("Cancel", null)
                .setTitle("Actions")
                .create()
                .show();
    }
}
