package gabeconsalter.remorning;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
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
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import gabeconsalter.remorning.entity.MyDate;
import gabeconsalter.remorning.entity.Task;
import gabeconsalter.remorning.misc.ListTaskAdapter;

import static java.security.AccessController.getContext;

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
    private ArrayList<Integer> itemsOldSelected = new ArrayList<Integer>();
    private Toolbar toolbar;
    private boolean itemsEnabled = false;

    private ArrayList<Task> tasks = new ArrayList<Task>();
    private ListView livTasks;
    private ListTaskAdapter aLivTasks;

    private ArrayList<Task> oldTasks = new ArrayList<Task>();
    private ListView livOldTasks;
    private ListTaskAdapter aLivOldTasks;

    private static final int NEW_TASK = 69;

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
                startActivityForResult(new Intent(MainActivity.this, NewTaskActivity.class), 1);
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
        DatabaseReference refTasks = fb.getReference("users/" + user.getUid() + "/task");
        DatabaseReference refOldTasks = fb.getReference("users/" + user.getUid() + "/oldTask");
        updateTasks(fb);

        refTasks.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Task task = dataSnapshot.getValue(Task.class);
                if(!task.getDone() && task.getDate().equals(MyDate.getMyDate(new Date()))){
                    tasks.add(task);
                    aLivTasks = new ListTaskAdapter(MainActivity.this, R.layout.tasks_list_item, tasks);
                    livTasks.setAdapter(aLivTasks);
                    ajustaTamanho(tasks.size(), livTasks);



                    Context ctx = MainActivity.this;
                    Intent notificationIntent = new Intent(ctx, MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(ctx,
                            555, notificationIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

                    NotificationManager nm = (NotificationManager) ctx
                            .getSystemService(Context.NOTIFICATION_SERVICE);

                    Resources res = ctx.getResources();
                    Notification.Builder builder = new Notification.Builder(ctx);

                    builder.setContentIntent(contentIntent)
                            .setSmallIcon(R.drawable.ic_done)
                            .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_done))
                            .setAutoCancel(true)
                            .setContentTitle("Hello " + user.getDisplayName())
                            .setContentText("You have " + tasks.size() + " tasks today");
                    Notification n = builder.build();

                    nm.notify(001, n);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);
                aLivTasks.remove(task);
                tasks.remove(task);
                aLivTasks.notifyDataSetChanged();
                reloadActivity();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        refOldTasks.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Task task = dataSnapshot.getValue(Task.class);
                if(!task.getDone()){
                    oldTasks.add(task);
                    aLivOldTasks = new ListTaskAdapter(MainActivity.this, R.layout.old_tasks_list_item, oldTasks);
                    livOldTasks.setAdapter(aLivOldTasks);
                    ajustaTamanho(oldTasks.size(), livOldTasks);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);
                aLivOldTasks.remove(task);
                oldTasks.remove(task);
                aLivOldTasks.notifyDataSetChanged();
                reloadActivity();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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

        livOldTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!itemsOldSelected.contains(position)){
                    parent.getChildAt(position).setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                    TextView txtDescription = (TextView) parent.getChildAt(position).findViewById(R.id.txtDescription);
                    txtDescription.setTextColor(Color.WHITE);
                    TextView txtDate = (TextView) parent.getChildAt(position).findViewById(R.id.txtDate);
                    txtDate.setTextColor(Color.WHITE);

                    itemsOldSelected.add(position);
                }else{
                    cleanListSelection(parent, position);
                    int i = itemsOldSelected.indexOf(position);
                    itemsOldSelected.remove(i);
                }

                if(itemsOldSelected.size() > 0){
                    itemsEnabled = true;
                    onCreateOptionsMenu(toolbar.getMenu());
                }else{
                    itemsEnabled = false;
                    onCreateOptionsMenu(toolbar.getMenu());
                }
            }
        });
}

    public void reloadActivity(){
        finish();
        startActivity(new Intent(getIntent()));
    }

    public void updateTasks(final FirebaseDatabase fb){
        DatabaseReference ref = fb.getReference("users/" + user.getUid() + "/task");

        ref.orderByChild("date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Task task = dataSnapshot.getValue(Task.class);

                if(MyDate.before(task.getDate(), MyDate.getMyDate(new Date()))){
                    dataSnapshot.getRef().removeValue();

                    DatabaseReference newRef = fb.getReference("users/" + user.getUid() + "/oldTask");
                    newRef.push().setValue(task);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public void addDone(Task task){

    }

    public void cleanListSelection(AdapterView<?> parent, int position){
        parent.getChildAt(position).setBackgroundColor(Color.WHITE);
        TextView txtDescription = (TextView) parent.getChildAt(position).findViewById(R.id.txtDescription);
        txtDescription.setTextColor(Color.GRAY);
        TextView txtDate = (TextView) parent.getChildAt(position).findViewById(R.id.txtDate);
        if(txtDate != null){
            txtDate.setTextColor(Color.GRAY);
        }
    }

    public void ajustaTamanho(int c, ListView lista){
        int tam = c * 56;
        float scale = getResources().getDisplayMetrics().density;

        ViewGroup.LayoutParams params = lista.getLayoutParams();
        params.height = (int) (tam * scale);
        lista.setLayoutParams(params);
        lista.requestLayout();
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

        switch (item.getItemId()){
            case R.id.mitDeleteTask:
                deleteTasks();
                break;
            case R.id.mitDoneTask:
                deleteTasks();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteTasks(){
        for(int i = 0; i < itemsSelected.size(); i++){
            final TextView txtDescriptionTask = (TextView) livTasks.getChildAt(i).findViewById(R.id.txtDescription);
            FirebaseDatabase fb = FirebaseDatabase.getInstance();
            DatabaseReference ref = fb.getReference("users/" + user.getUid() + "/task");

            String sDate = MyDate.getMyDate(new Date());
            if(sDate != null){

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                            if(snap.child("description").getValue().toString().equals(txtDescriptionTask.getText().toString())){
                                snap.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                Toast.makeText(this, getString(R.string.anErrorOcurred), Toast.LENGTH_SHORT).show();
            }
        }

        for(int i = 0; i < itemsOldSelected.size(); i++){
            final TextView txtDescriptionOldTask = (TextView) livOldTasks.getChildAt(i).findViewById(R.id.txtDescription);
            FirebaseDatabase fb = FirebaseDatabase.getInstance();
            DatabaseReference ref = fb.getReference("users/" + user.getUid() + "/oldTask");

            String sDate = MyDate.getMyDate(new Date());
            if(sDate != null){

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                            if(snap.child("description").getValue().toString().equals(txtDescriptionOldTask.getText().toString())){
                                snap.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                Toast.makeText(this, getString(R.string.anErrorOcurred), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void doneTasks(){
        for(int i = 0; i < itemsSelected.size(); i++){
            final TextView txtDescriptionTask = (TextView) livTasks.getChildAt(i).findViewById(R.id.txtDescription);
            FirebaseDatabase fb = FirebaseDatabase.getInstance();
            DatabaseReference ref = fb.getReference("users/" + user.getUid() + "/task");

            String sDate = MyDate.getMyDate(new Date());
            if(sDate != null){

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                            if(snap.child("description").getValue().toString().equals(txtDescriptionTask.getText().toString())){
                                Task task = snap.getValue(Task.class);
                                task.setDone(true);
                                snap.getRef().setValue(task);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                Toast.makeText(this, getString(R.string.anErrorOcurred), Toast.LENGTH_SHORT).show();
            }
        }

        for(int i = 0; i < itemsOldSelected.size(); i++){
            final TextView txtDescriptionOldTask = (TextView) livOldTasks.getChildAt(i).findViewById(R.id.txtDescription);
            FirebaseDatabase fb = FirebaseDatabase.getInstance();
            DatabaseReference ref = fb.getReference("users/" + user.getUid() + "/oldTask");

            String sDate = MyDate.getMyDate(new Date());
            if(sDate != null){

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                            if(snap.child("description").getValue().toString().equals(txtDescriptionOldTask.getText().toString())){
                                Task task = snap.getValue(Task.class);
                                task.setDone(true);
                                snap.getRef().setValue(task);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                Toast.makeText(this, getString(R.string.anErrorOcurred), Toast.LENGTH_SHORT).show();
            }
        }
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

        livOldTasks = (ListView) findViewById(R.id.livOldTasks);
    }

    public void infoUser(){
        Picasso.with(this).load(user.getPhotoUrl()).into(imgUserPhoto);
        txtUserName.setText(user.getDisplayName());
        txtUserEmail.setText(user.getEmail());
    }
}
