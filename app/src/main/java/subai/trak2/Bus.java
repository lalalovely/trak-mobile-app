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

    private Coordinates position, destination;
    private String  busCompany, route, accommodation, status;

    Bus(){
        //dummy values
        destination = new Coordinates(String.valueOf(10.298237), String.valueOf(123.893133));
        position = new Coordinates("", "");
        busCompany = "Ceres";
        route = "Bato";
        accommodation = "Aircon";
        status = "In-transit";
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

    public String getBusCompany() { return busCompany; }

    public String getRoute() { return route; }

    public String getAccommodation() { return accommodation; }

    public String getStatus() { return status; }
}

