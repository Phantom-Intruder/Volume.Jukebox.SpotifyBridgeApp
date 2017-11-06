package volume.jukebox.spotifybridgeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import static android.content.ContentValues.TAG;
import static volume.jukebox.spotifybridgeapp.Common.Constants.*;

public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{

    public static Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.e(TAG, "ASDASDD "+ FirebaseInstanceId.getInstance().getToken());

        AuthenticationRequest.Builder       builder         = new AuthenticationRequest.Builder(CLIENT_ID,
                                                                                                AuthenticationResponse.Type.TOKEN,
                                                                                                REDIRECT_URI);

        builder.setScopes(new String[]{SCOPES_READ, SCOPES_STREAM});

        AuthenticationRequest               request         = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

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
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
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

            default:
                break;

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
            final Handler           handler         = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Metadata        metadata        = mPlayer.getMetadata();

                    String          artistName      = metadata.currentTrack.name;

                    TextView        textView        = findViewById(R.id.textView);

                    textView.setText(artistName);
                }
            }, UPDATE_STREAM_TIME);
        }
    }

    public void stopStream(View view) {

        mPlayer.pause(null);

        mPlayer.destroy();

    }
}