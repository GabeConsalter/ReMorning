package gabeconsalter.remorning.entity;

import java.util.Date;

/**
 * Created by consa on 15/05/2017.
 */

public class Task {

    private String description;
    private String date;

    public Task() {
    }

    public Task(String description, String date) {
        this.description = description;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
