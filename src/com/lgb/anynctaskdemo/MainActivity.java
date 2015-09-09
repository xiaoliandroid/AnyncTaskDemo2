package com.lgb.anynctaskdemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private Button bt;
	private ImageView iv;
	private ProgressDialog dialog;
	private String IMAGE_PATH = "http://p8.qhimg.com/t011e93ef805362113f.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bt = (Button) findViewById(R.id.button1);
		iv = (ImageView) findViewById(R.id.imageView1);
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示信息");
		dialog.setMessage("正在下载请稍后");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				new MyTask().execute(IMAGE_PATH);

			}
		});
	}

	/**
	 * @author liwai 第一个参数是URL 第二个参数是各种类型,进度的刻度 第三个参数 任务执行的返回
	 *
	 */
	public class MyTask extends AsyncTask<String, Integer, Bitmap> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}

		// 耗时操作
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params[0]);
			Bitmap bp = null;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			InputStream inputStream = null;
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					/*
					 * HttpEntity httpEntity = httpResponse.getEntity(); byte[]
					 * data = EntityUtils.toByteArray(httpEntity); bp =
					 * BitmapFactory.decodeByteArray(data, 0, data.length);
					 */
					inputStream = httpResponse.getEntity().getContent();
					// 获得文件的总长度
					long file_length = httpResponse.getEntity()
							.getContentLength();
					int len = 0;
					byte[] data = new byte[1024];
					int total_length = 0;

					while ((len = inputStream.read(data)) != -1) {
						total_length += len;
						int value = (int) ((total_length / (float) file_length) * 100);
						publishProgress(value);
						outputStream.write(data, 0, data.length);
					}
					byte[] result = outputStream.toByteArray();
					bp = BitmapFactory
							.decodeByteArray(result, 0, result.length);
				}

			} catch (ClientProtocolException e) { // TODO Auto-generated catch
													// block
													// e.printStackTrace();
			} catch (IOException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return bp;
		}

		// 跟新UI操作
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.dismiss();
			iv.setImageBitmap(result);

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			dialog.setProgress(values[0]);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
