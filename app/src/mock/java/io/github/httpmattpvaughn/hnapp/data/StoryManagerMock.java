package io.github.httpmattpvaughn.hnapp.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
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
        List<Story> comments = new ArrayList<>(getFakeStoryList());
        callback.onCommentsLoaded(comments, getFakeStory());
    }

    @Override
    public void loadCommentsIndividually(@NonNull LoadCommentsIndividuallyCallback callback, Story parent, int depth) {
//        TODO- method stub
        List<Story> comments = new ArrayList<>(getFakeStoryList());
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
        story.score = 123;
        story.text = "Uh... I don't sing the song. Other people do. This is ve" +
                "ry traditional in tv and radio. Gilligan doesn't sing Gilligan'" +
                "s Island theme song, for example. On The Late Show or Saturday" +
                " Night Live, a narrator introduces the host and the casts. Carry on.";
        story.kids = new int[]{-1, -2, -3, -4, -5};
        story.title = "Scientists discover that there are more stars in the sky than atoms in the universe";
        story.time = (int) ((System.currentTimeMillis() - 10000) / 1000F);
        return story;
    }

    public static List<Story> getFakeStoryList() {
        List<Story> storyList = new ArrayList<>();
        for (int i = 0; i < STORIES_PER_PAGE; i++) {
            storyList.add(getFakeStory());
        }
        return storyList;
    }
}
