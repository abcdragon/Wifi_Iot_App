package bro.myapplication;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private String htmlPageUrl = "";
    private EditText inputUrl;
    private TextView textviewHtmlDocument;
    private String htmlContentInStringFormat;
    Activity this_act = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textviewHtmlDocument = (TextView)findViewById(R.id.textView);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod());

        Button htmlTitleButton = (Button)findViewById(R.id.button);
        htmlTitleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });

    }

    public boolean chk(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean return_bool;

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnectedOrConnecting()) {
                return_bool = true;
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE && activeNetwork.isConnectedOrConnecting()) {
                return_bool = true;
            }
            else {
                return_bool = false;
            }
        }
        else {
            return_bool = false;
        }

        return return_bool;
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            htmlContentInStringFormat = "";
            inputUrl = (EditText)findViewById(R.id.inputUrl);
            boolean Url_nullchk = inputUrl.getText().toString().equals("");
            boolean chking = chk();

            if(!chking){
                Toast.makeText(this_act, "인터넷이 연결되지 않았습니다. 다시 연결한 후 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
            else if(Url_nullchk){
                Toast.makeText(this_act, "입력이 잘못되었습니다 다시 입력해 주세요", Toast.LENGTH_SHORT).show();
            }
            else{
                htmlPageUrl = "http://";
                htmlPageUrl += inputUrl.getText().toString().trim();
            }

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(htmlPageUrl).get();
                /*Elements links = doc.select("a[href]");

                for (Element link : links) {
                    htmlContentInStringFormat += (link.attr("abs:href")
                            + "("+link.text().trim() + ")\n");
                }*/
                htmlContentInStringFormat += doc.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids){
            textviewHtmlDocument.setText(htmlContentInStringFormat);
            Toast.makeText(this_act, "로드가 완료되었습니다.", Toast.LENGTH_SHORT).show();
        }


        @Override
        protected void onPostExecute(Void result) {
            textviewHtmlDocument.setText(htmlContentInStringFormat);
        }
    }


}