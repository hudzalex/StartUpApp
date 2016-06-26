package com.biosens.biosens;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Sasha on 25.06.2016.
 */
public class ResListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private SQLiteDatabase db;
    private Cursor cursor,cursor1;
    String placeName,Date,ListText,plase_id;
    int placePhoto,ImageId;
    int haveToxin;
    // Alert Dialog Manager
    public ResListAdapter(Context context, String[] values) {
        super(context, R.layout.list_layout, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_layout, parent, false);
        TextView fieldName = (TextView) rowView.findViewById(R.id.field_name);
        TextView DateRez = (TextView) rowView.findViewById(R.id.date_rez);
        TextView TextRez = (TextView) rowView.findViewById(R.id.list_rez);

        ImageView photo = (ImageView) rowView.findViewById(R.id.list_image);
        ImageView prev_image = (ImageView) rowView.findViewById(R.id.prev_image);





    SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
    db = biosensDatabaseHelper.getReadableDatabase();
    String query = "SELECT research._id,research.place_id, research.culture_id,research.end_time,research.have_toxin, place.name,place.photo FROM research INNER JOIN place ON research.place_id = place._id where research._id= ?";


    cursor = db.rawQuery(query, new String[]{values[position]});
    cursor.moveToFirst();
    if (!cursor.isAfterLast()) {
        haveToxin = cursor.getInt(4);
        if (haveToxin == 1) {
            ImageId = R.drawable.fail;
            ListText = "Toxins detected";


        } else {
            ImageId = R.drawable.success;
            ListText = "Toxins not detected";

        }



        fieldName.setText(cursor.getString(5));
        DateRez.setText(cursor.getString(3));
        TextRez.setText(ListText);
        prev_image.setImageResource(cursor.getInt(6));
        prev_image.setContentDescription("Prev");
        photo.setImageResource(ImageId);
        photo.setContentDescription("Rez Photo");

    }
        return rowView;
    }
}
