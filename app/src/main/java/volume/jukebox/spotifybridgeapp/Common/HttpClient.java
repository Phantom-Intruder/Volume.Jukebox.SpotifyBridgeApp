package volume.jukebox.spotifybridgeapp.Common;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Sahan.Ratnayake on 02/08/2017.
 */

public class HttpClient {

    public static JSONObject get(URL url) {

        try {
            HttpURLConnection       client              = getClient(url);

            int                     responseCode        = client.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream         in                  = new BufferedInputStream(client.getInputStream());
                String              inString            = new Scanner(in).useDelimiter("\\A").next();

                return new JSONObject(inString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getList(URL url) {

        try {
            HttpURLConnection       client              = getClient(url);

            int                     responseCode        = client.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream         in                   = new BufferedInputStream(client.getInputStream());
                String              responseString       = new Scanner(in).useDelimiter("\\A").next();

                Log.e("GetResponse", responseString);
                return new JSONArray(responseString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject post(URL url, JSONObject data) throws IOException {

        try {
            HttpURLConnection       client              = postClient(url, data);

            int                     responseCode        = client.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream         in                  = new BufferedInputStream(client.getInputStream());
                String              responseString      = new Scanner(in).useDelimiter("\\A").next();

                Log.e("(" + responseCode + ")PostResponse", responseString);
                return new JSONObject(responseString);
            }else{
                Log.e("PostResponse", "Code - " + responseCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray postList(URL url, JSONObject data) throws IOException {

        try {
            HttpURLConnection       client              = postClient(url, data);

            int                     responseCode        = client.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream         in                  = new BufferedInputStream(client.getInputStream());
                String              responseString      = new Scanner(in).useDelimiter("\\A").next();
                return new JSONArray(responseString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection postClient(URL url, JSONObject data) throws IOException {

        HttpURLConnection       client      = (HttpURLConnection) url.openConnection();

        client.setRequestMethod("POST");
        client.setDoInput(true);
        client.setDoOutput(true);
        client.setRequestProperty("Content-Type", "application/json");
        client.setRequestProperty("Accept", "application/json");

        OutputStream            os          = client.getOutputStream();
        OutputStreamWriter      writer      = new OutputStreamWriter(client.getOutputStream());
        writer.write(data.toString());
        writer.flush();
        writer.close();
        os.close();

        return client;
    }

    private static HttpURLConnection getClient(URL url) throws IOException {

        HttpURLConnection       client      = (HttpURLConnection) url.openConnection();

        client.setRequestMethod("GET");
        client.setDoInput(true);
        client.setRequestProperty("Content-Type", "application/json");
        client.setRequestProperty("Accept", "application/json");

        return client;
    }
}
