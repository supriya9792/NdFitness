package com.ndfitnessplus.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.ndfitnessplus.Model.WorkOutDetailsList;
import com.ndfitnessplus.R;
import com.ndfitnessplus.Utility.Config;
import com.ndfitnessplus.Utility.ServiceUrls;

public class WorkoutDetailsDescriptionActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener  {
    public static String TAG = WorkoutDetailsDescriptionActivity.class.getName();
    ImageView image;
    VideoView videoView;
    TextView WorkoutName,Musculargroup,Set,Repitation,description,Weight;
    WorkOutDetailsList filterArrayList;
    String videoUrl;

    //youtube video playing
    private static final int RECOVERY_REQUEST = 1;
    /// private YouTubePlayerView youTubeView;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private YouTubePlayer player;
    YouTubePlayerSupportFragment frag;
    LinearLayout videoViewLyt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details_description);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(getResources().getString(R.string.menu_workout));
//        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

//        setActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_workout));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponent();
    }
    private void initComponent(){

        //image=findViewById(R.id.image);
        WorkoutName=findViewById(R.id.workout_nameTV);
        Musculargroup=findViewById(R.id.musculargroup);
        description=findViewById(R.id.disc);
        Set=findViewById(R.id.sets);
        Repitation=findViewById(R.id.repitationTV);
        Weight=findViewById(R.id.weightTV);
        videoViewLyt=findViewById(R.id.lay_videoView);


        //videoView=findViewById(R.id.video);
//        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
//        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
//
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
        frag =(YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(Config.YOUTUBE_API_KEY, this);
//        final EditText seekToText = (EditText) findViewById(R.id.seek_to_text);
//        Button seekToButton = (Button) findViewById(R.id.seek_to_button);
//        seekToButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int skipToSecs = Integer.valueOf(seekToText.getText().toString());
//                player.seekToMillis(skipToSecs * 1000);
//            }
//        });

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            filterArrayList = (WorkOutDetailsList) args.getSerializable("filter_array_list");

            // String image=filterArrayList.getProductImage();
            WorkoutName.setText(filterArrayList.getWorkoutName());
            Musculargroup.setText(filterArrayList.getBodyPart());
            description.setText(filterArrayList.getDiscription());
            // Time.setText(filterArrayList.getTime());
            Set.setText(filterArrayList.getSet());
            Repitation.setText(filterArrayList.getRepitation());
            Weight.setText(filterArrayList.getWeight());

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(filterArrayList.getWorkoutName());
            String url= ServiceUrls.IMAGES_URL + filterArrayList.getWorkoutImage();

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.nouser);
            requestOptions.error(R.drawable.nouser);
            int skipToSecs = Integer.valueOf("2000");
            //  player.seekToMillis(2000 * 1000);

//            Glide.with(this)
//                    .setDefaultRequestOptions(requestOptions)
//                    .load(url).into(image);

            String vurl= filterArrayList.getVideoLink();
            Log.e("Video Url", vurl);
            if((vurl.equals("")||vurl.equals("null"))) {
                videoViewLyt.setVisibility(View.GONE);
            }
//            Uri video = Uri.parse(vurl);
//            if((filterArrayList.getVideoLink()==(null))){
//                //videoView.setVisibility(View.INVISIBLE);
//            }else {
//                //Toast.makeText(mContext,"Video url "+ vurl,Toast.LENGTH_SHORT).show();
//                videoView.setVisibility(View.VISIBLE);
//                videoView.setVideoURI(video);
//                videoView.start();
//            }

//            try {
////                // Start the MediaController
////                MediaController mediacontroller = new MediaController(this);
////                mediacontroller.setAnchorView(videoView);
////                // Get the URL from String videoUrl
////                Uri video = Uri.parse("https://www.youtube.com/watch?v=fvwyPGBOEgY");
////                videoView.setMediaController(mediacontroller);
////                videoView.setVideoURI(video);
////                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
////                    public void onPrepared(MediaPlayer mp) {
////                        videoView.start();
////                    }
////                });
////
////            } catch (Exception e) {
////                Log.e("Error", e.getMessage());
////                e.printStackTrace();
////            }


        }

        //new YourAsyncTask().execute();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(WorkoutDetailsDescriptionActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(id== android.R.id.home){
            //Toast.makeText(this,"Navigation back pressed",Toast.LENGTH_SHORT).show();
            // NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return true;
    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        this.player = youTubePlayer;
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        if (!wasRestored) {
            String vurl=filterArrayList.getVideoLink();
            if(!(vurl.equals("")||vurl.equals("null"))) {
                String vsp[] = vurl.split("=");
                if (vsp.length > 1)
                    player.cueVideo(vsp[1]);
                else {
                    videoViewLyt.setVisibility(View.GONE);
//                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WorkoutDetailsDescriptionActivity.this);
//                    builder.setMessage("Invalid url to play video");
//                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.dismiss();
//                        }
//                    });
//                    android.app.AlertDialog dialog = builder.create();
//                    dialog.setCancelable(false);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.show();
                }
            }
            // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = getResources().getString(R.string.something_went);
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return frag;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            // showMessage("Playing");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            //showMessage("Paused");
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            // showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
