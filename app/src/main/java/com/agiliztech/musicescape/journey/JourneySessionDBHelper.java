package com.agiliztech.musicescape.journey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.agiliztech.musicescape.models.JourneySession;
import com.agiliztech.musicescape.utils.Global;
import com.agiliztech.musicescape.utils.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by praburaam on 27/08/16.
 */
public class JourneySessionDBHelper extends SQLiteOpenHelper {
    public  final String COL_FAVOURITE = "fav";
    public final String COL_JOURNEY_ID = "jid";
    public final String COL_JOURNEY_STARTED = "started";
    public  final String COL_NAME = "name";
    public final String COL_JOURNEY_SESSION_OBJ = "journeysessionjson";

    public JourneySessionDBHelper(Context context) {
        super(context, Global.DBNAME, null, Global.DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String COL_ID = "_id";
        String createSQL = "CREATE TABLE "+Global.JOURNEY_TBL_NAME+"( "
                +COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COL_JOURNEY_ID+" TEXT, "
                +COL_JOURNEY_STARTED+" TEXT, "
                +COL_NAME+" TEXT, "
                +COL_FAVOURITE+" TEXT, "
                +COL_JOURNEY_SESSION_OBJ+" TEXT "
                +");";

        db.execSQL(createSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addJourneySession(JourneySession session){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_JOURNEY_ID, session.getJourneyID());
            contentValues.put(COL_NAME, session.getName());
            contentValues.put(COL_FAVOURITE, session.getFavourite());
            contentValues.put(COL_JOURNEY_SESSION_OBJ, UtilityClass.getJsonConvertor().toJson(session));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            contentValues.put(COL_JOURNEY_STARTED, simpleDateFormat.format(session.getStarted()));
            db.insert(Global.JOURNEY_SESSION_TBL_NAME,null,contentValues);
        }
        catch (Exception e){
            UtilityClass.log(JourneySessionDBHelper.class.getSimpleName(), e.getMessage());
        }
    }

    public List<JourneySession> getFavouriteJourneySessions(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<JourneySession> journeys = null;

        try{
            Cursor cursor = db.rawQuery("select * from "+Global.JOURNEY_SESSION_TBL_NAME
                    +" where "+COL_FAVOURITE+" = '1' "
                    +" order by "+COL_NAME,null);

            if (cursor .moveToFirst()) {
                journeys = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    String journeyStr = cursor.getString(cursor
                            .getColumnIndex(COL_JOURNEY_SESSION_OBJ));
                    JourneySession j = UtilityClass.getJsonConvertor().fromJson(journeyStr, JourneySession.class);
                    journeys.add(j);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        catch (Exception e){
            UtilityClass.log(JourneySessionDBHelper.class.getSimpleName(), e.getMessage());
        }
        return journeys;
    }

    public List<JourneySession> getTopEightSessions(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<JourneySession> journeys = null;
        try{
            String query = "SELECT * FROM "+Global.JOURNEY_SESSION_TBL_NAME
                    +" ORDER BY date("+COL_JOURNEY_STARTED+") desc limit 8";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor .moveToFirst()) {
                journeys = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    String journeyStr = cursor.getString(cursor
                            .getColumnIndex(COL_JOURNEY_SESSION_OBJ));
                    JourneySession j = UtilityClass.getJsonConvertor().fromJson(journeyStr, JourneySession.class);
                    journeys.add(j);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        catch (Exception e){
            UtilityClass.log(JourneySessionDBHelper.class.getSimpleName(), e.getMessage());
        }
        return journeys;
    }

    public void clearEntriesOlderThan(Date lastDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = simpleDateFormat.format(lastDate);
            String query = "DELETE FROM "+Global.JOURNEY_SESSION_TBL_NAME
                    +" WHERE date("+COL_JOURNEY_STARTED+") < "+dateStr
                    +" and "+COL_FAVOURITE+" = '0'";

            db.execSQL(query);
        }
        catch (Exception e){
            UtilityClass.log(JourneySessionDBHelper.class.getSimpleName(), e.getMessage());
        }
    }
}