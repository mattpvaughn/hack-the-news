package io.github.httpmattpvaughn.hnapp.data;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class Injection {
    public static StoryRepository provideStoryRepository() {
        return new StoryManager();
    }
}
