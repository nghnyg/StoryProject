package com.igones.android.storyproject;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.igones.android.storyproject.controllers.DataBaseHelper;
import com.igones.android.storyproject.models.Story;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    ScrollView scrollView;
    LinearLayout scrollViewContent;
    ArrayList<Story> storyArrayList;
    View frm1, frm2;
    private DataBaseHelper db;
    private int currentSeq = 1;
    private boolean isOptions;
    private Story item1;
    private Story item2;
    View v;
    ImageView iconSettings, iconInfo, iconRefresh, iconVolume, iconMusic, iconShare;
    ArrayList<View> menuList;
    LinearLayout menuLayout;
    FrameLayout f, frmSetting, frmRefresh;
    MediaPlayer mediaPlayer = null;
    MediaPlayer efect = null;
    Button buttonCancel, buttonOk;
    private static final int VOLUME_OPENED = 1;
    private static final int VOLUME_MUTE = 0;
    private static final int MUSIC_OPENED = 1;
    private static final int MUSIC_CLOSED = 0;
    int storyCounts = 0;
    int menuPointer = 0;


    private Handler storyHandler = new Handler();
    public Runnable getSentenceTask = new Runnable() {


        @Override
        public void run() {
            storyArrayList = db.getItems(currentSeq);
            if (storyArrayList.size() == 0)
                return;

            if (storyArrayList.size() == 1) {
                isOptions = false;
                item1 = storyArrayList.get(0);
                item2 = null;
            } else {
                isOptions = true;
                item1 = storyArrayList.get(0);
                item2 = storyArrayList.get(1);
            }


            if (!isOptions && item1.getType().equals("SYSTEM")) {

                v.setVisibility(VISIBLE);
                scrollView.setVisibility(View.GONE);
                TextView tv = (TextView) findViewById(R.id.tv);

                Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/vinque.ttf");
                tv.setTypeface(typeFace);
                scaleView(v);

                currentSeq = item1.getNext_seq();
                storyHandler.postDelayed(getSentenceTask, item1.getInterval() * 1000);


            } else {
                scrollView.setVisibility(VISIBLE);
                v.setVisibility(View.GONE);

                if (isOptions) {

                    MediaPlayer efect = MediaPlayer.create(getApplicationContext(), R.raw.event_8);

                    if ((int) iconVolume.getTag() == VOLUME_OPENED) {
                        efect.start();
                    }

                    View itemView = getLayoutInflater().inflate(R.layout.story_choise_item, null);
                    TextView txt = (TextView) itemView.findViewById(R.id.txt);
                    TextView txt2 = (TextView) itemView.findViewById(R.id.txt2);
                    txt.setText(item1.getText());
                    txt2.setText(item2.getText());

                    Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/vinque.ttf");
                    txt.setTypeface(typeFace);
                    txt2.setTypeface(typeFace);
                    frm1 = itemView.findViewById(R.id.frm1);
                    frm2 = itemView.findViewById(R.id.frm2);

                    scrollViewContent.addView(itemView);
                    scaleView(itemView);


                    frm1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            currentSeq = item1.getNext_seq();

                            storyHandler.post(getSentenceTask);
                            frm2.setAlpha(0.5f);

                        }
                    });

                    frm2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            currentSeq = item2.getNext_seq();
                            frm1.setAlpha(0.5f);
                            storyHandler.post(getSentenceTask);


                        }
                    });

                } else {

                    View itemView = getLayoutInflater().inflate(R.layout.story_item, null);
                    TextView txt = (TextView) itemView.findViewById(R.id.txt);
                    txt.setText(item1.getText());
                    itemView.setBackgroundColor(Color.TRANSPARENT);
                    Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/vinque.ttf");
                    txt.setTypeface(typeFace);
                    scrollViewContent.addView(itemView);
                    scaleView(itemView);
                    MediaPlayer efect = MediaPlayer.create(getApplicationContext(), R.raw.event_8);
                    if ((int) iconVolume.getTag() == VOLUME_OPENED) {
                        efect.start();

                    }


                    currentSeq = item1.getNext_seq();
                    storyHandler.postDelayed(getSentenceTask, item1.getInterval() * 1000);
                }
                scrollView.post(new Runnable() {

                    @Override
                    public void run() {

                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

            }

        }

    };


    @Override
    public void onClick(View view) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iconInfo = (ImageView) findViewById(R.id.icon_info);
        iconRefresh = (ImageView) findViewById(R.id.icon_refresh);
        iconVolume = (ImageView) findViewById(R.id.icon_volume);
        iconMusic = (ImageView) findViewById(R.id.icon_music);
        iconShare = (ImageView) findViewById(R.id.icon_share);
        menuLayout = (LinearLayout) findViewById(R.id.menuL);
        iconSettings = (ImageView) findViewById(R.id.icon_settings);
        v = findViewById(R.id.v);
        db = new DataBaseHelper(getApplicationContext());


        final Dialog dialog = new Dialog(this, R.style.InfoDialogTheme);
        dialog.setContentView(R.layout.info);

        iconVolume.setTag(VOLUME_OPENED);
        iconMusic.setTag(MUSIC_OPENED);
        iconVolume.setImageResource(R.drawable.icon_volume_high);
        iconMusic.setImageResource(R.drawable.icon_music);

        menuList = new ArrayList<>();
        menuList.add(iconInfo);
        menuList.add(iconRefresh);
        menuList.add(iconVolume);
        menuList.add(iconMusic);
        menuList.add(iconShare);


        storyCounts = db.getStoryCount();
        db.close();


        final Dialog refreshDialog = new Dialog(this, R.style.InfoDialogTheme);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/vinque.ttf");
        refreshDialog.setContentView(R.layout.refresh);
        buttonCancel = (Button) refreshDialog.findViewById(R.id.buttonCancel);
        buttonCancel.setTypeface(typeFace);
        buttonOk = (Button) refreshDialog.findViewById(R.id.buttonOk);
        buttonOk.setTypeface(typeFace);

        final LinearLayout refresh = (LinearLayout) refreshDialog.findViewById(R.id.refresh);

        TextView txtRefresh = (TextView) refreshDialog.findViewById(R.id.txtRefresh);
        txtRefresh.setTypeface(typeFace);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refreshDialog.dismiss();
            }
        });


        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeq != 0) currentSeq = 1;
                scrollViewContent.removeAllViews();
                storyHandler.removeCallbacksAndMessages(null);
                storyHandler.post(getSentenceTask);
                refreshDialog.dismiss();
            }
        });

        iconRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer == null)
                    return;
                mediaPlayer.pause();
                refreshDialog.show();
                closeMenuItemsImmediately();
            }


        });


        frmSetting = (FrameLayout) findViewById(R.id.frmSetting);
        iconSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float fromDegree;
                float toDegree;


                if (menuLayout.getVisibility() == VISIBLE) {

                    toDegree = 120f;
                    fromDegree = 0f;
                } else {
                    frmSetting.setBackgroundColor(Color.parseColor("#B3000000"));
                    toDegree = 0f;
                    fromDegree = 120f;
                }

                RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree,
                        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);

                AnimationSet set = new AnimationSet(true);
                set.addAnimation(rotate);
                set.setDuration(1000);

                iconSettings.startAnimation(set);


                if (menuLayout.getVisibility() == VISIBLE) {

                    menuPointer = 4;
                    closeMenuItems();
                } else {
                    menuLayout.setVisibility(VISIBLE);
                    menuPointer = 0;
                    openMenuItems();

                }

                for (int i = 0; i < menuList.size(); i++) ;

            }
        });

        iconVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int) iconVolume.getTag() == VOLUME_OPENED) {

                    iconVolume.setTag(VOLUME_MUTE);
                    iconVolume.setImageResource(R.drawable.icon_vol_mute);

                } else {
                    iconVolume.setTag(VOLUME_OPENED);
                    iconVolume.setImageResource(R.drawable.icon_volume_high);

                }

            }
        });


        iconShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "THE FACILITY");
                startActivity(shareIntent);
            }

        });

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollViewContent = (LinearLayout) findViewById(R.id.scrollViewContent);


        final FrameLayout frmInfo = (FrameLayout) dialog.findViewById(R.id.frmInfo);
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/vinque.ttf");
        final TextView txtProgressBar = (TextView) dialog.findViewById(R.id.txtProgressBar);
        TextView progressTxt = (TextView) dialog.findViewById(R.id.progressTxt);
        TextView infoName1 = (TextView) dialog.findViewById(R.id.infoName1);
        TextView infoName2 = (TextView) dialog.findViewById(R.id.infoName2);
        TextView progressT = (TextView) dialog.findViewById(R.id.progressT);
        progressTxt.setTypeface(typeFace);
        infoName1.setTypeface(typeFace);
        infoName2.setTypeface(typeFace);
        progressT.setTypeface(typeFace);
        txtProgressBar.setTypeface(typeFace);

        iconInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtProgressBar.setText(String.valueOf((int) (currentSeq * 100 / storyCounts)));
                dialog.show();
                closeMenuItemsImmediately();

            }
        });

        ImageView icon_closed = (ImageView) dialog.findViewById(R.id.icon_close);
        icon_closed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.dismiss();


        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bg);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                mediaPlayer.start();
            }
        });


        iconMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int) iconMusic.getTag() == MUSIC_OPENED) {

                    iconMusic.setTag(MUSIC_CLOSED);
                    iconMusic.setImageResource(R.drawable.icon_music_close);

                    if (mediaPlayer == null)
                        return;
                    mediaPlayer.pause();

                } else {
                    iconMusic.setTag(MUSIC_OPENED);
                    iconMusic.setImageResource(R.drawable.icon_music);
                    mediaPlayer.start();

                }

            }
        });
        storyHandler.post(getSentenceTask);
    }


    public void scaleView(View v) {

        Animation anim = new AlphaAnimation(0.1f, 1f);
        anim.setDuration(500);
        v.startAnimation(anim);
    }

    public void closeMenuItemsImmediately() {

        menuLayout.setVisibility(GONE);
        frmSetting.setBackgroundColor(Color.TRANSPARENT);
        for (View v : menuList) {
            v.setVisibility(View.INVISIBLE);
        }

    }

    public void openMenuItems() {

        if (menuList.size() < 0) {
            return;
        }
        final View v = menuList.get(menuPointer);


        Animation anim = new ScaleAnimation(
                0f, 1f, // Start and end values for the X axis scaling
                0f, 1f, // Start and end values for the Y axis scaling
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,// Pivot point of X scaling
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuPointer++;
                if (menuPointer < menuList.size())
                    openMenuItems();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(anim);
    }

    public void closeMenuItems() {

        if (menuList.size() < 0) {
            return;
        }
        final View v = menuList.get(menuPointer);


        Animation anim = new ScaleAnimation(
                1f, 0f, // Start and end values for the X axis scaling
                1f, 0f, // Start and end values for the Y axis scaling
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,// Pivot point of X scaling
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuPointer--;
                v.setVisibility(View.INVISIBLE);
                if (menuPointer > -1)
                    closeMenuItems();
                else {
                    menuLayout.setVisibility(View.GONE);
                    frmSetting.setBackgroundColor(Color.TRANSPARENT);
                }


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(anim);
    }

}