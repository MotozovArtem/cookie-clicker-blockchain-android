package com.example.klimo.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvPoint;
    private TextView newClick;
    private TextView tvClock;
    private ImageView donutImage;
    private TextView time;
    private Button btnShop;
    private ProgressBar brTime;
    private int points;
    private int dpc;
    private CountDownTimer countDownTimerBoost;
    private boolean flagShop;
    private long timer;
    private long currentTime;
    private Animation rotateAnimation;
    private Animation timeAnumation;
    private Animation donutClickAnimation;
    private long timeBoost;
    private long currentTimeBoost;
    private CountDownTimer countDownTimer;
    private AlertDialog.Builder ad;
    private Context context;
    DatabaseHandler db;



    private static final String FORMAT = "%02d:%02d";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPoint = (TextView) findViewById(R.id.tvPoints);
        btnShop = (Button) findViewById(R.id.btnShop);
        time = (TextView) findViewById(R.id.time);
        brTime = (ProgressBar) findViewById(R.id.prBar);
        donutImage = (ImageView) findViewById(R.id.imageDonut);
        tvClock = (TextView) findViewById(R.id.tvClock);
        newClick = (TextView) findViewById(R.id.newClick);
        context = MainActivity.this;

        db = new DatabaseHandler(this);

        if ( savedInstanceState == null )   // приложение запущено впервые
        {
            dpc = 1;
            points = 0;
            flagShop = false;
            currentTime = 0;
            currentTimeBoost = 0;
        }
        else
        {
            dpc = savedInstanceState.getInt("dpc_state");
            points = savedInstanceState.getInt("points_state");
            flagShop = savedInstanceState.getBoolean("flagShop_state");
            currentTime = savedInstanceState.getLong("currentTime_state");
            currentTimeBoost = savedInstanceState.getLong("currentTimeBoost_state");
        }


        newClick.setVisibility(View.INVISIBLE);
        timeAnumation = AnimationUtils.loadAnimation(this, R.anim.timer_animation);
        rotateAnimation = AnimationUtils.loadAnimation(this,R.anim.donut_rotate);
        donutClickAnimation = AnimationUtils.loadAnimation(this,R.anim.donut_animation);
        donutClickAnimation.setAnimationListener(new SimpleAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                newClick.setVisibility(View.INVISIBLE);
                donutImage.startAnimation(rotateAnimation);
            }

            @Override
            public void onAnimationStart(Animation animation) {
                super.onAnimationStart(animation);
                donutClick();
                newClick.setVisibility(View.VISIBLE);
            }
        });

        ad = new AlertDialog.Builder(context);
        ad.setTitle(R.string.titleDialog);
        ad.setIcon(R.drawable.donut_icon);
        ad.setPositiveButton(R.string.continueGameDialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                finish();
            }
        });
        ad.setCancelable(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        tvPoint.setText(Integer.toString(points));
        donutImage.startAnimation(rotateAnimation);
        tvPoint.setTextColor(getResources().getColor(R.color.colorPoint));
        if (currentTime != 0) {
            timer = currentTime;
        } else timer = 30000;

        if(db.getPointsCount() == 10) {
            db.deleteAll();
        }
        countDownTimer = new CountDownTimer(timer, 1000) {

            public void onTick(long millisUntilFinished) {

                if (millisUntilFinished<=20000) {

                    tvClock.startAnimation(timeAnumation);
                }
                    tvClock.setText(""+String.format(FORMAT,
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    currentTime = millisUntilFinished;
                }

            public void onFinish() {
                if(flagShop) {
                    countDownTimerBoost.cancel();
                }
                donutImage.clearAnimation();
                tvClock.setText(R.string.endGame);
                ad.setMessage("Вы набрали "+points+" очков");
                ad.show();

                db.addPoints(points);
            }
        };
        if(flagShop) {
            if (currentTimeBoost != 0) {
                timeBoost = currentTimeBoost;
            } else {
                timeBoost = 20000;
            }
            countDownTimerBoost = new CountDownTimer(timeBoost,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    time.setVisibility(View.VISIBLE);
                    brTime.setVisibility(View.VISIBLE);
                    btnShop.setEnabled(false);
                    time.setText("Осталось: "+millisUntilFinished/1000+" секунд");
                    brTime.setProgress((int)millisUntilFinished/1000);
                    currentTimeBoost = millisUntilFinished;
                    if (millisUntilFinished/1000<=5) {
                        time.setTextColor(Color.RED);
                    }
                }

                @Override
                public void onFinish() {
                    btnShop.setEnabled(true);
                    time.setVisibility(View.GONE);
                    brTime.setVisibility(View.GONE);
                    currentTimeBoost = 0;
                    timeBoost = 20000;
                    dpc = 1;
                    flagShop = false;
                }
            };
            countDownTimerBoost.start();
        } else {
            time.setVisibility(View.GONE);
            brTime.setVisibility(View.GONE);
        }
        countDownTimer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        donutImage.clearAnimation();
        countDownTimer.cancel();
        if (flagShop) {
            countDownTimerBoost.cancel();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("dpc_state", dpc);
        outState.putInt("points_state", points);
        outState.putBoolean("flagShop_state", flagShop);
        outState.putLong("currentTime_state", currentTime);
        outState.putLong("currentTimeBoost_state", currentTimeBoost);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dpc = savedInstanceState.getInt("dpc_state");
        points = savedInstanceState.getInt("points_state");
        flagShop = savedInstanceState.getBoolean("flagShop_state");
        currentTime = savedInstanceState.getLong("currentTime_state");
        currentTimeBoost = savedInstanceState.getLong("currentTimeBoost_state");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageDonut) {
            v.startAnimation(donutClickAnimation);
        } else if (v.getId() == R.id.btnShop) {
            showShopFragment();
        }
    }

    private void donutClick() {
        this.points+=this.dpc;
        tvPoint.setText(Integer.toString(points));
        newClick.setText("+"+this.dpc);
    }

    private void showShopFragment() {
        Intent intent = new Intent(this, ShopActivity.class);
        intent.putExtra("points", points);
        intent.putExtra("dpc", dpc);
        intent.putExtra("currentTime", currentTime);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        dpc = data.getIntExtra("dpc", dpc);
        points = data.getIntExtra("points", points);
        flagShop = data.getBooleanExtra("flagShop", flagShop);
        currentTime=data.getLongExtra("currentTime", currentTime);
    }


}
