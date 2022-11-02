package com.example.quizapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {
    public static final String EXTRA_SCORE = "etracore";
    private static final long COUNTDOWN_IN_MILLIS = 30000;


    private TextView tvQuestion;
    private TextView tvScore;
    private TextView tvQuestionCount;
    private TextView tvCountdown;
    private RadioGroup rbGroup;
    private RadioButton rbOption1;
    private RadioButton rbOption2;
    private RadioButton rbOption3;
    private RadioButton rbOption4;
    private Button btnConfirm;
    private ArrayList<Question> questions;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;

    private long backPressedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        QuizDBHelper dbHelper = new QuizDBHelper(QuizActivity.this);
        getSupportActionBar().setTitle("Ứng dụng học tiếng Anh đơn giản");
        anhXa();
        textColorDefaultRb = rbOption1.getTextColors();
        textColorDefaultCd = tvCountdown.getTextColors();
        questions = dbHelper.getAllQuestion();
        questionCountTotal = questions.size();
        Collections.shuffle(questions);
        showNextQuestion();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!answered){
                    if(rbOption1.isChecked()||rbOption2.isChecked()||rbOption3.isChecked()||rbOption4.isChecked()){
                        checkAnswer();
                    }else {
                        Toast.makeText(QuizActivity.this, "Hãy chọn câu trả lời", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    showNextQuestion();
                }
            }
        });
    }

    private void checkAnswer() {
        answered = true;
        countDownTimer.cancel();
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected)+1;
        if(answerNr==currentQuestion.getAnswerNr()){
            score++;
            tvScore.setText("Score: "+score);
        }
        showSolution();
    }

    private void showSolution() {
        rbOption1.setTextColor(Color.RED);
        rbOption2.setTextColor(Color.RED);
        rbOption3.setTextColor(Color.RED);
        rbOption4.setTextColor(Color.RED);
        switch (currentQuestion.getAnswerNr()){
            case 1:
                rbOption1.setTextColor(Color.GREEN);
                break;
            case 2:
                rbOption2.setTextColor(Color.GREEN);
                break;
            case 3:
                rbOption3.setTextColor(Color.GREEN);
                break;
            case 4:
                rbOption4.setTextColor(Color.GREEN);
                break;
        }
        if(questionCounter<questionCountTotal){
            btnConfirm.setText("Next");
        }else {
            btnConfirm.setText("Finish");
        }
    }

    private void showNextQuestion() {
        rbOption1.setTextColor(textColorDefaultRb);
        rbOption2.setTextColor(textColorDefaultRb);
        rbOption3.setTextColor(textColorDefaultRb);
        rbOption4.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();
        if(questionCounter<questionCountTotal){
            currentQuestion = questions.get(questionCounter);
            tvQuestion.setText(currentQuestion.getQuestion());
            rbOption1.setText(currentQuestion.getOption1());
            rbOption2.setText(currentQuestion.getOption2());
            rbOption3.setText(currentQuestion.getOption3());
            rbOption4.setText(currentQuestion.getOption4());
            questionCounter++;
            tvQuestionCount.setText("Question: "+questionCounter+"/"+questionCountTotal);
            answered = false;
            btnConfirm.setText("Confirm");

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();
        }else {
            finishQuiz();
        }
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                updateCountDownNext();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownNext();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownNext() {
        int minutes = (int) ((timeLeftInMillis/1000)/60);
        int seconds = (int) ((timeLeftInMillis/1000)%60);
        String timeFomartted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        tvCountdown.setText(timeFomartted);
        if(timeLeftInMillis<10000){
            tvCountdown.setTextColor(Color.RED);
        }else {
            tvCountdown.setTextColor(textColorDefaultCd);
        }
    }

    private void finishQuiz() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SCORE,score);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void anhXa() {
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvCountdown = findViewById(R.id.tvCountdown);
        rbGroup = findViewById(R.id.rbGroup);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    @Override
    public void onBackPressed() {
        if(backPressedTime+2000>System.currentTimeMillis()){
            finishQuiz();
        }else {
            Toast.makeText(this, "Nhấn 1 lần nữa để kết thúc", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
    }
}