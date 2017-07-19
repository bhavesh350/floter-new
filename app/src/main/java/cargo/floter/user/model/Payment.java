package cargo.floter.user.model;

public class Payment {
    private Trip Trip;
    private String d_fname;
    private String order_id;
    private String pay_amount;
    private String pay_created;
    private String pay_date;
    private String pay_mode;
    private String pay_modified;
    private String pay_promo_amt;
    private String pay_promo_code;
    private String pay_status;
    private String pay_time;
    private String pay_trans_id;
    private String payment_id;
    private String promo_id;
    private String settlement;
    private String transaction_id;
    private String trip_id;
    private String u_fname;

    public String getTransaction_id() {
        return this.transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getPayment_id() {
        return this.payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getTrip_id() {
        return this.trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getU_fname() {
        return this.u_fname;
    }

    public void setU_fname(String u_fname) {
        this.u_fname = u_fname;
    }

    public String getD_fname() {
        return this.d_fname;
    }

    public void setD_fname(String d_fname) {
        this.d_fname = d_fname;
    }

    public String getPay_trans_id() {
        return this.pay_trans_id;
    }

    public void setPay_trans_id(String pay_trans_id) {
        this.pay_trans_id = pay_trans_id;
    }

    public String getPay_date() {
        return this.pay_date;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public String getPay_mode() {
        return this.pay_mode;
    }

    public void setPay_mode(String pay_mode) {
        this.pay_mode = pay_mode;
    }

    public String getPay_amount() {
        return this.pay_amount;
    }

    public void setPay_amount(String pay_amount) {
        this.pay_amount = pay_amount;
    }

    public String getPay_status() {
        return this.pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getPromo_id() {
        return this.promo_id;
    }

    public void setPromo_id(String promo_id) {
        this.promo_id = promo_id;
    }

    public String getPay_promo_code() {
        return this.pay_promo_code;
    }

    public void setPay_promo_code(String pay_promo_code) {
        this.pay_promo_code = pay_promo_code;
    }

    public String getPay_promo_amt() {
        return this.pay_promo_amt;
    }

    public void setPay_promo_amt(String pay_promo_amt) {
        this.pay_promo_amt = pay_promo_amt;
    }

    public String getPay_time() {
        return this.pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getSettlement() {
        return this.settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public String getPay_created() {
        return this.pay_created;
    }

    public void setPay_created(String pay_created) {
        this.pay_created = pay_created;
    }

    public String getPay_modified() {
        return this.pay_modified;
    }

    public void setPay_modified(String pay_modified) {
        this.pay_modified = pay_modified;
    }

    public Trip getTrip() {
        return this.Trip;
    }

    public void setTrip(Trip trip) {
        this.Trip = trip;
    }
}
