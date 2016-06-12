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
            db.execSQL("CREATE TABLE User ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "USER_UUID UUID,"
                    + "NAME TEXT,"
                    + "DESCRIPTION TEXT,"
                    + "PASSWORD TEXT,"
                    + "LOGIN TEXT,"
                    + "EMAIl TEXT)");
            db.execSQL("CREATE TABLE Test ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
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
                    + "USER_UUID UUID)"
                    );

          insertUser(db, "Sasha", "Admin Acces", "1111", "admin", "biosens@gmail.com");
            insertUser(db, "Sasha1", "Admin Acces1", "1111", "admin1", "biosens1@gmail.com");
            insertUser(db, "Sasha2", "Admin Acces2", "1111", "admin2", "biosens2@gmail.com");
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
    public static void insertUser(SQLiteDatabase db, String name, String description, String password,String login,String email){
        ContentValues userValues=new ContentValues();
        UUID user_uuid = UUID.randomUUID();
        userValues.put("USER_UUID", user_uuid.toString());
        userValues.put("NAME",name);
        userValues.put("DESCRIPTION",description);
        userValues.put("PASSWORD",password);
        userValues.put("LOGIN",login);
        userValues.put("EMAIL",email);
        db.insert("User",null,userValues);
    }
    public static void insertTest(SQLiteDatabase db, int result, String fileld, String date,String Culture,String Affection, String ListText,int imageID,int previmageID,double Longitude, double Latitude, String user_id){
        ContentValues testValues=new ContentValues();
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
        db.insert("Test",null,testValues);
    }
}
