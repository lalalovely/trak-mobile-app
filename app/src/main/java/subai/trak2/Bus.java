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

        destination = new Coordinates(String.valueOf(10.298432), String.valueOf(123.893072));
        position = new Coordinates("", "");
        busCompany = "";
        route = "";
        accommodation = "";
        status = "In-transit";
    }

    public void setPosition(String lat, String lng) {position = new Coordinates(lat,lng);}

    public void setAccommodation(String accommodation) {this.accommodation = accommodation;}

    public void setBusCompany(String busCompany) {
        this.busCompany = busCompany;
    }

    public void setRoute(String route) {
        this.route = route;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}

