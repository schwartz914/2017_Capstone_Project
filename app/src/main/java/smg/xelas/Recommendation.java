package smg.xelas;

/**
 * Created by Ahmad-PC on 14/10/2017.
 */

public class Recommendation {
    String Title;
    String Date;
    int id;
    int photoId;

    Recommendation(int id, String title, String date, int photoId) {
        this.id = id;
        this.Title = title;
        this.Date = date;
        this.photoId = photoId;
    }
}
