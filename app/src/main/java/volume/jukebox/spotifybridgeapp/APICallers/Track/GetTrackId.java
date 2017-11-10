package volume.jukebox.spotifybridgeapp.APICallers.Track;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import volume.jukebox.spotifybridgeapp.Common.Constants;
import volume.jukebox.spotifybridgeapp.Common.HttpClient;
import volume.jukebox.spotifybridgeapp.Common.SessionSingleton;
import volume.jukebox.spotifybridgeapp.Common.Token;
import volume.jukebox.spotifybridgeapp.Common.Track;
import volume.jukebox.spotifybridgeapp.MainActivity;

import static volume.jukebox.spotifybridgeapp.MainActivity.mPlayer;

/**
 * Created by Mewantha.Bandara on 08/11/2017.
 */

public abstract class GetTrackId extends AsyncTask<Token, Void, Void> {

    public abstract void onError(Exception exception);

    @Override
    protected Void doInBackground(Token... tokens) {

        SendTrackRequest(tokens[0]);

        return null;

    }

    private void SendTrackRequest(Token token) {
        try {

            URL                                                 url                 = new URL(Constants.API_TRACK_URI + token.getToken());

            JSONObject                                          response            = HttpClient.get(url);

            SessionSingleton                                    session             = SessionSingleton.getInstanceOfObject();

            session.setTrack(deserializeResponse(response));

            String                                              trackId             = session.getTrack().getTrackId();

            com.spotify.sdk.android.player.PlaybackState        state               = MainActivity.mPlayer.getPlaybackState();

            if ( state.isPlaying ) {

                Spotify.destroyPlayer(this);

            }

            MainActivity.mPlayer.playUri(null, trackId, 0, 0);

        } catch (MalformedURLException e) {

            onError(e);

            e.printStackTrace();

        }
    }

    private Track deserializeResponse(JSONObject response){

        Gson        gson                            = new GsonBuilder().create();

        return gson.fromJson(response.toString(), Track.class);

    }

}
