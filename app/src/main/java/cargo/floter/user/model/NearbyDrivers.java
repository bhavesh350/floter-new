package cargo.floter.user.model;

import java.util.List;

/**
 * Created by SONI on 4/26/2017.
 */

public class NearbyDrivers {

    private String status;
    private int code;
    private List<Response> response;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }

    public class Response{
        private String driver_id;
        private String d_name;
        private String d_fname;
        private String d_lname;
        private String d_email;
        private String d_phone;
        private String d_address;
        private String d_city;
        private String d_state;
        private String d_country;
        private String d_zip;
        private String d_lat;
        private String d_lng;
        private String truck_reg_no;
        private String truck_model;
        private String truck_description;
        private String d_device_type;
        private String d_device_token;
        private String d_rating;
        private String d_rating_count;
        private String d_is_available;
        private String d_is_verified;
        private String d_created;
        private String car_name;
        private String d_profile_image_path;
        private String car_fare_per_km;
        private String car_fare_per_min;
        private String base_fare;
        private String distance;
        private String price_per_km;
        private String price_per_min_after_60_min;
        private String capacity;

        public String getDriver_id() {
            return driver_id;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public String getD_name() {
            return d_name;
        }

        public void setD_name(String d_name) {
            this.d_name = d_name;
        }

        public String getD_fname() {
            return d_fname;
        }

        public void setD_fname(String d_fname) {
            this.d_fname = d_fname;
        }

        public String getD_lname() {
            return d_lname;
        }

        public void setD_lname(String d_lname) {
            this.d_lname = d_lname;
        }

        public String getD_email() {
            return d_email;
        }

        public void setD_email(String d_email) {
            this.d_email = d_email;
        }

        public String getD_phone() {
            return d_phone;
        }

        public void setD_phone(String d_phone) {
            this.d_phone = d_phone;
        }

        public String getD_address() {
            return d_address;
        }

        public void setD_address(String d_address) {
            this.d_address = d_address;
        }

        public String getD_city() {
            return d_city;
        }

        public void setD_city(String d_city) {
            this.d_city = d_city;
        }

        public String getD_state() {
            return d_state;
        }

        public void setD_state(String d_state) {
            this.d_state = d_state;
        }

        public String getD_country() {
            return d_country;
        }

        public void setD_country(String d_country) {
            this.d_country = d_country;
        }

        public String getD_zip() {
            return d_zip;
        }

        public void setD_zip(String d_zip) {
            this.d_zip = d_zip;
        }

        public String getD_lat() {
            return d_lat;
        }

        public void setD_lat(String d_lat) {
            this.d_lat = d_lat;
        }

        public String getD_lng() {
            return d_lng;
        }

        public void setD_lng(String d_lng) {
            this.d_lng = d_lng;
        }

        public String getTruck_reg_no() {
            return truck_reg_no;
        }

        public void setTruck_reg_no(String truck_reg_no) {
            this.truck_reg_no = truck_reg_no;
        }

        public String getTruck_model() {
            return truck_model;
        }

        public void setTruck_model(String truck_model) {
            this.truck_model = truck_model;
        }

        public String getTruck_description() {
            return truck_description;
        }

        public void setTruck_description(String truck_description) {
            this.truck_description = truck_description;
        }

        public String getD_device_type() {
            return d_device_type;
        }

        public void setD_device_type(String d_device_type) {
            this.d_device_type = d_device_type;
        }

        public String getD_device_token() {
            return d_device_token;
        }

        public void setD_device_token(String d_device_token) {
            this.d_device_token = d_device_token;
        }

        public String getD_rating() {
            return d_rating;
        }

        public void setD_rating(String d_rating) {
            this.d_rating = d_rating;
        }

        public String getD_rating_count() {
            return d_rating_count;
        }

        public void setD_rating_count(String d_rating_count) {
            this.d_rating_count = d_rating_count;
        }

        public String getD_is_available() {
            return d_is_available;
        }

        public void setD_is_available(String d_is_available) {
            this.d_is_available = d_is_available;
        }

        public String getD_is_verified() {
            return d_is_verified;
        }

        public void setD_is_verified(String d_is_verified) {
            this.d_is_verified = d_is_verified;
        }

        public String getD_created() {
            return d_created;
        }

        public void setD_created(String d_created) {
            this.d_created = d_created;
        }

        public String getCar_name() {
            return car_name;
        }

        public void setCar_name(String car_name) {
            this.car_name = car_name;
        }

        public String getD_profile_image_path() {
            return d_profile_image_path;
        }

        public void setD_profile_image_path(String d_profile_image_path) {
            this.d_profile_image_path = d_profile_image_path;
        }

        public String getCar_fare_per_km() {
            return car_fare_per_km;
        }

        public void setCar_fare_per_km(String car_fare_per_km) {
            this.car_fare_per_km = car_fare_per_km;
        }

        public String getCar_fare_per_min() {
            return car_fare_per_min;
        }

        public void setCar_fare_per_min(String car_fare_per_min) {
            this.car_fare_per_min = car_fare_per_min;
        }

        public String getBase_fare() {
            return base_fare;
        }

        public void setBase_fare(String base_fare) {
            this.base_fare = base_fare;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getPrice_per_km() {
            return price_per_km;
        }

        public void setPrice_per_km(String price_per_km) {
            this.price_per_km = price_per_km;
        }

        public String getPrice_per_min_after_60_min() {
            return price_per_min_after_60_min;
        }

        public void setPrice_per_min_after_60_min(String price_per_min_after_60_min) {
            this.price_per_min_after_60_min = price_per_min_after_60_min;
        }

        public String getCapacity() {
            return capacity;
        }

        public void setCapacity(String capacity) {
            this.capacity = capacity;
        }

    }
}
