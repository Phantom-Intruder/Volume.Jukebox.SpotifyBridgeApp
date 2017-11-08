package volume.jukebox.spotifybridgeapp.Common;

/**
 * Created by Mewantha.Bandara on 06/11/2017.
 */

public class Constants {

    //API URL's
    public static final String      CLIENT_ID                  = "829b5a90c4ea48a7816965302b8124cc";

    public static final String      REDIRECT_URI               = "http://172.18.0.118/Volume.Jukebox.Web/";

    public static final String      API_URI                    = "http://172.18.0.118/Volume.Jukebox.API/api/";

    public static final String      TRACK_URI                  = "Track?authorizationToken=BQC7-SvMoUm_pL0lnp6mK_AwUrTzNZ_FvyERv-mO4SoaxqfA6dt-1VKolB_2PqYvy1wfipGK0skbgo3hSSvqrw";

    public static final String      AUTHORIZE_URI              = "Authorize";

    public static final String      API_TRACK_URI              = API_URI + TRACK_URI;

    public static final String      API_AUTHORIZE_URI          = API_URI + AUTHORIZE_URI;

    //Scope constants
    public static final String      SCOPES_READ                = "user-read-private";

    public static final String      SCOPES_STREAM              = "streaming";

    //Spotify app ints
    public static final int         UPDATE_STREAM_TIME         = 10000;

    public static final int         REQUEST_CODE               = 1337;

}
