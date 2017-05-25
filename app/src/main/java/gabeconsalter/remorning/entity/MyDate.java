package gabeconsalter.remorning.entity;

import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by consa on 25/05/2017.
 */

public class MyDate {

    public static String getMyDate(Date date){
        SimpleDateFormat sFormat = new SimpleDateFormat("dd-MM-yyyy");
        String sDate = sFormat.format(date);

        try{
            date = sFormat.parse(sDate);
        }catch (Exception e){
            return null;
        }

        return sDate;
    }
}
