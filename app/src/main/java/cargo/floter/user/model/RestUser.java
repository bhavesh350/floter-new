package cargo.floter.user.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Abhishek on 20-04-2017.
 */

public class RestUser implements Serializable {

    private static final long serialVersionUID = 752647256228747L;
    private String status;
    private String code;
    private String message;
    private List<Response> response;

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }

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

    public class Response implements Serializable{
        private static final long serialVersionUID = 75264722956228747L;
        private String id;
        private String goodtype_name;
        private String active;
        private String created;
        private String modified;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGoodtype_name() {
            return goodtype_name;
        }

        public void setGoodtype_name(String goodtype_name) {
            this.goodtype_name = goodtype_name;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getModified() {
            return modified;
        }

        public void setModified(String modified) {
            this.modified = modified;
        }


    }
}
