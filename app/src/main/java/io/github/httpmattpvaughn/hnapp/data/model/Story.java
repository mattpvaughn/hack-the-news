package io.github.httpmattpvaughn.hnapp.data.model;

import com.oissela.software.multilevelexpindlistview.MultiLevelExpIndListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class Story implements MultiLevelExpIndListAdapter.ExpIndData {
    public String title;
    public String by;
    public String url;
    public String type;
    public String text;
    public int[] kids;
    public int id;
    public int score;
    public int time;
    public int descendants;
    public int parent;
    public int depth;

    // "job", "story", "comment", "poll", or "pollopt"
    private String TYPE_STORY = "story";

    // Not POJO stuff -> stuff for collapsable child view
    private List<Story> children = new ArrayList<Story>();
    private boolean isGroup;
    private int groupSize;

    @Override
    public String toString() {
        return by;
//        return "Story{" +
//                "title='" + title + '\'' +
//                ", by='" + by + '\'' +
//                ", url='" + url + '\'' +
//                ", type='" + type + '\'' +
//                ", text='" + text + '\'' +
//                ", kids=" + Arrays.toString(kids) +
//                ", id=" + id +
//                ", score=" + score +
//                ", time=" + time +
//                ", descendants=" + descendants +
//                ", parent=" + parent +
//                ", depth=" + depth +
//                '}';
    }

    public Story copy() {
        Story copy = new Story();
        copy.id = this.id;
        copy.by = this.by;
        copy.url = this.url;
        copy.type = this.type;
        copy.text = this.text;
        copy.kids = Arrays.copyOf(this.kids, this.kids.length);
        copy.score = this.score;
        copy.time = this.time;
        copy.depth = this.depth;
        copy.descendants = this.descendants;
        copy.parent = this.parent;
        return copy;
    }

    @Override
    public List<? extends MultiLevelExpIndListAdapter.ExpIndData> getChildren() {
        return children;
    }

    // Everything with childern is a group
    @Override
    public boolean isGroup() {
        return isGroup;
    }

    @Override
    public void setIsGroup(boolean value) {
        this.isGroup = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Story story = (Story) o;

        return id == story.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void addChild(Story story) {
        if (children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(this.children.size(), story);
    }

    public boolean isStory() {
        return type.equals(TYPE_STORY);
    }
}
