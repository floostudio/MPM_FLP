package chat.floo.mpmflp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivateUser extends ActionBarActivity implements AsyncResponse{

    String MPMuserID;
    Button activateButton,cancelButton;
    TextView PIN;
    MyHTTPPost postman;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_user);
        if(getIntent().getExtras()!=null)
        {
            MPMuserID = getIntent().getStringExtra("MPMuserID");
        }

        activateButton = (Button)findViewById(R.id.btnactivate);
        cancelButton = (Button)findViewById(R.id.btncancel);
        PIN = (TextView)findViewById(R.id.activationPin);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<NameValuePair>postParameter = new ArrayList<>();
                postParameter.add(new BasicNameValuePair("user_id",MPMuserID));
                postParameter.add(new BasicNameValuePair("pin",PIN.getText().toString()));

                postman = new MyHTTPPost(ActivateUser.this,DataManager.restAPIurl+"user/activateUser",postParameter);
                postman.delegate=ActivateUser.this;
                postman.execute();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_activate_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processFinish(String output) {
        if(output!="")
        {
            try {
                JSONObject obj = new JSONObject(output);
                String loginStatus = obj.getString("status");

                if (loginStatus.equals("1")) {

                    Toast.makeText(this,"Activation Success",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(this,
                            LoginActivity.class);
                    finish();
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
