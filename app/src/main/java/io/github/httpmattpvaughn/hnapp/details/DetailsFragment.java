package io.github.httpmattpvaughn.hnapp.details;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import io.github.httpmattpvaughn.hnapp.R;
import io.github.httpmattpvaughn.hnapp.Util;
import io.github.httpmattpvaughn.hnapp.data.model.Story;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class DetailsFragment extends Fragment implements DetailsContract.View,
        BetterLinkMovementMethod.OnLinkClickListener,
        BetterLinkMovementMethod.OnLinkLongClickListener {

    private DetailsContract.Presenter presenter;
    private WebView webView;
    private SlidingUpPanelLayout slidingUpPanel;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private boolean isAttached = false;
    private boolean isViewCreated = false;
    private Story currentStory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getActivity() == null && getActivity() instanceof AppCompatActivity;
        webView = view.findViewById(R.id.article_web_view);
        setUpWebView();

        this.slidingUpPanel = view.findViewById(R.id.sliding_up_panel);
        setupSlidingPanel();

        this.commentsRecyclerView = view.findViewById(R.id.comment_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            // Sort of a hack- stops "Inconsistency Detected" error which occasionally
            // occurs after calling notifyItemInserted in adapter
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.commentsRecyclerView.setLayoutManager(layoutManager);

        this.isViewCreated = true;
        if (isAttached && isViewCreated) {
            addPresenterActions();
        }

//        SwipeRefreshLayout dragRefresh = view.findViewById(R.id.comments_drag_refresh);
//        dragRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadDiscussion(currentStory);
//            }
//        });
    }

    private void setupSlidingPanel() {
        this.slidingUpPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            // Offset is range [0-1f]  where 0 is collapsed, 1 is expanded
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                // show webview_bottom_bar or discussion_toolbar depending on whats available
                View discussionToolbar = slidingUpPanel.findViewById(R.id.discussion_toolbar);
                View webviewBottomBar = slidingUpPanel.findViewById(R.id.webview_bottom_bar);
                discussionToolbar.setVisibility(View.VISIBLE);
                webviewBottomBar.setVisibility(View.VISIBLE);
                discussionToolbar.setAlpha(1 - slideOffset);
                webviewBottomBar.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                // show webview_bottom_bar or discussion_toolbar depending on whats available
                View discussionToolbar = slidingUpPanel.findViewById(R.id.discussion_toolbar);
                View webviewBottomBar = slidingUpPanel.findViewById(R.id.webview_bottom_bar);
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    // show webview bottom bar
                    discussionToolbar.setVisibility(View.INVISIBLE);
                    webviewBottomBar.setVisibility(View.VISIBLE);
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    // show toolbar
                    discussionToolbar.setVisibility(View.VISIBLE);
                    webviewBottomBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        View openInBrowser = slidingUpPanel.findViewById(R.id.open_in_browser);
        updateWebViewState();
        openInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openLinkIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(webView.getUrl()));
                getActivity().startActivity(openLinkIntent);
            }
        });
    }

    private void setUpWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                return shouldOverrideUrlLoading(url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                Uri uri = request.getUrl();
                return shouldOverrideUrlLoading(uri.toString());
            }

            private boolean shouldOverrideUrlLoading(final String url) {
                webView.loadUrl(url);
                return false; // Returning True means that application wants to leave the current WebView and handle the url itself, otherwise return false.
            }
        });
        final ProgressBar webViewProgress = getActivity().findViewById(R.id.webview_progress);
        this.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                webViewProgress.setProgress(newProgress);
                if (newProgress == 100) {
                    webViewProgress.setVisibility(View.GONE);
                } else if (webViewProgress.getVisibility() != View.VISIBLE) {
                    webViewProgress.setVisibility(View.VISIBLE);
                }
                super.onProgressChanged(view, newProgress);
            }

        });
    }

    public void attachPresenter(final DetailsContract.Presenter presenter) {
        this.presenter = presenter;
        this.isAttached = true;

        addPresenterActions();
    }

    private void addPresenterActions() {
        if (isAttached && isViewCreated) {
            Toolbar discussionToolbar = slidingUpPanel.findViewById(R.id.discussion_toolbar);
            discussionToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back_white_24dp));
            discussionToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.closeDetailsPage();
                }
            });

            this.presenter.addView(this);
        }
    }

    @Override
    public void openArticle(String url) {
        if (slidingUpPanel.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
        this.webView.loadUrl(url);
        updateWebViewState();

        View discussionToolbar = slidingUpPanel.findViewById(R.id.discussion_toolbar);
        View webviewBottomBar = slidingUpPanel.findViewById(R.id.webview_bottom_bar);
        discussionToolbar.setVisibility(View.INVISIBLE);
        webviewBottomBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void openDiscussion(String url) {
        if (slidingUpPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
            slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        this.webView.loadUrl(url);
        updateWebViewState();

        View discussionToolbar = slidingUpPanel.findViewById(R.id.discussion_toolbar);
        View webviewBottomBar = slidingUpPanel.findViewById(R.id.webview_bottom_bar);
        discussionToolbar.setVisibility(View.VISIBLE);
        webviewBottomBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void addComments(List<Story> comments) {
        commentAdapter.addAll(comments);
    }

    @Override
    public void showCommentsLoading() {
        if (getActivity() != null) {
            ProgressBar progressBar = getActivity().findViewById(R.id.comments_progress);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideCommentsLoading() {
        if (getActivity() != null) {
            ProgressBar progressBar = getActivity().findViewById(R.id.comments_progress);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void setArticleViewLock(boolean isLocked) {
        slidingUpPanel.setTouchEnabled(isLocked);
    }

    // Set the preferred items from discussion page to match data stored in
    // item object
    @Override
    public void loadDiscussion(Story story) {
        this.currentStory = story;
        this.commentAdapter = new CommentAdapter(story, this, this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = commentsRecyclerView.getChildLayoutPosition(view);
                commentAdapter.toggleGroup(position);
            }
        });
        this.commentsRecyclerView.setAdapter(commentAdapter);
        TextView score = getView().findViewById(R.id.score);
        score.setText(String.valueOf(story.score));
        TextView author = getView().findViewById(R.id.author);
        author.setText(story.by);
        TextView title = getView().findViewById(R.id.title);
        title.setText(story.title);
        TextView url = getView().findViewById(R.id.url);
        url.setText(Util.beautifyUrl(story.url));
        TextView time = getView().findViewById(R.id.time);
        time.setText(Util.beautifyPostAge(story.time));
        TextView comments = getView().findViewById(R.id.comments);
        comments.setText(String.valueOf(story.descendants));

        if (story.text != null) {
            TextView content = getView().findViewById(R.id.text_content);
            content.setText(Util.stringToHtml(story.text));
            content.setVisibility(View.VISIBLE);
        } else {
            TextView content = getView().findViewById(R.id.text_content);
            content.setVisibility(View.GONE);
        }
    }

    @Override
    public void printErrorMessage(String string) {
        Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
    }

    // On link clicked
    @Override
    public boolean onClick(TextView textView, String url) {
        presenter.openLink(url);
        // return true so parent gets event as well
        return false;
    }

    private void updateWebViewState() {
        enableWebViewBackButton(webView.canGoBack());
        enableWebViewForwardsButton(webView.canGoForward());
    }

    private void enableWebViewForwardsButton(boolean enabled) {
        if (enabled) {
            View forwardsButton = slidingUpPanel.findViewById(R.id.forwards_button);
            forwardsButton.setAlpha(1f);
            forwardsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    webView.goForward();
                    updateWebViewState();
                }
            });
        } else {
            View forwardsButton = slidingUpPanel.findViewById(R.id.forwards_button);
            forwardsButton.setAlpha(.5f);
            forwardsButton.setOnClickListener(null);
        }
    }

    private void enableWebViewBackButton(boolean enabled) {
        if (enabled) {
            View backButton = slidingUpPanel.findViewById(R.id.back_button);
            backButton.setAlpha(1f);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    webView.goBack();
                    updateWebViewState();
                }
            });
        } else {
            View backButton = slidingUpPanel.findViewById(R.id.back_button);
            backButton.setAlpha(.5f);
            backButton.setOnClickListener(null);
        }
    }

    // action taken when a link in comments is longclicked
    @Override
    public boolean onLongClick(TextView textView, final String url) {
        final CharSequence[] actions = new CharSequence[]{
                "Open in browser",
                "Share",
                "Copy"
        };
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
        return false;
    }
}
