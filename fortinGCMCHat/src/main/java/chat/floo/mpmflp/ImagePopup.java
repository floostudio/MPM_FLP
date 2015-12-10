package chat.floo.mpmflp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ImagePopup extends Activity {

    ImageView imageView;
    public static String KEY_IMAGE_PATH = "imagePath";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_popup);
        String imagePath="";
        if(getIntent().getExtras()!=null)
        {
            imagePath = getIntent().getStringExtra(KEY_IMAGE_PATH);
        }
        imageView = (ImageView)findViewById(R.id.imageView);
        //
        // new MyImageDownloader(imageView,true,false).execute(DataManager.dirUrl+imagePath);
        //MyImageLoader imgLoader = new MyImageLoader(this);
        //imgLoader.displayImage(DataManager.dirUrl+imagePath,imageView);
        ImageLoader imgLoader = ImageLoader.getInstance();
        imgLoader.displayImage(DataManager.dirUrl+imagePath,imageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_image_popup, menu);
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
}
