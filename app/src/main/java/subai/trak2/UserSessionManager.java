package subai.trak2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.style.BulletSpan;
import android.util.Log;

public class UserSessionManager {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context context;
    private final static String MYPREFERENCES = "MyPrefs";
    private static final String IS_USER_LOGGEDIN = "IsUserLoggedIn";
    public static final String KEY_BUS_NUM = "bus_number";
    public static final String KEY_ROUTE = "bus_route";
    public static final String KEY_STARTED = "hasStarted";
    public static final String KEY_SPINNER_SELECT = "selectedSpinner";
    public static final String POSITION = "spinnerPosition";
    public static final String SPINNERSTATE = "spinnerState";
    public static final String BUS_COMPANY = "busCompany";
    public static final String ACCOMODATION = "busAccommodation";
    public static final String LASTTRIP = "lasttrip";
    public static final String STOPPED = "stop";

    public UserSessionManager(Context cont) {
        this.context = cont;
        prefs = context.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setIsLastTrip(boolean lastTrip) {
        editor.putBoolean(LASTTRIP, lastTrip);
        editor.commit();
    }

    public boolean getIsLastTrip() {
        return prefs.getBoolean(LASTTRIP, false);
    }

    public void setIsStop(boolean stop) {
        editor.putBoolean(STOPPED, stop);
        editor.commit();
    }

    public boolean getIsStop() {
        return prefs.getBoolean(STOPPED, false);
    }

    public void createUserLoginSession(String bus_num){
        editor.putBoolean(IS_USER_LOGGEDIN, true);
        editor.putString(KEY_BUS_NUM, bus_num);
        editor.commit();
    }

    public void setPosition(int pos) {
        editor.putInt(POSITION, pos);
        editor.commit();
    }

    public int getPosition() {
       return prefs.getInt(POSITION, 0);
    }

    public void setStop(boolean stop) {
        editor.putBoolean("stopped", stop);
        editor.commit();
    }

    public boolean getStop() {
        return prefs.getBoolean("stopped", false);
    }

    public void setSpinnerState(boolean state) {
        editor.putBoolean(SPINNERSTATE, state);
        editor.commit();
    }

    public boolean getSpinnerState() {
        return prefs.getBoolean(SPINNERSTATE, false);
    }

    public void setHasSpinnerSelected(boolean hasSelected) {
        editor.putBoolean(KEY_SPINNER_SELECT, hasSelected);
        editor.commit();
    }

    public boolean hasSpinnerSelected() {
        return prefs.getBoolean(KEY_SPINNER_SELECT, false);
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

    public void setBusCompany(String bus_Comp){
        editor.putString(BUS_COMPANY, bus_Comp);
        editor.commit();
    }

    public String getBusCompany(){
        return prefs.getString(BUS_COMPANY, null);
    }

    public void setAccomodation(String accomodation){
        editor.putString(ACCOMODATION, accomodation);
        editor.commit();
    }

    public String getAccomodation(){
        return prefs.getString(ACCOMODATION, null);
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
