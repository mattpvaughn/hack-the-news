package io.github.httpmattpvaughn.hnapp.data;

import java.util.List;

import io.github.httpmattpvaughn.hnapp.data.model.Story;
import retrofit2.Call;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class HackerNewsAPI {

    public interface StoriesListener {
        void onStoriesLoaded(List<Story> Stories);
        void onStoriesLoadFailed(String message);
        void onStoryLoaded(Story contributor);
    }
}
