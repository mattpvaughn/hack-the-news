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

    // Not POJO stuff -> stuff for collapsable child view
    private List<Story> children;
    private boolean isGroup;
    private int groupSize;

    @Override
    public String toString() {
        return "Story{" +
                "title='" + title + '\'' +
                ", by='" + by + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", text='" + text + '\'' +
                ", kids=" + Arrays.toString(kids) +
                ", id=" + id +
                ", score=" + score +
                ", time=" + time +
                ", descendants=" + descendants +
                ", parent=" + parent +
                ", depth=" + depth +
                '}';
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
        this.children.add(story);
    }
}
