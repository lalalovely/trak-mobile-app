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
    private String  busCompany, route, accommodation;

    Bus(){
        //dummy values
        destination = new Coordinates(String.valueOf(10.295794), String.valueOf(123.894716));
        position = new Coordinates("", "");
        busCompany = "";
        route = "";
        accommodation = "";
    }

    //lat, lng, busNumber, route, accommodation, busCompany

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


}

