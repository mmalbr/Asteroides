package meafocus.asteriodes.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import meafocus.asteriodes.R;
import meafocus.asteriodes.data.WeaponData;
import meafocus.asteriodes.utils.AchievementUtils;
import meafocus.asteriodes.utils.FontUtils;
import meafocus.asteriodes.utils.ImageUtils;
import meafocus.asteriodes.utils.PreferenceUtils;
import meafocus.asteriodes.views.GameView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity
        implements GameView.GameListener, View.OnClickListener {
    private InterstitialAd mInterstitialAd;

    private TextView titleView;
    private TextView highScoreView;
    private TextView hintView;
    private ImageView musicView;
    private ImageView soundView;
    private ImageView aboutView;
    private LinearLayout buttonLayout;
    private ImageView pauseView;
    private ImageView stopView;
    private GameView gameView;

    private ValueAnimator animator;
    private String appName, hintStart;

    private SoundPool soundPool;
    private int explosionId;
    private int explosion2Id;
    private int buttonId;
    private int hissId;
    private int upgradeId;
    private int replenishId;
    private int errorId;

    private SharedPreferences prefs;

    private boolean isSound;
    private Bitmap soundEnabled;
    private Bitmap soundDisabled;

    private MediaPlayer player;
    private boolean isMusic;
    private Bitmap musicEnabled;
    private Bitmap musicDisabled;

    private boolean isPaused;
    private Bitmap play;
    private Bitmap pause;

    private AchievementUtils achievementUtils;

    private final Handler handler = new Handler();
    private final Runnable hintRunnable = new Runnable() {
        @Override
        public void run() {
            if (!gameView.isPlaying() && (animator == null || !animator.isStarted())) {
                if (!hintView.getText().toString().contains("."))
                    hintView.setText(String.format(".%s.", hintStart));
                else if (hintView.getText().toString().contains("..."))
                    hintView.setText(hintStart);
                else if (hintView.getText().toString().contains(".."))
                    hintView.setText(String.format("...%s...", hintStart));
                else if (hintView.getText().toString().contains("."))
                    hintView.setText(String.format("..%s..", hintStart));

                if (highScoreView.getVisibility() == View.VISIBLE)
                    highScoreView.setVisibility(View.GONE);
                else highScoreView.setVisibility(View.VISIBLE);
            }

            if (isPaused) {
                if (pauseView.getAlpha() == 1)
                    pauseView.setAlpha(0.5f);
                else pauseView.setAlpha(1f);
            }

            handler.postDelayed(this, 1000);
        }


    };

    private void prepareAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6944068380993727/5906000395");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
prepareAd();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                if (mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }else {
                    prepareAd();
                }

            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.


            }
        });

        //banner admob - Início
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //banner admob - Fim


        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        titleView = findViewById(R.id.title);
        highScoreView = findViewById(R.id.highScore);
        hintView = findViewById(R.id.hint);
        buttonLayout = findViewById(R.id.buttonLayout);
        musicView = findViewById(R.id.music);
        soundView = findViewById(R.id.sound);
        ImageView achievementsView = findViewById(R.id.achievements);
        ImageView rankView = findViewById(R.id.rank);
        ImageView gamesView = findViewById(R.id.games);
        aboutView = findViewById(R.id.about);
        pauseView = findViewById(R.id.pause);
        stopView = findViewById(R.id.stop);
        gameView = findViewById(R.id.game);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .build();
        } else soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        explosionId = soundPool.load(this, R.raw.explosion, 1);
        explosion2Id = soundPool.load(this, R.raw.explosion_two, 1);
        buttonId = soundPool.load(this, R.raw.button, 1);
        hissId = soundPool.load(this, R.raw.hiss, 1);
        replenishId = soundPool.load(this, R.raw.replenish, 1);
        upgradeId = soundPool.load(this, R.raw.upgrade, 1);
        errorId = soundPool.load(this, R.raw.error, 1);
        WeaponData.loadSounds(this, soundPool);

        Typeface typeface = FontUtils.getTypeface(this);
        int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        int colorAccent = ContextCompat.getColor(this, R.color.colorAccent);

        titleView.setTypeface(typeface);
        titleView.setPaintFlags(titleView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        titleView.getPaint().setShader(new LinearGradient(
                0, 0, 0,
                titleView.getLineHeight(),
                colorAccent,
                colorPrimary,
                Shader.TileMode.REPEAT
        ));

        highScoreView.setTypeface(typeface);
        highScoreView.getPaint().setShader(new LinearGradient(
                0, 0, 0,
                hintView.getLineHeight(),
                colorAccent,
                colorPrimary,
                Shader.TileMode.REPEAT
        ));

        hintView.setTypeface(typeface);
        hintView.getPaint().setShader(new LinearGradient(
                0, 0, 0,
                hintView.getLineHeight(),
                colorAccent,
                colorPrimary,
                Shader.TileMode.REPEAT
        ));

        appName = getString(R.string.app_name);
        hintStart = getString(R.string.hint_start);

        isMusic = prefs.getBoolean(PreferenceUtils.PREF_MUSIC, true);
        musicEnabled = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_music_enabled), colorAccent, colorPrimary);
        musicDisabled = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_music_disabled), colorAccent, colorPrimary);
        musicView.setImageBitmap(isMusic ? musicEnabled : musicDisabled);
        musicView.setOnClickListener(view -> {
            isMusic = !isMusic;
            prefs.edit().putBoolean(PreferenceUtils.PREF_MUSIC, isMusic).apply();
            musicView.setImageBitmap(isMusic ? musicEnabled : musicDisabled);

            if (isMusic) player.start();
            else player.pause();

            if (isSound)
                soundPool.play(buttonId, 1, 1, 0, 0, 1);
        });

        isSound = prefs.getBoolean(PreferenceUtils.PREF_SOUND, true);
        soundEnabled = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_sound_enabled), colorAccent, colorPrimary);
        soundDisabled = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_sound_disabled), colorAccent, colorPrimary);
        soundView.setImageBitmap(isSound ? soundEnabled : soundDisabled);
        soundView.setOnClickListener(view -> {
            isSound = !isSound;
            prefs.edit().putBoolean(PreferenceUtils.PREF_SOUND, isSound).apply();
            soundView.setImageBitmap(isSound ? soundEnabled : soundDisabled);
            if (isSound)
                soundPool.play(buttonId, 1, 1, 0, 0, 1);
        });

        achievementsView.setImageBitmap(ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_achievements), colorAccent, colorPrimary));
        achievementsView.setOnClickListener(view -> {
            if (isSound)
                soundPool.play(buttonId, 1, 1, 0, 0, 1);
        });

        rankView.setImageBitmap(ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_rank), colorAccent, colorPrimary));
        rankView.setOnClickListener(view -> {
            if (isSound)
                soundPool.play(buttonId, 1, 1, 0, 0, 1);
        });

        gamesView.setImageBitmap(ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_game), colorAccent, colorPrimary));

        aboutView.setImageBitmap(ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_info), colorAccent, colorPrimary));
        aboutView.setOnClickListener(view -> {
            if (!gameView.isPlaying() && (animator == null || !animator.isStarted())) {
                gameView.setOnClickListener(null);
                gameView.play(true);
                animateTitle(false);
                if (isSound)
                    soundPool.play(hissId, 1, 1, 0, 0, 1);
            }
        });

        play = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_play), colorAccent, colorPrimary);
        pause = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_pause), colorAccent, colorPrimary);
        Bitmap stop = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_stop), colorAccent, colorPrimary);

        pauseView.setImageBitmap(pause);
        pauseView.setOnClickListener(view -> {
            isPaused = !isPaused;
            if (isPaused) {
                pauseView.setImageBitmap(play);
                if (!gameView.isTutorial())
                    stopView.setVisibility(View.VISIBLE);
                gameView.onPause();
            } else {
                pauseView.setImageBitmap(pause);
                pauseView.setAlpha(1f);
                stopView.setVisibility(View.GONE);
                gameView.onResume();
            }
        });

        stopView.setImageBitmap(stop);
        stopView.setOnClickListener(view -> {
            if (isPaused) {
                pauseView.setImageBitmap(pause);
                pauseView.setAlpha(1f);
                gameView.onResume();
                isPaused = false;
            }

            onStop(gameView.score);
            gameView.stop();
        });

        int highScore = prefs.getInt(PreferenceUtils.PREF_HIGH_SCORE, 0);
        if (highScore > 0)
            highScoreView.setText(String.format(getString(R.string.score_high), highScore));

        player = MediaPlayer.create(this, R.raw.music);
        player.setLooping(true);
        if (isMusic)
            player.start();

        handler.postDelayed(hintRunnable, 1000);

        gameView.setListener(this);
        gameView.setOnClickListener(this);
        animateTitle(true);

    }

    private void animateTitle(final boolean isVisible) {
        highScoreView.setVisibility(View.GONE);

        animator = ValueAnimator.ofFloat(isVisible ? 0 : 1, isVisible ? 1 : 0);
        animator.setDuration(750);
        animator.setStartDelay(500);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> {
            titleView.setText(appName.substring(0, (int) ((float) valueAnimator.getAnimatedValue() * appName.length())));
            hintView.setText(hintStart.substring(0, (int) ((float) valueAnimator.getAnimatedValue() * hintStart.length())));
        });
        animator.start();

        if (isVisible) {
            buttonLayout.setVisibility(View.VISIBLE);
            pauseView.setVisibility(View.GONE);
            stopView.setVisibility(View.GONE);


            if (prefs.getBoolean(PreferenceUtils.PREF_TUTORIAL, true))
                aboutView.setVisibility(View.GONE);
            else aboutView.setVisibility(View.VISIBLE);
        } else {
            buttonLayout.setVisibility(View.GONE);
            pauseView.setVisibility(View.VISIBLE);
            stopView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        if (isMusic)
            player.pause();
        if (gameView != null && !isPaused)
            gameView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMusic)
            player.start();
        if (gameView != null && !isPaused)
            gameView.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Settings.Global.getFloat(getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1) != 1) {
                try {
                    ValueAnimator.class.getMethod("setDurationScale", float.class).invoke(null, 1f);
                } catch (Throwable t) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.title_animation_speed)
                            .setMessage(R.string.desc_animation_speed)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                try {
                                    startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                                } catch (Exception ignored) {
                                }
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) ->
                                    dialogInterface.dismiss())
                            .create()
                            .show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }

    @Override
    public void onStart(boolean isTutorial) {
        if (achievementUtils != null)
            achievementUtils.onStart(isTutorial);

    }

    @Override
    public void onTutorialFinish() {
        if (achievementUtils != null)
            achievementUtils.onTutorialFinish();

        prefs.edit().putBoolean(PreferenceUtils.PREF_TUTORIAL, false).apply();
    }

    @Override
    public void onStop(int score) {
        animateTitle(true);
        gameView.setOnClickListener(this);
        int highScore = prefs.getInt(PreferenceUtils.PREF_HIGH_SCORE, 0);
        if (score > highScore) {
            //TODO: awesome high score animation or something
            highScore = score;
            prefs.edit().putInt(PreferenceUtils.PREF_HIGH_SCORE, score).apply();

        }

        highScoreView.setText(String.format(getString(R.string.score_high), highScore));

        if (achievementUtils != null)
            achievementUtils.onStop(score);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }else {
                    prepareAd();
                }            }
        },1500);

    }

    @Override
    public void onAsteroidPassed() {
        if (achievementUtils != null)
            achievementUtils.onAsteroidPassed();

    }

    @Override
    public void onAsteroidCrashed() {
        if (isSound)
            soundPool.play(explosion2Id, 1, 1, 0, 0, 1);
        if (achievementUtils != null)
            achievementUtils.onAsteroidCrashed();

    }

    @Override
    public void onWeaponUpgraded(WeaponData weapon) {
        if (isSound)
            soundPool.play(upgradeId, 1, 1, 0, 0, 1);
        if (achievementUtils != null)
            achievementUtils.onWeaponUpgraded(weapon);
    }

    @Override
    public void onAmmoReplenished() {
        if (isSound)
            soundPool.play(replenishId, 1, 1, 0, 0, 1);
        if (achievementUtils != null)
            achievementUtils.onAmmoReplenished();
    }

    @Override
    public void onProjectileFired(WeaponData weapon) {
        if (isSound)
            soundPool.play(weapon.soundId, 1, 1, 0, 0, 1);
        if (achievementUtils != null)
            achievementUtils.onProjectileFired(weapon);
    }

    @Override
    public void onOutOfAmmo() {
        if (isSound)
            soundPool.play(errorId, 1, 1, 0, 0, 1);
        if (achievementUtils != null)
            achievementUtils.onOutOfAmmo();
    }

    @Override
    public void onAsteroidHit(int score) {
        titleView.setText(String.valueOf(score));
        if (isSound)
            soundPool.play(explosionId, 1, 1, 0, 0, 1);
        if (achievementUtils != null)
            achievementUtils.onAsteroidHit(score);
    }

    @Override
    public void onClick(View view) {
        if (!gameView.isPlaying() && (animator == null || !animator.isStarted())) {
            gameView.setOnClickListener(null);

            gameView.play(prefs.getBoolean(PreferenceUtils.PREF_TUTORIAL, true));

            animateTitle(false);
            if (isSound)
                soundPool.play(hissId, 1, 1, 0, 0, 1);
        }
    }

}
