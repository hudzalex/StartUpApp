package com.biosens.biosens;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;
import java.util.UUID;


/**
 * Created by Sasha on 20.04.2016.
 */
public class BioSensDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="biosens";
    private static final int DB_VERSION=1;

    BioSensDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public  void onCreate(SQLiteDatabase db){
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public  void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion){
        updateMyDatabase(db,oldVersion,newVersion);
    }
    private void updateMyDatabase(SQLiteDatabase db,int oldVersion,int newVersion){
        if(oldVersion<1){
            db.execSQL("CREATE TABLE user_account ("
                    + "_id TEXT PRIMARY KEY NOT NULL,"
                    + "name TEXT NOT NULL,"
                    + "password_hash TEXT NOT NULL)");

            db.execSQL("CREATE TABLE Test ("
                    + "_id TEXT PRIMARY KEY,"
                    + "Result INTEGER,"
                    + "Field TEXT,"
                    + "Date TEXT,"
                    + "Culture TEXT,"
                    + "Affection TEXT,"
                    + "ListText TEXT,"
                    + "ImageId INTEGER,"
					+ "PrevImageId INTEGER,"
                    + "Longitude NUMERIC,"
                    + "Latitude NUMERIC,"
                    + "Sync BOOLEAN,"
                    + "USER_UUID TEXT)"
                    );
            db.execSQL("CREATE TABLE research ("
                    + "_id TEXT PRIMARY KEY NOT NULL,"
                    + "place_id TEXT NOT NULL,"
                    + "start_time TEXT NOT NULL,"
                    + "end_time TEXT NOT NULL,"
                    + "culture_id TEXT NOT NULL,"
                    + "user_id TEXT NOT NULL,"
                    + "have_toxin BOOLEAN NOT NULL,"
                    + "description TEXT NOT NULL,"
                    + "sync BOOLEAN NOT NULL)"

            );
            db.execSQL("CREATE TABLE measurement ("
                    + "_id TEXT PRIMARY KEY NOT NULL,"
                    + "research_id TEXT NOT NULL,"
                    + "start_time TEXT NOT NULL,"
                    + "end_time TEXT NOT NULL,"
                    + "unit TEXT NOT NULL,"
                    + "value NUMERIC NOT NULL,"
                    + "boundary_value NUMERIC NULL,"
                    + "longitude NUMERIC NOT NULL,"
                    + "latitude NUMERIC NOT NULL,"
                    + "user_id TEXT NOT NULL,"
                    + "description TEXT NOT NULL,"
                    + "sync BOOLEAN NOT NULL)"
            );
            db.execSQL("CREATE TABLE place ("
                    + "_id TEXT PRIMARY KEY NOT NULL,"
                    + "name TEXT NOT NULL,"
                    + "longitude NUMERIC NOT NULL,"
                    + "latitude NUMERIC NOT NULL,"
                    + "user_id TEXT NOT NULL,"
                    + "photo INTEGER NULL,"
                    + "sync BOOLEAN NOT NULL)"
            );
            db.execSQL("CREATE TABLE culture ("
                    + "_id TEXT PRIMARY KEY NOT NULL,"
                    + "name TEXT NOT NULL,"
                    + "user_id TEXT NOT NULL,"
                    + "photo INTEGER NULL,"
                    + "sync BOOLEAN NOT NULL)"
            );

           UUID userId = insertUser(db, "admin", "1111");

            insertCult(db,"Пшеница",R.drawable.field_1);
          //  insertPlace(db,"Field 1", 50, 30, R.drawable.field_1, userId.toString());

          /*    insertTest(db, 0, "поле", "12.12.2014", "So", "T2", 0, 0, 1);
            insertTest(db, 1, "поле1", "12.12.2015", "So", "T2", 1, 0, 1);
              insertDrink(db,"Cappuccino","Espresso, hot milk, and a steamed milk foam",R.drawable.cappuccino);
            insertDrink(db, "Filter", "Highest quality beans roasted and brewed fresh", R.drawable.filter);

            CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    amount DOUBLE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);


            */
        }
        if(oldVersion<2){

        }
    }
    public static UUID insertUser(SQLiteDatabase db, String name, String password){
        ContentValues userValues=new ContentValues();
        UUID user_uuid = UUID.randomUUID();
        userValues.put("_id", user_uuid.toString());
        userValues.put("name",name);
        userValues.put("password_hash",password);
        db.insert("user_account",null,userValues);

        return user_uuid;
    }
    public static void insertPlace(SQLiteDatabase db, String name,double Longitude, double Latitude, int photoID, String user_id){
        ContentValues placeValues=new ContentValues();
        UUID place_uuid = UUID.randomUUID();

        boolean sync=false;
        placeValues.put("_id", place_uuid.toString());
        placeValues.put("name",name);
        placeValues.put("longitude",Longitude);
        placeValues.put("latitude",Latitude);
        placeValues.put("user_id",user_id);
        placeValues.put("photo",photoID);
        placeValues.put("sync",sync);
        long rowid = db.insert("place",null,placeValues);
    }
    public static void insertCult(SQLiteDatabase db, String name , int photoID){
        ContentValues cultValues=new ContentValues();
        UUID cult_uuid = UUID.randomUUID();
        UUID user_id = UUID.randomUUID();
        boolean sync=false;
        cultValues.put("_id", cult_uuid.toString());
        cultValues.put("name",name);
        cultValues.put("user_id",user_id.toString());
        cultValues.put("photo",photoID);
        cultValues.put("sync",sync);
        long rowid = db.insert("culture",null,cultValues);
    }
    public static UUID insertResearch(SQLiteDatabase db, String place_id, String startTime, String endTime,String culture_id,String user_id, boolean haveToxin,String description){
        ContentValues researchValues=new ContentValues();
        UUID research_uuid = UUID.randomUUID();
        boolean sync=false;
        researchValues.put("_id", research_uuid.toString());
        researchValues.put("place_id",place_id);
        researchValues.put("start_time",startTime);
        researchValues.put("end_time", endTime);
        researchValues.put("culture_id",culture_id);
        researchValues.put("user_id",user_id);
        researchValues.put("have_toxin",haveToxin);
        researchValues.put("description",description);
        researchValues.put("sync",sync);
        db.insert("research",null,researchValues);

        return research_uuid;
    }

    public static void updateResearch(SQLiteDatabase db, String Id, boolean haveToxin){
        ContentValues researchValues=new ContentValues();

        researchValues.put("have_toxin",haveToxin);
        researchValues.put("sync",false);


        String where = " _id=?";
        String[] whereArgs = new String[] { Id};


        db.update("research", researchValues, where, whereArgs);



    }
    public static void updateSync(SQLiteDatabase db, String TtableName, String Id, boolean sync){
        ContentValues updateSync=new ContentValues();
        updateSync.put("sync",sync);
        String where = "_id=?";
        String[] whereArgs = new String[] {Id};
        db.update(TtableName, updateSync, where, whereArgs);
    }

    public static void updatePlace(SQLiteDatabase db,  String Id, String name){
        ContentValues updatePlace=new ContentValues();
        updatePlace.put("name",name);
        updatePlace.put("sync",false);
        String where = "_id=?";
        String[] whereArgs = new String[] {Id};
        db.update("place", updatePlace, where, whereArgs);
    }



    public static void insertMeasurement(SQLiteDatabase db, String research_id, String startTime, String endTime,String unit,double value,double boundary_value,double Longitude, double Latitude, String user_id, String description){
        ContentValues measurementValues=new ContentValues();
        UUID test_uuid = UUID.randomUUID();
        boolean sync=false;
        measurementValues.put("_id", test_uuid.toString());
        measurementValues.put("research_id",research_id);
        measurementValues.put("start_time",startTime);
        measurementValues.put("end_time", endTime);
        measurementValues.put("unit",unit);
        measurementValues.put("value",value);
        measurementValues.put("boundary_value",boundary_value);
        measurementValues.put("longitude",Longitude);
        measurementValues.put("latitude",Latitude);
        measurementValues.put("user_id",user_id);
        measurementValues.put("description",description);
        measurementValues.put("sync",sync);
        db.insert("measurement",null,measurementValues);
    }
    public static void insertTest(SQLiteDatabase db, int result, String fileld, String date,String Culture,String Affection, String ListText,int imageID,int previmageID,double Longitude, double Latitude, String user_id){
        ContentValues testValues=new ContentValues();
        UUID test_uuid = UUID.randomUUID();
        boolean sync=false;
        testValues.put("_id", test_uuid.toString());
        testValues.put("Result",result);
        testValues.put("Field",fileld);
        testValues.put("Date", date);
        testValues.put("Culture",Culture);
        testValues.put("Affection",Affection);
        testValues.put("ListText",ListText);
        testValues.put("ImageId",imageID);
        testValues.put("PrevImageId",previmageID);
        testValues.put("Longitude",Longitude);
        testValues.put("Latitude",Latitude);
        testValues.put("USER_UUID",user_id);
        testValues.put("Sync",sync);
        db.insert("Test",null,testValues);
    }


}
