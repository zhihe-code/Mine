package com.example.mine;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ScoreProvider extends ContentProvider {
    public static final int WORD_DIR=0;
    public static final int WORD_ITEM=1;
    public static final String AUTHOEITY="com.example.mine.provider";
    private static UriMatcher uriMatcher;
    private MyDatabaseHelper databaseHelper;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHOEITY,"score",WORD_DIR);
        uriMatcher.addURI(AUTHOEITY,"score/#",WORD_ITEM);
    }

    public ScoreProvider() {
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)){
            case WORD_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.mine.provider.score";
            case WORD_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.mine.provider.score";
        }
        return null;
    }



    @Override
    public boolean onCreate() {
        databaseHelper = new MyDatabaseHelper(getContext(),"ScoreList.db",null,1);
        // TODO: Implement this to initialize your content provider on startup.
        return true;
    }

    //添加数据
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)){
            case WORD_DIR:
            case WORD_ITEM:
                long  newWordID = db.insert("Score",null,values);
                uriReturn = Uri.parse("content://"+AUTHOEITY+"/score/"+newWordID);
                break;
            default:
                break;

        }
        return uriReturn;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }
}
