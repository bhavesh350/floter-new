package cargo.floter.user.application;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import cargo.floter.user.model.NearbyDrivers.Response;
import cargo.floter.user.model.RateCard.RateCardResponse;
import cargo.floter.user.model.Trip;
import java.util.ArrayList;
import java.util.List;

public class SingleInstance {
    private static final SingleInstance ourInstance = new SingleInstance();
    private List<Trip> allTrips = new ArrayList();
    private LatLng destinationLatLng;
    private Trip historyTrip;
    private List<Response> notifiableDrivers;
    private Place selectedPlace;
    private RateCardResponse selectedRate;
    private LatLng sourceLatLng;

    public static SingleInstance getInstance() {
        return ourInstance;
    }

    private SingleInstance() {
    }

    public List<Trip> getAllTrips() {
        return this.allTrips;
    }

    public void setAllTrips(List<Trip> allTrips) {
        this.allTrips = allTrips;
    }

    public Place getSelectedPlace() {
        return this.selectedPlace;
    }

    public void setSelectedPlace(Place selectedPlace) {
        this.selectedPlace = selectedPlace;
    }

    public List<Response> getNotifiableDrivers() {
        return this.notifiableDrivers;
    }

    public void setNotifiableDrivers(List<Response> notifiableDrivers) {
        this.notifiableDrivers = notifiableDrivers;
    }

    public LatLng getSourceLatLng() {
        return this.sourceLatLng;
    }

    public void setSourceLatLng(LatLng sourceLatLng) {
        this.sourceLatLng = sourceLatLng;
    }

    public LatLng getDestinationLatLng() {
        return this.destinationLatLng;
    }

    public void setDestinationLatLng(LatLng destinationLatLng) {
        this.destinationLatLng = destinationLatLng;
    }

    public RateCardResponse getSelectedRate() {
        return this.selectedRate;
    }

    public void setSelectedRate(RateCardResponse selectedRate) {
        this.selectedRate = selectedRate;
    }

    public Trip getHistoryTrip() {
        return this.historyTrip;
    }

    public void setHistoryTrip(Trip historyTrip) {
        this.historyTrip = historyTrip;
    }
}
