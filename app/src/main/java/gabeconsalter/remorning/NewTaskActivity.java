package gabeconsalter.remorning;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import gabeconsalter.remorning.entity.MyDate;
import gabeconsalter.remorning.entity.Task;

public class NewTaskActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private TextView txtNewWhat;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText edtNewWhat;
    private CalendarView calendarView;
    private Date data = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initViews();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                data = calendar.getTime();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sData = MyDate.getMyDate(data);

                if(sData != null){

                    Task task = new Task(edtNewWhat.getText().toString(), sData, false);

                    mAuth = FirebaseAuth.getInstance();
                    user = mAuth.getCurrentUser();
                    FirebaseDatabase fb = FirebaseDatabase.getInstance();
                    DatabaseReference ref = fb.getReference("users/" + user.getUid() + "/task/");
                    ref.push().setValue(task);

                    finish();
                }else{
                    Toast.makeText(NewTaskActivity.this, getString(R.string.anErrorOcurred), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //disableFab();

    }

    public void enableFab(){
        fab.setEnabled(true);
        fab.getBackground().setAlpha(100);
    }

    public void disableFab(){
        fab.setEnabled(false);
        fab.getBackground().setAlpha(80);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.newTaskCancelled), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                Toast.makeText(this, getString(R.string.newTaskCancelled), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return true;
    }

    public void initViews(){
        fab = (FloatingActionButton) findViewById(R.id.fab);
        txtNewWhat = (TextView) findViewById(R.id.txtNewWhat);
        edtNewWhat = (EditText) findViewById(R.id.edtNewWhat);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
    }

}
