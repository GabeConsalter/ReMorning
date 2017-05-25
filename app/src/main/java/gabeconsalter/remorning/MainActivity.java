package gabeconsalter.remorning;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gabeconsalter.remorning.entity.Task;
import gabeconsalter.remorning.misc.ListTaskAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageView imgUserPhoto;
    private TextView txtUserName;
    private TextView txtUserEmail;
    private LinearLayout navHeaderMain;
    private NavigationView navigationView;
    private View nvHeader;
    private ArrayList<Integer> itemsSelected = new ArrayList<Integer>();
    private Toolbar toolbar;
    private boolean itemsEnabled = false;
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private ListView livTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewTaskActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        infoUser();

        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        DatabaseReference ref = fb.getReference("users/" + user.getUid() + "/task");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    Task task = snap.getValue(Task.class);

                    tasks.add(task);
                    ListTaskAdapter adapter = new ListTaskAdapter(MainActivity.this, R.layout.tasks_list_item, tasks);
                    livTasks.setAdapter(adapter);
                    ajustaTamanho(tasks.size(), livTasks);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        livTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!itemsSelected.contains(position)){
                    parent.getChildAt(position).setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                    TextView txtDescription = (TextView) parent.getChildAt(position).findViewById(R.id.txtDescription);
                    txtDescription.setTextColor(Color.WHITE);

                    itemsSelected.add(position);
                }else{
                    cleanListSelection(parent, position);
                    int i = itemsSelected.indexOf(position);
                    itemsSelected.remove(i);
                }

                if(itemsSelected.size() > 0){
                    itemsEnabled = true;
                    onCreateOptionsMenu(toolbar.getMenu());
                }else{
                    itemsEnabled = false;
                    onCreateOptionsMenu(toolbar.getMenu());
                }
            }
        });
}

    public void cleanListSelection(AdapterView<?> parent, int position){
        parent.getChildAt(position).setBackgroundColor(Color.WHITE);
        TextView txtDescription = (TextView) parent.getChildAt(position).findViewById(R.id.txtDescription);
        txtDescription.setTextColor(Color.GRAY);
    }

    public void ajustaTamanho(int c, ListView lista){
        int tam = c * 56;
        float scale = getResources().getDisplayMetrics().density;

        ViewGroup.LayoutParams params = lista.getLayoutParams();
        params.height = (int) (tam * scale);
        lista.setLayoutParams(params);
        lista.requestLayout();
    }

    public void showListActions(){
        try{
            Menu menu = toolbar.getMenu();
            MenuItem mi = menu.getItem(0);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_actions, menu);

        MenuItem mitDoneTask = menu.findItem(R.id.mitDoneTask);
        MenuItem mitDeleteTask = menu.findItem(R.id.mitDeleteTask);

        mitDoneTask.setEnabled(itemsEnabled);
        mitDoneTask.setVisible(itemsEnabled);
        mitDeleteTask.setEnabled(itemsEnabled);
        mitDeleteTask.setVisible(itemsEnabled);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.mitDoneTask){

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.close)
                    .setMessage(R.string.closeMsg)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navConfig) {
            startActivity(new Intent(MainActivity.this, ConfigActivity.class));

        } else if (id == R.id.navSignout) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.signout)
                    .setMessage(R.string.signoutConfirm)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            signOut();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut(){
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        nvHeader = navigationView.getHeaderView(0);
        imgUserPhoto = (ImageView) nvHeader.findViewById(R.id.imgUserPhoto);
        txtUserName = (TextView) nvHeader.findViewById(R.id.txtUserName);
        txtUserEmail = (TextView) nvHeader.findViewById(R.id.txtUserEmail);
        livTasks = (ListView) findViewById(R.id.livTasks);
    }

    public void infoUser(){
        Picasso.with(this).load(user.getPhotoUrl()).into(imgUserPhoto);
        txtUserName.setText(user.getDisplayName());
        txtUserEmail.setText(user.getEmail());
    }
}
