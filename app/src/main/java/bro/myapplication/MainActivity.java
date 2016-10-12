package bro.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ITEM_SIZE = 4;
    private static final String strSDpath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String URLs = "http://www.naver.com";

    private static String value_1 = "";
    private static String value_2 = "";
    private static String value_3 = "";
    private static String value_4 = "";

    private static Context context;
    private static RecyclerView recyclerView;
    private Toolbar toolbar;

    private static File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFile = new File(strSDpath+"/Parsing_URL.txt");
        try {
            if (!mFile.exists()) {
                FileOutputStream fos = new FileOutputStream(mFile);
                fos.write(URLs.getBytes());
                fos.close();
            } else {
                URLs = "";
                FileInputStream fis = new FileInputStream(strSDpath + "/Parsing_URL.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));

                String temp ="";
                while((temp = bufferedReader.readLine()) != null) {
                    URLs += temp;
                }
                Toast.makeText(getAppContext(), " "+URLs, Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        MainActivity.context = getApplicationContext();

        // RecyclerView 선언
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        // RecyclerView 선언 끝

        // Toolbar 선언
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("IoT Service");
        toolbar.inflateMenu(R.menu.main); // 아이콘을 붙이기 위해 메뉴를 추가
        Drawable dr = getResources().getDrawable(R.drawable.ic_settings_white_24dp);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        toolbar.setOverflowIcon(drawable);
        // 끝

        // 인터넷 연결 상태 체크
        if(!isConnectedNetwork())Toast.makeText(getAppContext(), "인터넷에 연결 되지 않았습니다. 인터넷에 연결한 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
        // 끝

        // cardview에 들어갈 item 설정
        List<Item> items = new ArrayList<>();
        Item[] item = new Item[ITEM_SIZE];
        item[0] = new Item(R.drawable.rock_close, "보안모드", value_1);
        item[1] = new Item(R.drawable.window, "창문 상태", value_2);
        item[2] = new Item(R.drawable.temp, "온도", value_3);
        item[3] = new Item(R.drawable.rock_open, "문 열기", value_4);

        for (int i = 0; i < ITEM_SIZE; i++) {
            items.add(item[i]);
        }
        // 끝

        // RecyclerView 설정
        recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_main));
        // 끝
    }

    public static Context getAppContext(){ // context가 non-static이기 때문에 static 클래스에서 사용하기 위한 메서드
        return context;
    }

    public static boolean isConnectedNetwork(){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        boolean chk = mobile.isConnected() || wifi.isConnected();

        return chk;
    }

    public static void ParsingData(){ // Data 파싱
        Document doc;
        try{
            doc = Jsoup.connect(URLs).get();
            Elements ele1 = doc.select("title").eq(0);
            Elements ele2 = doc.select("a").eq(0);
            Elements ele3 = doc.select("p").eq(0);
            Elements ele4 = doc.select("div").eq(0);
            value_1 = ele1.text();
            value_2 = ele2.text();
            value_3 = ele3.text();
            value_4 = ele4.text();
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }

    static class MyAsyncTask extends AsyncTask<Void, Void, Void>{ // 메인쓰레드의 부하를 막기 위한 AsyncTask
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids){
            if(!isConnectedNetwork()) return null;
            ParsingData();
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids){
            Toast.makeText(getAppContext(), "URL : " + URLs,Toast.LENGTH_LONG).show();

            RecyclerView recyclerView = MainActivity.recyclerView;
            LinearLayoutManager layoutManager = new LinearLayoutManager(getAppContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);

            if(!isConnectedNetwork()) {
                Toast.makeText(getAppContext(), "인터넷에 연결 되지 않았습니다. 인터넷에 연결한 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Item> items = new ArrayList<>();
            Item[] item = new Item[ITEM_SIZE];
            item[0] = new Item(R.drawable.rock_close, "보안모드", value_1);
            item[1] = new Item(R.drawable.window, "창문 상태", value_2);
            item[2] = new Item(R.drawable.temp, "온도", value_3);
            item[3] = new Item(R.drawable.rock_open, "문 열기", value_4);

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
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("파싱 설정");
            alert.setMessage("파싱할 주소를 입력해주세요");

            final EditText input =  new EditText(this);
            input.setText(URLs);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int whichButton){
                    // 아래는 input값이 제대로 들어갔는지에 대한 확인용 토스트
                    // Toast.makeText(getAppContext(), " "+input.getText().toString(), Toast.LENGTH_SHORT).show();
                    try{
                        FileOutputStream fos = new FileOutputStream(mFile);
                        fos.write(input.getText().toString().getBytes());
                        fos.close();
                        URLs = input.getText().toString();
                        MainActivity.MyAsyncTask myAsyncTask = new MainActivity.MyAsyncTask();
                        myAsyncTask.execute();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int whichButton){
                }
            });

            alert.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}