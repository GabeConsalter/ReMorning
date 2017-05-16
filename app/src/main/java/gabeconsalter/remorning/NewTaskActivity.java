package gabeconsalter.remorning;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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

import gabeconsalter.remorning.entity.Task;

public class NewTaskActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private TextView txtNewWhat;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText edtNewWhat;
    private CalendarView calendarView;
    private Date data;

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

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String sData = format.format(data);

                try{
                    data = format.parse(sData);
                }catch(Exception e){
                    Toast.makeText(NewTaskActivity.this, "Não foi possível salvar, tente novamente", Toast.LENGTH_SHORT).show();
                }

                Task task = new Task(edtNewWhat.getText().toString(), sData);

                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();
                FirebaseDatabase fb = FirebaseDatabase.getInstance();
                DatabaseReference ref = fb.getReference("users/" + user.getUid() + "/task/");
                ref.push().setValue(task);
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

    public void initViews(){
        fab = (FloatingActionButton) findViewById(R.id.fab);
        txtNewWhat = (TextView) findViewById(R.id.txtNewWhat);
        edtNewWhat = (EditText) findViewById(R.id.edtNewWhat);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
    }

}
