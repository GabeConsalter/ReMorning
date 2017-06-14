package gabeconsalter.remorning;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtConfigNewTasksHour;
    private TextView txtConfigResetHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtConfigNewTasksHour = (TextView) findViewById(R.id.txtConfigNewTasksHour);
        txtConfigNewTasksHour.setText("08:00 AM");

        txtConfigResetHour = (TextView) findViewById(R.id.txtConfigResetHour);
        txtConfigResetHour.setText("12:00 AM");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }
}
