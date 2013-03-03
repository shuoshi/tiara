package me.shuoshi.tiara;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class News extends Activity {

	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listView = new ListView(this);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, new ArrayList()));
		new LoadNewsTask().execute();
		// listView.setAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_expandable_list_item_1,getData()));
		// setContentView(listView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_news, menu);
		return true;
	}
	
	public class JsonAdapter extends SimpleAdapter {
		
	    public News context;
	    public ArrayList<HashMap<String,String>> list;
	    public String[] fieldNames;
	    public int[] fieldTargetIds;

	    public JsonAdapter(News c, 
	            ArrayList<HashMap<String, String>> newses,
	            int textViewResourceId,
	            String[] fieldNames,
	            int[] fieldTargetIds) {
	        super(c, newses, textViewResourceId, fieldNames, fieldTargetIds);
	        this.context = c;
	        this.list = newses;
	        this.fieldNames = fieldNames;
	        this.fieldTargetIds = fieldTargetIds;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

	        View row = convertView;
	        if (row == null) {
	            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            //row = vi.inflate(R.layout.list, null);
	        }
	        //super.getView(position, convertView, parent);

	        for (int i=0; i<fieldNames.length; i++) {
	            TextView tv = (TextView) row.findViewById(fieldTargetIds[i]);
	            tv.setText(list.get(position).get(fieldNames[i]));              
	        }
	        return row;
	    }
	}

	private class LoadNewsTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>> > {

        protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {
			ArrayList<HashMap<String, String>> newses = new ArrayList<HashMap<String, String>>();
        	try {
				DefaultHttpClient httpClient = new DefaultHttpClient();   
		    	//����HttpGetʵ��
		    	HttpGet request = new HttpGet("http://shuoshi.me/news.php");
				// ���ӷ�����
				HttpResponse response = httpClient.execute(request);
				// ��ȡ����ͷ����
				Header[] header = response.getAllHeaders();
				HashMap<String, String> hm = new HashMap<String, String>();
				for (int i = 0; i < header.length; i++) {
					hm.put(header[i].getName(), header[i].getValue());
				}
				// ȡ�����ݼ�¼
				HttpEntity entity = response.getEntity();
				// ȡ�����ݼ�¼����
				InputStream is = entity.getContent();
				// ��ʾ���ݼ�¼����
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
				String str = "";// in.readLine();
				StringBuffer s = new StringBuffer("");
				while ((str = in.readLine()) != null) {
					s.append(str);
				}
				// �ͷ�����
				httpClient.getConnectionManager().shutdown();
				try {
					JSONArray newsesJson = new JSONArray(s.toString());
					for (int i=0; i<newsesJson.length(); i++){
						JSONObject jsonAttributes = newsesJson.getJSONObject(i);
						Log.i("href", jsonAttributes.getString("href"));
						HashMap<String, String> map = new HashMap<String, String>();
				        map.put("href", jsonAttributes.getString("href"));
				        map.put("title", jsonAttributes.getString("title"));
						newses.add(map);
					}
				} catch (JSONException e) {
					System.out.println(e.getMessage());
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(), "ClientProtocolException", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
    	    return newses;		    	
        }

		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			/*ListAdapter adapter = new JsonAdapter(News.this, result, R.layout.list,
		              new String[] {"href", "title"}, new int[] {R.id.item_href, R.id.item_title});
			setListAdapter(adapter);*/
		}
	}
}