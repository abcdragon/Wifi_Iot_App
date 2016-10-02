package bro.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static int ITEM_SIZE = 4;
    static String URLs = "http://www.naver.com";

    private static String value_1 = "";
    private static String value_2 = "";
    private static String value_3 = "#3";
    private static String value_4 = "#4";

    private static int count = 0;

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;

    private static Context context;
    private static RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        // RecyclerView 선언
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        // RecyclerView 선언 종료

        // Toolbar 선언
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("IoT Service");
        toolbar.inflateMenu(R.menu.main);
        Drawable dr = getResources().getDrawable(R.drawable.ic_settings_white_24dp);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        Drawable drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        toolbar.setOverflowIcon(drawable);
        // Toolbar 선언 종료

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        boolean chk = mobile.isConnected() || wifi.isConnected();

        if(!chk)Toast.makeText(getAppContext(), "인터넷에 연결 되지 않았습니다. 인터넷에 연결한 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();

        List<Item> items = new ArrayList<>();
        Item[] item = new Item[ITEM_SIZE];
        item[0] = new Item("온도", value_1);
        item[1] = new Item("수분", value_2);
        item[2] = new Item("#3", value_3);
        item[3] = new Item("#4", value_4);

        for (int i = 0; i < ITEM_SIZE; i++) {
            items.add(item[i]);
        }

        recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_main));

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;

                if(count % 2 == 0){
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute();
                    count = 0;
                }
            }
        });
    }

    public static Context getAppContext(){
        return context;
    }

    static class MyAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids){
            Document doc;
            try{
                doc = Jsoup.connect(URLs).get();
                Element ele = doc.select("title").first();
                value_1 = ele.text();
                Toast.makeText(getAppContext(), value_1, Toast.LENGTH_SHORT).show();
            }

            catch(Exception e){
                e.printStackTrace();
            }

            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids){
            RecyclerView recyclerView = MainActivity.recyclerView;
            LinearLayoutManager layoutManager = new LinearLayoutManager(getAppContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);

            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            boolean chk = mobile.isConnected() || wifi.isConnected();

            if(!chk)Toast.makeText(getAppContext(), "인터넷에 연결 되지 않았습니다. 인터넷에 연결한 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();

            List<Item> items = new ArrayList<>();
            Item[] item = new Item[ITEM_SIZE];
            item[0] = new Item("온도", value_1);
            item[1] = new Item("수분", value_2);
            item[2] = new Item("#3", value_3);
            item[3] = new Item("#4", value_4);

            for (int i = 0; i < ITEM_SIZE; i++) {
                items.add(item[i]);
            }

            recyclerView.setAdapter(new RecyclerAdapter(getAppContext(), items, R.layout.activity_main));
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 한 줄 코드
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.settings){
            Toast.makeText(getAppContext(), "설정 버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}