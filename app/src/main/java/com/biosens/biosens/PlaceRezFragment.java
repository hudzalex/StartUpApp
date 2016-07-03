package com.biosens.biosens;

import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class PlaceRezFragment extends Fragment  implements View.OnClickListener{
    // Inflate the layout for this fragment
    SQLiteDatabase db;
    String row_id;
    Cursor cursor;
    TextView NewPlaceName;
    Button updateButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View layout=inflater.inflate(R.layout.fragment_place_rez, container, false);
       updateButton = (Button) layout.findViewById(R.id.ubutton);
        row_id = this.getArguments().getString("row_id");
        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
            db = biosensDatabaseHelper.getWritableDatabase();

            cursor = db.query("place",
                    new String[]{"name","photo"},
                    " _id= ?",
                    new String[] {row_id},
                    null, null,null);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        cursor.moveToFirst();
        String PlaceName=cursor.getString(0);
        int PlacePhoto = cursor.getInt(1);
        cursor.close();
        TextView fieldName=(TextView)layout.findViewById(R.id.field_name);
        NewPlaceName=(TextView)layout.findViewById(R.id.editTextNamePlace);

        ImageView photo = (ImageView)layout.findViewById(R.id.field_image);


        photo.setImageResource(PlacePhoto);
        photo.setContentDescription("Place Photo");
        fieldName.setText(PlaceName);
        updateButton.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View v) {

        BioSensDatabaseHelper.updatePlace(db,row_id,NewPlaceName.getText().toString());
        Bundle bundle = new Bundle();
        bundle.putString("row_id", row_id);
        PlaceRezFragment PRfragment = new PlaceRezFragment();
        PRfragment.setArguments(bundle);

        FragmentTransaction fr = getFragmentManager().beginTransaction();

        fr.replace(R.id.container, PRfragment);
        fr.commit();

    }

}
