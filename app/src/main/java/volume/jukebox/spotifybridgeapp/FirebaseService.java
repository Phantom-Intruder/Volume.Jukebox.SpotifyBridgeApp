package volume.jukebox.spotifybridgeapp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.spotify.sdk.android.player.Spotify;

import static android.content.ContentValues.TAG;

public class FirebaseService extends FirebaseMessagingService {
    public FirebaseService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "ABC123: " + remoteMessage.getFrom());

        com.spotify.sdk.android.player.PlaybackState state = MainActivity.mPlayer.getPlaybackState();

        if ( state.isPlaying ) {

            Spotify.destroyPlayer(this);

        }

        MainActivity.mPlayer.playUri(null, remoteMessage.getNotification().getBody(), 0, 0);


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            Log.e(TAG, "ABC123: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
