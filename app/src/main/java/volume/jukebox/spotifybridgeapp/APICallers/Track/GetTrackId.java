package volume.jukebox.spotifybridgeapp.APICallers.Track;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import volume.jukebox.spotifybridgeapp.Common.Constants;
import volume.jukebox.spotifybridgeapp.Common.HttpClient;
import volume.jukebox.spotifybridgeapp.Common.Track;

/**
 * Created by Mewantha.Bandara on 08/11/2017.
 */

public abstract class GetTrackId extends AsyncTask<Track, String, Void> {

    public abstract void onError(Exception exception);

    @Override
    protected Void doInBackground(Track... tracks) {

        SendTrackRequest();

        return null;

    }

    private void SendTrackRequest() {
        try {

            URL             url             = new URL(Constants.API_TRACK_URI);

            JSONObject      response        = HttpClient.get(url);

            String          trackId         = deserializeResponse(response);



        } catch (MalformedURLException e) {

            onError(e);

            e.printStackTrace();

        }
    }

    private String deserializeResponse(JSONObject response){

        Gson        gson                            = new GsonBuilder().create();

        Track       track                            = gson.fromJson(response.toString(), Track.class);

        return track.getTrack();

    }

}
