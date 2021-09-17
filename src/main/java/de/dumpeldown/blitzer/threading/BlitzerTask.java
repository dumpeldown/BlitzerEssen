package de.dumpeldown.blitzer.threading;

import de.dumpeldown.blitzer.request.TwitterRequestManager;

import java.net.URL;
import java.util.TimerTask;

public class BlitzerTask extends TimerTask {
    private int numberOfTweets;
    public BlitzerTask(int numberOfTweets) {
        this.numberOfTweets = numberOfTweets;
    }

    @Override
    public void run() {
        TwitterRequestManager twitterRequestManager = new TwitterRequestManager("413192825", this.numberOfTweets);
        for (URL url : twitterRequestManager.getURLs()) {
            new BlitzerThread(url).start();
        }
    }
}
