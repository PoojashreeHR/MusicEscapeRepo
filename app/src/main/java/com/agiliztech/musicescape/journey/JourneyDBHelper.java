package com.agiliztech.musicescape.journey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.agiliztech.musicescape.models.Journey;
import com.agiliztech.musicescape.utils.Global;
import com.agiliztech.musicescape.utils.UtilityClass;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by praburaam on 25/08/16.
 */
public class JourneyDBHelper extends SQLiteOpenHelper {

    public static final String COL_NAME = "name";
    public static final String COL_GEN_BY = "generatedby";
    private final String COL_JOURNEY_OBJ_STR = "journeyjson";


    public JourneyDBHelper(Context context) {
        super(context, Global.DBNAME, null, Global.DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String COL_ID = "_id";
        String createSQL = "CREATE TABLE "+Global.JOURNEY_TBL_NAME+"( "
                +COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COL_NAME+" TEXT, "
                +COL_GEN_BY+" TEXT, "
                +COL_JOURNEY_OBJ_STR+" TEXT "
                +");";

        db.execSQL(createSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addJourney(Journey journey){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_NAME, journey.getName());
            contentValues.put(COL_GEN_BY, journey.getGeneratedBy());
            contentValues.put(COL_JOURNEY_OBJ_STR, UtilityClass.getJsonConvertor().toJson(journey));
            db.insert(Global.JOURNEY_TBL_NAME,null,contentValues);
        }
        catch (Exception e){
            UtilityClass.log(JourneyDBHelper.class.getSimpleName(), e.getMessage());
        }
    }

    public int getJourneyCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        try{
            long count = DatabaseUtils.queryNumEntries(db, Global.JOURNEY_TBL_NAME);
            db.close();
            return (int) count;
        }
        catch (Exception e){
            return -1;
        }
    }

    public List<Journey> getAllJourneys(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Journey> journeys = null;

        try{
            Cursor cursor = db.rawQuery("select * from "+Global.JOURNEY_TBL_NAME+" order by "+COL_NAME,null);

            if (cursor .moveToFirst()) {
                journeys = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    String journeyStr = cursor.getString(cursor
                            .getColumnIndex(COL_JOURNEY_OBJ_STR));

                    Journey j = UtilityClass.getJsonConvertor().fromJson(journeyStr, Journey.class);
                    journeys.add(j);

                    cursor.moveToNext();
                }
            }

            cursor.close();
        }
        catch (Exception e){
            UtilityClass.log(JourneyDBHelper.class.getSimpleName(), e.getMessage());
        }
        return journeys;
    }

    public List<Journey> getJourneysBy(String whereClause, String value){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Journey> journeys = null;

        try{
            Cursor cursor = db.rawQuery("select * from "+Global.JOURNEY_TBL_NAME
                    +" where "+whereClause+" = "+value
                    +" order by "+COL_NAME,null);

            if (cursor .moveToFirst()) {
                journeys = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    String journeyStr = cursor.getString(cursor
                            .getColumnIndex(COL_JOURNEY_OBJ_STR));

                    Journey j = UtilityClass.getJsonConvertor().fromJson(journeyStr, Journey.class);
                    journeys.add(j);

                    cursor.moveToNext();
                }
            }

            cursor.close();
        }
        catch (Exception e){
            UtilityClass.log(JourneyDBHelper.class.getSimpleName(), e.getMessage());
        }
        return journeys;
    }

}
