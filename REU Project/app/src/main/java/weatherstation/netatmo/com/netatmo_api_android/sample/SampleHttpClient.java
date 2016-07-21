package weatherstation.netatmo.com.netatmo_api_android.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import weatherstation.netatmo.com.netatmo_api_android.R;
import weatherstation.netatmo.com.netatmo_api_android.api.NetatmoHttpClient;
import weatherstation.netatmo.com.netatmo_api_android.api.NetatmoUtils;

/**
 * This is just an example of how you can extend NetatmoHttpClient.
 * Tokens are stored in the shared preferences of the app, but you can store them as you wish
 * as long as they are properly returned by the getters.
 * If you want to add your own '/getmeasure' requests, this is also the place to do it.
 */
public class SampleHttpClient extends NetatmoHttpClient {

    Context context;

    SharedPreferences mSharedPreferences;


    public SampleHttpClient(Context context) {
        super(context);
        this.context = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected String getClientId() {
        return "578cf5f2ea00a02b9e8b474c";
    }

    @Override
    protected String getClientSecret() {
        return "zCUatHsRJuk4O00Uh1ah9l1o3Rqi";
    }

    @Override
    protected String getAppScope() {
        return "read_station";
    }

    @Override
    protected void storeTokens(String refreshToken, String accessToken, long expiresAt) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(NetatmoUtils.KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(NetatmoUtils.KEY_ACCESS_TOKEN, accessToken);
        editor.putLong(NetatmoUtils.KEY_EXPIRES_AT, expiresAt);
        editor.apply();
    }

    @Override
    protected void clearTokens() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected String getRefreshToken() {
        return mSharedPreferences.getString(NetatmoUtils.KEY_REFRESH_TOKEN, null);
    }

    @Override
    protected String getAccessToken() {
        return mSharedPreferences.getString(NetatmoUtils.KEY_ACCESS_TOKEN,null);
    }

    @Override
    protected long getExpiresAt() {
        return mSharedPreferences.getLong(NetatmoUtils.KEY_EXPIRES_AT,0);
    }
}
