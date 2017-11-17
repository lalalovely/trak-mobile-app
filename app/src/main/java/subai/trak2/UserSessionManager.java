package subai.trak2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.style.BulletSpan;

public class UserSessionManager {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context context;
    private final static String MYPREFERENCES = "MyPrefs";
    private static final String IS_USER_LOGGEDIN = "IsUserLoggedIn";
    public static final String KEY_BUS_NUM = "bus_number";
    public static final String KEY_ROUTE = "bus_route";
    public static final String KEY_STARTED = "hasStarted";

    public UserSessionManager(Context cont) {
        this.context = cont;
        prefs = context.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createUserLoginSession(String bus_num){
        editor.putBoolean(IS_USER_LOGGEDIN, true);
        editor.putString(KEY_BUS_NUM, bus_num);
        editor.commit();
    }

    public void setHasStarted(boolean hasStarted) {
        editor.putBoolean(KEY_STARTED, hasStarted);
        editor.commit();
    }

    public boolean hasStarted() {
        return prefs.getBoolean(KEY_STARTED, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        editor.putBoolean(IS_USER_LOGGEDIN, loggedIn);
        editor.commit();
    }

    public void setRoute(String r) {
        editor.putString(KEY_ROUTE, r);
        editor.commit();
    }

    public void setBusNumber(String busNum) {
        editor.putString(KEY_BUS_NUM, busNum);
        editor.commit();
    }

    public boolean isUserLoggedIn(){
        return prefs.getBoolean(IS_USER_LOGGEDIN, false);
    }

    public String getBusNum() {
        return prefs.getString(KEY_BUS_NUM, null);
    }

    public String getRoute() {
        return prefs.getString(KEY_ROUTE, null);
    }

    public boolean checkLogin(){
        if(!this.isUserLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(context, LoginActivity.class);
            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            context.startActivity(i);
            return true;
        }
        return false;
    }

    public String getUser() {
        return prefs.getString(KEY_BUS_NUM, null);
    }

    public void logoutUser(){
        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();
        // After logout redirect user to Login Activity
        Intent i = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        context.startActivity(i);
    }

}
