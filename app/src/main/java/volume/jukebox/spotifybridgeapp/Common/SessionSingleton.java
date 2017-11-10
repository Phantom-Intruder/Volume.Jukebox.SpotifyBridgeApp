package volume.jukebox.spotifybridgeapp.Common;

/**
 * Created by Mewantha.Bandara on 09/11/2017.
 */

public class SessionSingleton {

    private  static         Token                   token;

    private  static         Track                   track;

    private  static         SessionSingleton        instanceOfObject;

    private SessionSingleton(){

    }

    public static synchronized SessionSingleton getInstanceOfObject(){

        if (instanceOfObject == null)

            instanceOfObject        =       new SessionSingleton();

        return instanceOfObject;

    }

    public Track getTrack() {

        return track;

    }

    public void setTrack(Track track) {

        SessionSingleton.track      = null;

        SessionSingleton.track      = track;

    }


    public Token getToken() {

        return token;

    }

    public void setToken(Token token) {

        SessionSingleton.token      = null;

        SessionSingleton.token      = token;

    }

    public void destroyInstance(){

        instanceOfObject = null;

    }
}
