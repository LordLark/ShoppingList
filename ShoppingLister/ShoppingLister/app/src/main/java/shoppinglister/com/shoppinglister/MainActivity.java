package shoppinglister.com.shoppinglister;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class MainActivity extends Activity {

    EditText input;
    ArrayList <String> listOfItems;
    ListView itemListView;
    ArrayAdapter<String> arrayAdapter;
    public String item;
    Context ctx = this;
    ImageButton btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.addButton);
        input = findViewById(R.id.itemEditText);
        listOfItems = new ArrayList<>();
        itemListView = findViewById(R.id.itemListView);


        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                listOfItems
        )
        { @Override public View getView(int position, View convertView, ViewGroup parent){
            Random rand = new Random();
            final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7',
                    '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            char[] s = new char[7];

            do {

                int n = rand.nextInt(0x1000000);

                s[0] = '#';
                for (int i = 1; i < 7; i++) {
                    s[i] = hex[n & 0xf];
                    n >>= 4;

                }
            }while (s[1]== 'e' || s[1] == 'd'|| s[1] == '8'|| s[1] == 'c'|| s[1] == 'f'|| s[1] == '7'|| s[1] == '6'|| s[1] == '9'|| s[1] == 'a'|| s[1] == 'b'|| s[3] == 'e');

            TextView item = (TextView) super.getView(position,convertView,parent);
            item.setTextColor(Color.parseColor(String.copyValueOf(s)));
            item.setTypeface(item.getTypeface(), Typeface.BOLD + Typeface.ITALIC);  item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);

            String buy = item.getText().toString();
            buy = "Purchase: " + buy + String.copyValueOf(s);
            item.setText(buy);
            return item;
        }

        }  ;
        itemListView.setAdapter(arrayAdapter);


        if (true) {
            DataBaseHandler dbx = new DataBaseHandler(ctx);
            Cursor holds = dbx.getList(dbx);
            holds.moveToFirst();
            try {
                if (holds.getString(0) != null) {
                    int i = 0;
                    do {
                        String load = holds.getString(i);
                        listOfItems.add(load);
                        arrayAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "DB loaded", Toast.LENGTH_LONG).show();


                    } while (holds.moveToNext());
                } else {
                    //do nothing
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "No database info", Toast.LENGTH_LONG).show();
            }
        }


        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    String s = itemListView.getItemAtPosition(position).toString();



                                                    DataBaseHandler dele = new DataBaseHandler(ctx);
                                                    dele.removeItem(dele, s);

                                                    listOfItems.remove(listOfItems.indexOf(s));
                                                    arrayAdapter.notifyDataSetChanged();
                                                    Toast.makeText(getBaseContext(), "Removing " + s + " from Shopping List", Toast.LENGTH_SHORT).show();
                                                }
                                            }


        );

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(v);
            }
        });



    }


    public void addItem(View view){
        item = input.getText().toString();
        DataBaseHandler addLDB = new DataBaseHandler(ctx);
        addLDB.addItemDB(item);

        listOfItems.add(item);
        arrayAdapter.notifyDataSetChanged();
        input.setText("");

    }
    public class DataBaseHandler extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DB_Name = "DB_NAME";
        protected static final String Shopping_Table = "Shopping_Table";


        public DataBaseHandler(Context ctx) {
            super(ctx, DB_Name, null, DATABASE_VERSION);
            Log.d("Database", "made");
            Toast.makeText(getBaseContext(), "DB", Toast.LENGTH_LONG).show();
        }
        //"create table if not exists "
        public static final String CREATE_Table = "Create Table "
                + Shopping_Table
                + "("+" item " + " TEXT " + ");";



        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_Table);
            Log.d("table", "added");
            //db.close();
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }


        private static final String item = " item ";

        public void addItemDB( String item) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("item", item);
            db.insert(Shopping_Table, null, values);
            Log.d("Item has been added to database", item.toLowerCase());
            Toast.makeText(getBaseContext(), "Item " + item + " saved to Database ", Toast.LENGTH_LONG).show();
            db.close();

        }

        public Cursor getList(DataBaseHandler x){


            SQLiteDatabase db = x.getReadableDatabase();
            String[] column = {"item"};
            Cursor cursor = db.query(Shopping_Table, column, null, null, null, null, null);
            return cursor;

        }

        public void removeItem(DataBaseHandler x ,String s ){
            SQLiteDatabase db = x.getReadableDatabase();
            db.delete(Shopping_Table, "item = ?", new String[] {s});

        }


    }



}
