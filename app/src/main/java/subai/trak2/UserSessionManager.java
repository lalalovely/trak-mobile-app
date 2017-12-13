package subai.trak2;

import android.content.Context;
import android.content.SharedPreferences;

//UserSessionManager saves the important information such bus number, bus route, etc in the SharedPreferences
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

    //constructor
    public UserSessionManager(Context cont) {
        this.context = cont;
        prefs = context.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    //status includes emergencies
    public void setStatus(String stat) {
        editor.putString("stat", stat);
        editor.commit();
    }

    //returns the saved status of the bus
    public String getStatus() {
        return prefs.getString("stat", null);
    }

    //sets the position to be saved in the SharedPreferences
    public void setPosition(int pos) {
        editor.putInt(POSITION, pos);
        editor.commit();
    }

    //returns the position
    public int getPosition() {
       return prefs.getInt(POSITION, 0);
    }

    //sets the state of the spinner
    public void setSpinnerState(boolean state) {
        editor.putBoolean(SPINNERSTATE, state);
        editor.commit();
    }

    public void setHasSpinnerSelected(boolean hasSelected) {
        editor.putBoolean(KEY_SPINNER_SELECT, hasSelected);
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
}
