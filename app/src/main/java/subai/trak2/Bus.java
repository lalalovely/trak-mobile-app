package subai.trak2;

/**
 * Created by lovely grace on 10/30/2017.
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
    public String busNumber, busCompany, route, accommodation, status;

//    Bus(String busName, String route, String accomodation, String status, String x1, String y1, String x2, String y2) {
//        destination = new com.example.dcs_madl09.location.Bus.Coordinates(x1, y1);
//        position = new com.example.dcs_madl09.location.Bus.Coordinates(x2,y2);
//
//        this.busName = busName;
//        this.route = route;
//        this.status = status;
//        this.accomodation = accomodation;
//    }

    Bus(String x2, String y2, String busNumber, String route, String accommodation, String busCompany){
        destination = new Coordinates(String.valueOf(10.298237), String.valueOf(123.893133));
        position = new Coordinates(x2, y2);
        this.route = route;
        status = "In-transit";
        this.busNumber = busNumber;
//        this.busCompany= "Ceres";
//        this.accommodation = "Ordinary";
        this.busCompany = busCompany;
        this.accommodation = accommodation;


    }
}

