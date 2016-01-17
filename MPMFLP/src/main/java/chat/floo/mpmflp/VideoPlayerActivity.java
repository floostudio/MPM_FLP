package chat.floo.mpmflp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoPlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public static final String API_KEY = "AIzaSyCg33CczE6it3-l-Dlaqt8NFFTd-7m4Ers";

    //http://youtu.be/<VIDEO_ID>
    String VIDEO_ID = "TRzLuycvX7A";

    YouTubePlayerView mVideoView;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        //String mUrl = DataManager.dirUrl+"static/videos/SAMPLE_VIDEO.mp4";
        mVideoView = (YouTubePlayerView)findViewById(R.id.videoView);
        mVideoView.initialize(API_KEY,this);
        if(getIntent().getStringExtra("videoKey")!=null)
            VIDEO_ID = getIntent().getStringExtra("videoKey");
        /*final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Buffering video...", true);

        //String mUrl = "http://ia600409.us.archive.org/20/items/ksnn_blue_sky/ksnn_blue_sky.mp4";

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    VideoPlayerActivity.this);
            mediacontroller.setAnchorView(mVideoView);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(mUrl);
            mVideoView.setMediaController(mediacontroller);
            mVideoView.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                mVideoView.start();
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                //finish();
                return true;
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
/** add listeners to YouTubePlayer instance **/
        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {

            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        });
        youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {

            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onStopped() {

            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {

            }
        });

        /** Start buffering **/
        if (!wasRestored) {
            youTubePlayer.cueVideo(VIDEO_ID);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
