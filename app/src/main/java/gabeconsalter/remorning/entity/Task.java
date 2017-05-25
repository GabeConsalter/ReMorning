package gabeconsalter.remorning.entity;

import java.util.Date;

/**
 * Created by consa on 15/05/2017.
 */

public class Task {

    private String description;
    private String date;
    private boolean done;
    private String  id;

    public Task() {
    }

    public Task(String description, String date, boolean done) {
        this.description = description;
        this.date = date;
        this.done = done;
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

    public boolean getDone() { return done; }

    public void setDone(boolean done) { this.done = done; }
}
