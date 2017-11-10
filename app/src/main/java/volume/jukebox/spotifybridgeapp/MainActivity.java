package volume.jukebox.spotifybridgeapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.Timer;
import java.util.TimerTask;

import volume.jukebox.spotifybridgeapp.APICallers.Authorization.GetAuthenticationToken;
import volume.jukebox.spotifybridgeapp.APICallers.Track.GetTrackId;
import volume.jukebox.spotifybridgeapp.Common.Constants;
import volume.jukebox.spotifybridgeapp.Common.SessionSingleton;

import static android.content.ContentValues.TAG;
import static volume.jukebox.spotifybridgeapp.Common.Constants.*;

public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{

    public static           Player          mPlayer;
    private Timer           timer               = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startTokenTimer();

        Log.e(TAG, "ASDASDD "+ FirebaseInstanceId.getInstance().getToken());

        AuthenticationRequest.Builder       builder         = new AuthenticationRequest.Builder(CLIENT_ID,
                                                                                                AuthenticationResponse.Type.TOKEN,
                                                                                                REDIRECT_URI);

        builder.setScopes(new String[]{SCOPES_READ, SCOPES_STREAM});

        AuthenticationRequest               request         = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    private void startTokenTimer() {

        TimerTask       hourlyTask          = new TimerTask() {

            @SuppressLint("StaticFieldLeak")
            @Override
            public void run () {

                new GetAuthenticationToken() {

                    @Override
                    public void onError(Exception exception) {

                    }

                }.execute();

            }

        };

        timer.schedule (hourlyTask, 0L, Constants.UPDATE_TOKEN_TIME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {

            AuthenticationResponse      response            = AuthenticationClient.getResponse(resultCode, intent);

            if (response.getType() == AuthenticationResponse.Type.TOKEN) {

                Config                  playerConfig        = new Config(this, response.getAccessToken(), CLIENT_ID);

                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {

                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {

                        mPlayer                             = spotifyPlayer;

                        mPlayer.addConnectionStateCallback(MainActivity.this);

                        mPlayer.addNotificationCallback(MainActivity.this);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "Could not initialize player: " + throwable.getMessage());
                        Toast.makeText(getApplicationContext(), "No work", Toast.LENGTH_SHORT).show();
                    }

                });

            }

        }

    }

    @Override
    protected void onDestroy() {

        // VERY IMPORTANT! This must always be called or else you will leak resources

        Spotify.destroyPlayer(this);

        super.onDestroy();

    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {

        Log.d("MainActivity", "Playback event received: " + playerEvent.name());

        switch (playerEvent) {

            // Handle event type as necessary
            case kSpPlaybackNotifyPlay:

                setMetadata();

            case kSpPlaybackNotifyAudioDeliveryDone:
                startNewSongTimer();

            default:

                break;

        }

    }

    private void startNewSongTimer() {

        TimerTask       songTask          = new TimerTask() {

            @SuppressLint("StaticFieldLeak")
            @Override
            public void run () {

                startNewSong();
                setMetadata();

            }

        };

            timer.schedule(songTask, 0L, SessionSingleton.getInstanceOfObject().getTrack().getTrackDuration());

    }

    @SuppressLint("StaticFieldLeak")
    public static void startNewSong() {

        if (!mPlayer.getPlaybackState().isPlaying){

            //Do request to API and receive track ID as response
            String s = SessionSingleton.getInstanceOfObject().getToken().getToken();

            new GetTrackId() {

                @Override

                public void onError(Exception exception) {

                    exception.printStackTrace();

                }

            }.execute(SessionSingleton.getInstanceOfObject().getToken());

        }

    }

    @Override
    public void onPlaybackError(Error error) {

        Log.d("MainActivity", "Playback error received: " + error.name());

        switch (error) {
            // Handle error type as necessary
            default:
                break;

        }

    }

    @Override
    public void onLoggedIn() {

        Log.d(TAG, "User logged in");

    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error i) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    public void setMetadata() {


        Metadata metadata = mPlayer.getMetadata();

        try{

            String                  artistName      = metadata.currentTrack.name;

            TextView                textView        = findViewById(R.id.textView);

            textView.setText(artistName);

        }catch (Exception e){

            final Handler           handler         = new Handler(Looper.getMainLooper());

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Metadata        metadata        = mPlayer.getMetadata();

                            String          artistName      = metadata.currentTrack.name;

                            Log.e(TAG, "sounds of "+ artistName);

                            TextView        textView        = findViewById(R.id.textView);

                            textView.setText(artistName);

                        }
                    });


                }

            }, UPDATE_STREAM_TIME);

        }

    }

    public void stopStream(View view) {

        mPlayer.pause(null);

        mPlayer.destroy();

    }
}