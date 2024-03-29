package volume.jukebox.spotifybridgeapp.APICallers.Authorization;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import volume.jukebox.spotifybridgeapp.Common.Constants;
import volume.jukebox.spotifybridgeapp.Common.Constants.*;
import volume.jukebox.spotifybridgeapp.Common.HttpClient;
import volume.jukebox.spotifybridgeapp.Common.SessionSingleton;
import volume.jukebox.spotifybridgeapp.Common.Token;
import volume.jukebox.spotifybridgeapp.Common.Track;

/**
 * Created by Mewantha.Bandara on 08/11/2017.
 */

public abstract class GetAuthenticationToken extends AsyncTask<Token, String, Void> {

    public abstract void onError(Exception exception);

    @Override
    protected Void doInBackground(Token... tokens) {

        SendAuthenticationRequest();

        return null;

    }

    private void SendAuthenticationRequest(){

        try {

            URL                     url             = new URL(Constants.API_AUTHORIZE_URI);

            JSONObject              response        = HttpClient.get(url);

            SessionSingleton        session         = SessionSingleton.getInstanceOfObject();

            session.setToken(deserializeResponse(response));

        } catch (MalformedURLException e) {

            onError(e);

            e.printStackTrace();

        }
    }

    private Token deserializeResponse(JSONObject response){

        Gson                    gson                    = new GsonBuilder().create();

        return gson.fromJson(response.toString(), Token.class);

    }

}

