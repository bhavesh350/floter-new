package cargo.floter.user.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Abhishek on 16-04-2017.
 */

public class RateCard implements Serializable{
    private static final long serialVersionUID = 752647632562247L;
    private String status;
    private String code;
    private String message;
    private List<RateCardResponse> response;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RateCardResponse> getResponse() {
        return response;
    }

    public void setResponse(List<RateCardResponse> response) {
        this.response = response;
    }

    public class RateCardResponse implements Serializable{
        private static final long serialVersionUID = 752647229536227847L;
        private String base_fare;
        private String capacity;
        private String car_name;
        private String charge_after_free_time;
        private String free_load_unload_time;
        private String height;
        private String length;
        private String price_per_km;
        private String price_per_min_after_60_min;
        private String width;

        public String getCharge_after_free_time() {
            return charge_after_free_time;
        }

        public void setCharge_after_free_time(String charge_after_free_time) {
            this.charge_after_free_time = charge_after_free_time;
        }

        public String getFree_load_unload_time() {
            return free_load_unload_time;
        }

        public void setFree_load_unload_time(String free_load_unload_time) {
            this.free_load_unload_time = free_load_unload_time;
        }

        public String getPrice_per_min_after_60_min() {
            return price_per_min_after_60_min;
        }

        public void setPrice_per_min_after_60_min(String price_per_min_after_60_min) {
            this.price_per_min_after_60_min = price_per_min_after_60_min;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getCar_name() {
            return car_name;
        }

        public void setCar_name(String car_name) {
            this.car_name = car_name;
        }

        public String getBase_fare() {
            return base_fare;
        }

        public void setBase_fare(String base_fare) {
            this.base_fare = base_fare;
        }

        public String getPrice_per_km() {
            return price_per_km;
        }

        public void setPrice_per_km(String price_per_km) {
            this.price_per_km = price_per_km;
        }

        public String getCapacity() {
            return capacity;
        }

        public void setCapacity(String capacity) {
            this.capacity = capacity;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        private String size;


    }
}
