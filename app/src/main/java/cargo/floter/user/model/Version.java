package cargo.floter.user.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by SONI on 9/9/2017.
 */

@IgnoreExtraProperties
public class Version {
    public Version() {
      /*Blank default constructor essential for Firebase*/
    }
    public String user;
    public String driver;
}
