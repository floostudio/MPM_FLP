package chat.floo.mpmflp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by SONY_VAIO on 15-Sep-15.
 */
public class DoLogout extends AsyncTask<String, Void, String> {

    ProgressDialog progressDialog;
    Context context;
    SessionManager session;

    public DoLogout(Context context){
        this.context = context;
        session = new SessionManager(this.context);
    }



    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "", "Logging Out", true);

    }

    @Override
    protected String doInBackground(String... params) {

        APIManager.logout(session.getuserid(), session.getdeviceid());

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {

        progressDialog.dismiss();
        if (DataManager.status.equals("Logout successful")) {
            session.logoutUser();
            Intent intent = new Intent(context, LoginActivity.class);
            ((Activity)context).finish();
            context.startActivity(intent);
            Toast.makeText(context,
                    "Logout Successfully..", Toast.LENGTH_LONG).show();

        }
    }
}

