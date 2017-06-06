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

    public static Date getMyDate(String sDate){
        SimpleDateFormat sFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        try{
            date = sFormat.parse(sDate);
        }catch (Exception e){
            return null;
        }

        return date;
    }

    public static boolean before(String date1, String date2){
        SimpleDateFormat sFormat = new SimpleDateFormat("dd-MM-yyyy");

        try{
            return sFormat.parse(date1).before(sFormat.parse(date2));
        }catch (Exception e){
            return false;
        }
    }
}
