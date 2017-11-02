package subai.trak2;

/**
 * Created by - on 30/10/2017.
 */

public class Bus {
    static class Coordinates {
        public String lat, lng;

        Coordinates(String lat, String lon){
            this.lat = lat;
            this.lng = lon;
        }
    }

    public Coordinates position, destination;
    public String  busCompany, route, accommodation, status;

    Bus(){
        //dummy values
        destination = new Coordinates(String.valueOf(10.298237), String.valueOf(123.893133));
        position = new Coordinates("", "");
        busCompany = "Ceres";
        route = "Bato";
        accommodation = "Aircon";
        status = "In-transit";
    }


    public void setBusDetails(String lat, String lng, String route, String acc, String busComp){
        destination = new Coordinates(String.valueOf(10.298237), String.valueOf(123.893133));
        position = new Coordinates(lat,lng);
        this.route = route;
        this.accommodation = acc;
        this.busCompany = busComp;
    }

    //lat, lng, busNumber, route, accommodation, busCompany

    public void setStatus(String st) {
        status = st;
    }
    public void setPosition(String lat, String lng) {position = new Coordinates(lat,lng);}
    public void setAccommodation(String accommodation) {this.accommodation = accommodation;}

    public void setBusCompany(String busCompany) {
        this.busCompany = busCompany;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}

