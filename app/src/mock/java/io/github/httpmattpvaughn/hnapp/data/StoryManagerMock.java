package io.github.httpmattpvaughn.hnapp.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.httpmattpvaughn.hnapp.data.model.Story;

/**
 * Creates fake data to populate the app with in mock builds
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class StoryManagerMock implements StoryRepository {

    private final int NUM_STORIES = 500;
    private final Integer[] STORY_IDS = new Integer[500];
    private static final int STORIES_PER_PAGE = 25;

    @Override
    public void getStoryIdArray(@NonNull GetStoryIdsCallback callback) {
        callback.onPostsLoaded(STORY_IDS);
    }

    @Override
    public void getStoryList(@NonNull GetStoryListCallback callback) {
        callback.onPostsLoaded(getFakeStoryList());
    }

    @Override
    public void getCommentsList(@NonNull GetCommentsListCallback callback, Story parent) {
//        TODO- method stub
        List<Story> comments = new ArrayList<>(getFakeCommentsList());
        callback.onCommentsLoaded(comments, getFakeStory());
    }

    @Override
    public void loadCommentsIndividually(@NonNull LoadCommentsIndividuallyCallback callback, Story parent, int depth) {
//        TODO- method stub
        List<Story> comments = new ArrayList<>(getFakeCommentsList());
        callback.onCommentsLoad(comments, comments);
    }

    @Override
    public void resetStoriesLoadedCount() {
//        TODO- method stub
    }

    public static Story getFakeStory() {
        Story story = new Story();
        story.type = "story";
        story.url = "https://google.com";
        story.by = "matt";
        story.descendants = 25;
        story.id = -1;
        story.score = 123;
        story.text = "Uh... I don't sing the song. Other people do. This is ve" +
                "ry traditional in tv and radio. Gilligan doesn't sing Gilligan'" +
                "s Island theme song, for example. <a href=\"https://google.com\">https://google.com</a> or Saturday" +
                " Night Live, a narrator introduces the host and the casts. Carry on.";
        story.title = "Scientists discover that there are more stars in the sky than atoms in the universe";
        story.time = (int) ((System.currentTimeMillis() - 10000) / 1000F);
        return story;
    }

    public static List<Story> getFakeStoryList() {
        List<Story> storyList = new ArrayList<>();
        for (int i = 0; i < STORIES_PER_PAGE; i++) {
            Story story = getFakeStory();
            if(i % 2 == 0) {
                story.title = "This is a different story title, everything else is the same. There are";
            }
            storyList.add(story);
        }
        return storyList;
    }

    public static List<Story> getFakeCommentsList() {
        Story story1 = new Story();
        story1.by = "mattoo";
        story1.text = "root";
        story1.id = 1;
        story1.parent = -1;

        Story story1a = new Story();
        story1a.by = "matt";
        story1a.text = "child- level 1: <a href=\"https://isitchristmas.com/\">isitchristmas.com</a>";
        story1a.id = 2;
        story1a.depth = 1;
        story1a.setIsByOp(true);
        story1a.parent = story1.id;
        story1.kids = new int[]{story1a.id};
        story1.addChild(story1a);

        Story story1b = new Story();
        story1b.by = "gumbolaya";
        story1b.text = "child- level 2";
        story1b.id = 3;
        story1b.depth = 2;
        story1b.parent = story1a.id;
        story1a.kids = new int[]{story1b.id};
        story1a.addChild(story1b);

        Story story2 = new Story();
        story2.by = "mattoo";
        story2.text = "gucci gang gucci gang gucci gang gucci gang gucci gang" +
                " gucci gang gucci gang gucci gang gucci gang gucci gang gucci" +
                " gang gucci gang gucci gang gucci gang gucci gang gucci gang" +
                " gang gucci gang gucci gang gucci gang gucci gang gucci gang" +
                " gang gucci gang gucci gang gucci gang gucci gang gucci gang" +
                " gang gucci gang gucci gang gucci gang gucci gang gucci gang" +
                " gucci gang gucci gang gucci gang gucci gang ";
        story2.id = 4;
        story2.parent = -1;

        Story story3 = new Story();
        story3.by = "salumi";
        story3.text = "gucci gang gucci gang gucci gang gucci gang gucci gang" +
                " gucci gang gucci gang gucci gang gucci gang gucci gang gucci" +
                " gang gucci gang gucci gang gucci gang gucci gang gucci gang" +
                " gang gucci gang gucci gang gucci gang gucci gang gucci gang" +
                " gang gucci gang gucci gang gucci gang gucci gang gucci gang" +
                " gang gucci gang gucci gang gucci gang gucci gang gucci gang" +
                " gucci gang gucci gang gucci gang gucci gang ";
        story1b.id = 5;
        story3.parent = -1;
        return Arrays.asList(story1, story1a, story1b, story2, story3);
    }
}
