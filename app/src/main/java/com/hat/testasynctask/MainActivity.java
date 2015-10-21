package com.hat.testasynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends Activity {

    private Button mExecuteBtn;
    public Button mCancelBtn;
    public ProgressBar mProgBar;
    public TextView mProgTxtView;
    TestAsyncTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mExecuteBtn = (Button)findViewById(R.id.execute_btn);
        mCancelBtn = (Button)findViewById(R.id.cancel_btn);
        mProgBar = (ProgressBar)findViewById(R.id.progress_bar);
        mProgTxtView = (TextView)findViewById(R.id.textview);

        mExecuteBtn.setEnabled(true);
        mProgBar.setProgress(0);
        mProgTxtView.setText("");
        mCancelBtn.setEnabled(false);

        mExecuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTask = new TestAsyncTask();
                mTask.execute("http://www.ifeng.com");

                mExecuteBtn.setEnabled(false);
                mCancelBtn.setEnabled(true);
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTask.cancel(true);

                mCancelBtn.setEnabled(false);
                mCancelBtn.setEnabled(false);
            }
        });

    }

    private class TestAsyncTask extends AsyncTask<String, Integer,String> {

        @Override
        protected void onCancelled() {
            mExecuteBtn.setEnabled(true);
            mProgBar.setProgress(0);
        }

        @Override
        protected void onCancelled(String o) {
            mExecuteBtn.setEnabled(true);
            Log.i("test", "onCancelled: " + o);
        }

        @Override
        protected void onPostExecute(String o) {
            Log.i("test", "onPostExecute: " + o);
            mCancelBtn.setEnabled(true);
            mCancelBtn.setEnabled(true);
        }

        @Override
        protected void onPreExecute() {
            mExecuteBtn.setEnabled(false);
            mProgTxtView.setText("loading......");
            mExecuteBtn.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i("test", "onProgressUpdate: " + values[0]);
                    mProgBar.setProgress(values[0]);
            mProgTxtView.setText("loading......" + values[0] + "%");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    long total = entity.getContentLength();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int count = 0;
                    int length = -1;
                    while ((length = is.read(buf)) != -1) {
                        baos.write(buf, 0, length);
                        count += length;
                        //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
                        Log.i("test", "doInBackground cnt=" + count + ", total=" + total);
                        publishProgress((int) ((count / (float) total) * 100));
                        //为了演示进度,休眠500毫秒
                        Thread.sleep(500);
                    }
                    return new String(baos.toByteArray(), "gb2312");
                }
            } catch (Exception e) {
                Log.e("test", e.getMessage());
            }
            return null;
        }


    }
}
