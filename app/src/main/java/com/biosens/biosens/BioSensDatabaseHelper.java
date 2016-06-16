package com.biosens.biosens;

import android.content.ContentValues;
import android.content.Context;
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
                    + "_id UUID PRIMARY KEY NOT NULL,"
                    + "name TEXT NOT NULL,"
                    + "password_hash TEXT NOT NULL)");
            db.execSQL("CREATE TABLE Test ("
                    + "_id UUID PRIMARY KEY,"
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
                    + "USER_UUID UUID)"
                    );
            db.execSQL("CREATE TABLE place ("
                    + "_id UUID PRIMARY KEY NOT NULL,"
                    + "name TEXT NOT NULL,"
                    + "longitude FLOAT(53) NOT NULL,"
                    + "latitude FLOAT(53) NOT NULL,"
                    + "user_id UUID NOT NULL,"
                    + "photo INTEGER NULL,"
                    + "sync BOOLEAN NOT NULL)"
            );
            db.execSQL("CREATE TABLE culture ("
                    + "_id UUID PRIMARY KEY NOT NULL,"
                    + "name TEXT NOT NULL,"
                    + "user_id UUID NOT NULL,"
                    + "photo INTEGER NULL,"
                    + "sync BOOLEAN NOT NULL)"
            );

          insertUser(db, "admin", "1111");
            insertUser(db, "admin2", "1111");
            insertUser(db, "admin3", "1111");
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
    public static void insertUser(SQLiteDatabase db, String name, String password){
        ContentValues userValues=new ContentValues();
        UUID user_uuid = UUID.randomUUID();
        userValues.put("_id", user_uuid.toString());
        userValues.put("name",name);
        userValues.put("password_hash",password);
        db.insert("user_account",null,userValues);
    }
    public static void insertPlace(SQLiteDatabase db, String name,double Longitude, double Latitude,String user_id, int photoID){
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
        db.insert("place",null,placeValues);
    }
    public static void insertCult(SQLiteDatabase db, String name ,String user_id, int photoID){
        ContentValues cultValues=new ContentValues();
        UUID cult_uuid = UUID.randomUUID();
        boolean sync=false;
        cultValues.put("_id", cult_uuid.toString());
        cultValues.put("name",name);
        cultValues.put("user_id",user_id);
        cultValues.put("photo",photoID);
        cultValues.put("sync",sync);
        db.insert("culture",null,cultValues);
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
