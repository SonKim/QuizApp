package com.example.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.quizapp.QuizContract.*;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class QuizDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyQuiz";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase sqLiteDatabase;
    public QuizDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
        final String CREATE_TABLE = "CREATE TABLE "+QuestionTable.TABLE_NAME+"("+QuestionTable._ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                ""+QuestionTable.COLUMN_QUESTION+" NVARCHAR, " +
                ""+QuestionTable.COLUMN_OPTION1+" NVARCHAR, " +
                ""+QuestionTable.COLUMN_OPTION2+" NVARCHAR, " +
                ""+QuestionTable.COLUMN_OPTION3+" NVARCHAR, " +
                ""+QuestionTable.COLUMN_OPTION4+" NVARCHAR, " +
                ""+QuestionTable.COLUMN_ANSWER_NR+" INTEGER)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        fillQuestionTable();
    }

    private void fillQuestionTable() {
        Question question1 = new Question("Hello là gì","Xin chào","Ô tô","Bút","Sách",1);
        addQuestion(question1);

        Question question2 = new Question("Car là gì","Xin chào","Ô tô","Bút","Sách",2);
        addQuestion(question2);

        Question question3 = new Question("Pen là gì","Xin chào","Ô tô","Bút","Sách",3);
        addQuestion(question3);

        Question question4 = new Question("Book là gì","Xin chào","Ô tô","Bút","Sách",4);
        addQuestion(question4);

        Question question5 = new Question("No là gì","Không","Ô tô","Bút","Sách",1);
        addQuestion(question5);
    }
    private void addQuestion(Question question){
        ContentValues values = new ContentValues();
        values.put(QuestionTable.COLUMN_QUESTION,question.getQuestion());
        values.put(QuestionTable.COLUMN_OPTION1,question.getOption1());
        values.put(QuestionTable.COLUMN_OPTION2,question.getOption2());
        values.put(QuestionTable.COLUMN_OPTION3,question.getOption3());
        values.put(QuestionTable.COLUMN_OPTION4,question.getOption4());
        values.put(QuestionTable.COLUMN_ANSWER_NR,question.getAnswerNr());
        sqLiteDatabase.insert(QuestionTable.TABLE_NAME,null,values);
    }
    public ArrayList<Question> getAllQuestion(){
        ArrayList<Question> questions = new ArrayList<>();
        sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+QuestionTable.TABLE_NAME,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            while (cursor.isAfterLast()==false){
                Question question = new Question();
                question.setQuestion(cursor.getString(1));
                question.setOption1(cursor.getString(2));
                question.setOption2(cursor.getString(3));
                question.setOption3(cursor.getString(4));
                question.setOption4(cursor.getString(5));
                question.setAnswerNr(cursor.getInt(6));
                questions.add(question);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return questions;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+QuestionTable.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
