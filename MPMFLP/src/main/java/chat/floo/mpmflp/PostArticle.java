package chat.floo.mpmflp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class PostArticle extends ActionBarActivity {

    Button submit,cancel,browseImage;
    EditText title,content;
    TextView imageName;
    SessionManager session;
    String titleText,contentText;
    String filePath="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView titleBar = (TextView)toolbar.findViewById(R.id.toolbar_title);
        titleBar.setText("Artikel");
        session = new SessionManager(this);
        submit = (Button)findViewById(R.id.submit);
        cancel = (Button)findViewById(R.id.cancel);
        browseImage = (Button)findViewById(R.id.browseImage);

        title = (EditText)findViewById(R.id.postTitle);
        content = (EditText)findViewById(R.id.postContent);

        imageName = (TextView)findViewById(R.id.imageName);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        browseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, 1);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().length()>0&&content.getText().toString().length()>0){
                    titleText = title.getText().toString();
                    contentText = content.getText().toString();
                    new  SendArticle().execute();
                }
                else
                {
                    title.setError("Judul tidak boleh kososng");
                    content.setError("Content tidak boleh kososng");
                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();
                int temp = (filePath.split("/").length)-1;
                String imageNameTemp = filePath.split("/")[temp];

                //String file_extn = filePath.substring(filePath.lastIndexOf(".")+1);
                imageName.setText(imageNameTemp);
            }
            else{
                filePath="";
                imageName.setText("No Image Selected");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class SendArticle extends AsyncTask<Void,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result="";


            HttpParams myParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(myParams, 5000);
            HttpConnectionParams.setSoTimeout(myParams, 5000);
            /* example for adding an image part */
            if (filePath.length()>0) {
                HttpClient client = new DefaultHttpClient(myParams);

                HttpPost post = new HttpPost(DataManager.restAPIurl+"article/do_upload");

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            /* example for setting a HttpMultipartMode */
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                FileBody fileBody = new FileBody(new File(filePath));
                builder.addPart("userfile", fileBody);
                post.setEntity(builder.build());
                try {
                    HttpResponse response = client.execute(post);
                    HttpEntity resEntity = response.getEntity();
                    result = EntityUtils.toString(resEntity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("image post", result);
            }

            JSONObject object = new JSONObject();
            try {

                object.put("title",titleText);
                object.put("content",contentText);
                if(result.length()==0)
                    object.put("img","");
                else {
                    try {
                        JSONObject resultObj = new JSONObject(result);
                        object.put("img", resultObj.getString("filename"));
                    } catch (JSONException e) {
                        Toast.makeText(PostArticle.this, "Failed to upload image ", Toast.LENGTH_LONG).show();
                        //object.put("img", resultObj.getString("filename"));
                        object.put("img", "");
                    }
                }

                object.put("user_id",session.getuserid());
                object.put("device_id",session.getdeviceid());


                String json = object.toString();
                Log.e("json",json);
                HttpClient httpclient = new DefaultHttpClient(myParams);


                HttpPost httppost = new HttpPost(DataManager.restAPIurl+"article/add");
                httppost.setHeader("Content-type", "application/json");

                StringEntity se = new StringEntity(json);
                //se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);

                HttpResponse response = httpclient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
                Log.e("hello", result);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("postresult", s);
            try {
                JSONObject result = new JSONObject(s);
                Toast.makeText(PostArticle.this, result.getString("success"), Toast.LENGTH_LONG).show();
                PostArticle.this.finish();

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(PostArticle.this,"Failed to post article ",Toast.LENGTH_LONG).show();
            }

        }
    }
}
