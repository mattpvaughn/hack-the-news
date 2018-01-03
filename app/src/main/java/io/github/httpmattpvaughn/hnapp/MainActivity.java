package io.github.httpmattpvaughn.hnapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import io.github.httpmattpvaughn.hnapp.details.DetailsContract;
import io.github.httpmattpvaughn.hnapp.details.DetailsPresenter;
import io.github.httpmattpvaughn.hnapp.frontpage.FrontPageContract;
import io.github.httpmattpvaughn.hnapp.frontpage.FrontPagePresenter;

/*
 * Implement passive-view pattern- activity should more or less just handle basic
 * sets/gets/interactions with the views
 */

public class MainActivity extends AppCompatActivity implements MainActivityContract.View,
        ViewPager.OnPageChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private DisableableViewPager pager;
    private MainActivityContract.Presenter mainActivityPresenter;
    private FrontPageContract.Presenter frontPagePresenter;
    private DetailsContract.Presenter detailsPresenter;
    private static final int TOP_STORIES = 0;
    private static final int DETAILS_PAGE = 1;

    @Override
    protected void onStart() {
        super.onStart();
        this.frontPagePresenter.resetStoriesLoadedCount();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setTheme(this);
        super.onCreate(createBundleNoFragmentRestore(savedInstanceState));
        setContentView(R.layout.activity_main);

        createPresenters();

        setUpMainPagePager();
    }

    private void setUpMainPagePager() {
        pager = findViewById(R.id.view_pager);
        pager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(),
                frontPagePresenter,
                detailsPresenter
        ));
        pager.addOnPageChangeListener(this);

        if (!isDetailsPageOpen()) {
            pager.setSwipingEnabled(false);
        }
    }

    // A somewhat hacky approach to keeping android from restoring fragments.
    // from: https://stackoverflow.com/questions/15519214/prevent-fragment-recovery-in-android
    private static Bundle createBundleNoFragmentRestore(Bundle bundle) {
        if (bundle != null) {
            bundle.remove("android:support:fragments");
        }
        return bundle;
    }

    @Override
    protected void onDestroy() {
        mainActivityPresenter.detachView();
        detailsPresenter.detachView();
        frontPagePresenter.detachView();
        super.onDestroy();
    }

    // Restores presenters which have been retained or creates new ones as necessary
    private void createPresenters() {
        Presenters presenters = (Presenters) getLastCustomNonConfigurationInstance();
        if (presenters != null) {
            mainActivityPresenter = presenters.mainPresenter;
            detailsPresenter = presenters.detailsPresenter;
            frontPagePresenter = presenters.frontPagePresenter;
        } else {
            mainActivityPresenter = new MainActivityPresenter();
            detailsPresenter = new DetailsPresenter(mainActivityPresenter);
            frontPagePresenter = new FrontPagePresenter(mainActivityPresenter);
        }

        mainActivityPresenter.attachView(this);
        mainActivityPresenter.addDetailsPresenter(detailsPresenter);
        mainActivityPresenter.addFrontPagePresenter(frontPagePresenter);
    }

    @Override
    protected void onResume() {
//        if(PreferenceManager.getDefaultSharedPreferences(this)
//                .getBoolean(getString(R.string.use_dark_theme_key), false) != theme) {
//            recreate();
//        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        super.onResume();
    }

    // Keep presenters from being destroyed on config change
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return new Presenters(mainActivityPresenter, detailsPresenter, frontPagePresenter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isDetailsPageOpen()) {
                closeDetailsPage();
            } else {
                // ask if user wants to leave
                promptQuit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void openDetailsPage() {
        // switch to page 2 in viewpager
        pager.setCurrentItem(DETAILS_PAGE, true);
    }

    @Override
    public void closeDetailsPage() {
        pager.setCurrentItem(TOP_STORIES, true);
    }

    @Override
    public boolean isDetailsPageOpen() {
        return pager.getCurrentItem() == DETAILS_PAGE;
    }

    @Override
    public void promptQuit() {
        new AlertDialog.Builder(this)
                .setTitle("Quit")
                .setMessage("Are you sure you want to quit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    @Override
    protected void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onPageSelected(int position) {
        if (position == TOP_STORIES) {
            pager.setSwipingEnabled(false);
        } else if (position == DETAILS_PAGE) {
            pager.setSwipingEnabled(true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.theme_preference_key))) {
            recreate();
        }
    }

    // A POJO to hold presenters as they are retained in onRetainCustomNonConfigurationInstance
    public class Presenters {
        MainActivityContract.Presenter mainPresenter;
        DetailsContract.Presenter detailsPresenter;
        FrontPageContract.Presenter frontPagePresenter;

        public Presenters(MainActivityContract.Presenter mainPresenter, DetailsContract.Presenter detailsPresenter, FrontPageContract.Presenter frontPagePresenter) {
            this.mainPresenter = mainPresenter;
            this.detailsPresenter = detailsPresenter;
            this.frontPagePresenter = frontPagePresenter;
        }
    }
}
