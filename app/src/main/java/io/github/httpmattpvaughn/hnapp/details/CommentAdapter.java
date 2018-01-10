package io.github.httpmattpvaughn.hnapp.details;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.oissela.software.multilevelexpindlistview.MultiLevelExpIndListAdapter;

import java.util.regex.Pattern;

import io.github.httpmattpvaughn.hnapp.R;
import io.github.httpmattpvaughn.hnapp.Util;
import io.github.httpmattpvaughn.hnapp.data.model.Story;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class CommentAdapter extends MultiLevelExpIndListAdapter<CommentAdapter.ViewHolder> {

    private final View.OnClickListener onClickListener;
    private final Link.OnLongClickListener onLinkLongClickListener;
    private int[] colorDepthArr = new int[]{
            0xFFFFEB3B,
            0xFFFFC107,
            0xFFFF9800,
            0xFFFF5722,
            0xFFF44336,
            0xFF673AB7,
            0xFF3F51B5
    };
    private Link.OnClickListener onLinkClickListener;

    public CommentAdapter(Link.OnClickListener onLinkClickListener,
                          Link.OnLongClickListener onLinkLongClickListener,
                          View.OnClickListener onClickListener) {
        this.onLinkClickListener = onLinkClickListener;
        this.onLinkLongClickListener = onLinkLongClickListener;
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View storyItem = inflater.inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(storyItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).setComment((Story) getItemAt(position));
    }

    public void addComment(Story comment, Story parent) {
        if (comment.parent == 0) {
            System.out.println("adding root");
            add(comment);
        } else {
            System.out.println("adding child");
            int parentPosition = indexOf(parent);
            System.out.println("parentPosition is " + parentPosition);
            parent.addChild(comment);
            insert(parentPosition + 1, comment);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView collapsedCommentCounter;
        public View root;
        public TextView author;
        public TextView time;
        public TextView text;
        public View depthMarker;
        public Story story;

        // From https://github.com/klinker24/Android-TextView-LinkBuilder
        // and https://github.com/klinker24/Talon-for-Twitter/
        public final Pattern WEB_URL_PATTERN
                = Pattern.compile(
                "((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                        + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                        + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
                        + "((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+"   // named host
                        + "(?:"   // plus top level domain
                        + "(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
                        + "|(?:biz|b[abdefghijmnorstvwyz])"
                        + "|(?:cat|com|coop|c[acdfghiklmnoruvxyz])"
                        + "|d[ejkmoz]"
                        + "|(?:edu|e[cegrstu])"
                        + "|f[ijkmor]"
                        + "|(?:gov|g[abdefghilmnpqrstuwy])"
                        + "|h[kmnrtu]"
                        + "|(?:info|int|i[delmnoqrst])"
                        + "|(?:jobs|j[emop])"
                        + "|k[eghimnrwyz]"
                        + "|l[abcikrstuvy]"
                        + "|(?:mil|mobi|museum|m[acdghklmnopqrstuvwxyz])"
                        + "|(?:name|net|n[acefgilopruz])"
                        + "|(?:org|om)"
                        + "|(?:pro|p[aefghklmnrstwy])"
                        + "|qa"
                        + "|r[eouw]"
                        + "|s[abcdeghijklmnortuvyz]"
                        + "|(?:tel|travel|t[cdfghjklmnoprtvwz])"
                        + "|u[agkmsyz]"
                        + "|v[aceginu]"
                        + "|w[fs]"
                        + "|y[etu]"
                        + "|z[amw]))"
                        + "|(?:(?:25[0-5]|2[0-4]" // or ip address
                        + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]"
                        + "|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]"
                        + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                        + "|[1-9][0-9]|[0-9])))"
                        + "|\\.\\.\\."
                        + "(?:\\:\\d{1,5})?)" // plus option port number
                        + "(\\/(?:(?:[a-zA-Z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
                        + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
                        + "(?:\\b|$)"); // and finally, a word boundary or end of
        // input.  This is to stop foo.sure from
        // matching as foo.su

        public ViewHolder(View itemView) {
            super(itemView);
            this.root = itemView.findViewById(R.id.comment_item);
            this.author = itemView.findViewById(R.id.author);
            this.time = itemView.findViewById(R.id.time);
            this.text = itemView.findViewById(R.id.text);
            this.depthMarker = itemView.findViewById(R.id.depth_marker);
            this.collapsedCommentCounter = itemView.findViewById(R.id.collapsed_comment_counter);
        }

        // Do it this way so we have an easy way to handle different types
        public void setComment(final Story comment) {
            long startTime = System.currentTimeMillis();
            this.story = comment;
            if (comment.by != null) {
                this.author.setText(comment.by);
            } else {
                this.author.setText("deleted");
            }
            TypedValue value = new TypedValue();
            Resources.Theme theme = text.getContext().getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, value, true);
            int textColor = value.data;
            theme.resolveAttribute(R.attr.colorPrimaryDark, value, true);
            int secondaryTextColor = value.data;
            this.time.setText(Util.beautifyPostAge(comment.time));
            if (comment.text != null) {
                this.text.setText(Util.stringToHtml(comment.text));
                Link link = new Link(WEB_URL_PATTERN)
                        .setTextColor(textColor)
                        .setTextColorOfHighlightedLink(secondaryTextColor)
                        .setOnClickListener(onLinkClickListener)
                        .setOnLongClickListener(onLinkLongClickListener);
                LinkBuilder.on(this.text)
                        .addLink(link)
                        .build();
            } else {
                this.text.setText("[Deleted]");
            }

            int indentSize = (int) this.root.getContext().getResources().getDimension(R.dimen.depth_marker_width);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) this.root.getLayoutParams();
            if (comment.depth == 0) {
                this.depthMarker.setVisibility(View.GONE);
            } else {
                this.depthMarker.setVisibility(View.VISIBLE);
            }
            params.leftMargin = indentSize * (comment.depth - 1);
            this.root.setLayoutParams(params);

            // Set marker color depending on depth
            this.depthMarker.setBackgroundColor(colorDepthArr[comment.depth % colorDepthArr.length]);

            // Hack alert! Use this until we figure out BetterLinkMovementMethod stealing onClicks from parents
            this.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(root);
                }
            });
            this.root.setOnClickListener(onClickListener);

            if (comment.isGroup()) {
                this.collapsedCommentCounter.setVisibility(View.VISIBLE);
                this.collapsedCommentCounter.setText(Integer.toString(comment.getGroupSize()));
            } else {
                this.collapsedCommentCounter.setVisibility(View.INVISIBLE);
            }

            Log.i("TAG", "bindView time: " + (System.currentTimeMillis() - startTime));
        }
    }
}
