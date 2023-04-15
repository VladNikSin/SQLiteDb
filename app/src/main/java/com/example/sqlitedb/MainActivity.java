package com.example.sqlitedb;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ListView userList;
    TextView header;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        header = findViewById(R.id.header);
        userList = findViewById(R.id.list);

        //--------------------отлавливаем нажатия на список
        userList = findViewById(R.id.list);

        userList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long recordId) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch(id){
                            case R.id.menu_edit:
                                onShowEditDialog(recordId);//id элемента
                                return true;
                            case R.id.menu_delete:
                                db.delete(DatabaseHelper.TABLE_USERS, "_id = ?", new String[]{String.valueOf(recordId)});
                                onResume();
                                Toast.makeText(MainActivity.this, "Удалено!", Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        //return MainActivity.super.onOptionsItemSelected(menuItem);
                        return false;
                    }
                });
                // получение координат нажатия
                int[] location = new int[2];
                view.getLocationOnScreen(location);

                // вызов popupMenu в месте нажатия
                popupMenu.show();
                return true;
            }
        });
        //------------------------------------------

        databaseHelper = new DatabaseHelper(getApplicationContext());

    }

    public void onResume() {
        super.onResume();
        // открываем подключение
        db = databaseHelper.getReadableDatabase();

        //получаем данные из бд в виде курсора
        userCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE_USERS, null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_AGE};
        // создаем адаптер, передаем в него курсор
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        header.setText("Найдено элементов: " +  userCursor.getCount());
        userList.setAdapter(userAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }

    public void addRecord(View view) {
        Dialog addDialog = new Dialog(this, R.style.Theme_SQLiteDb);
        addDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100,0,0,0)));
        addDialog.setContentView(R.layout.add_data_dialog);
        //addDialog.cancel();

                EditText dialogName = addDialog.findViewById(R.id.personName);
                EditText dialogAge = addDialog.findViewById(R.id.personNumber);
                addDialog.findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "Данные добавлены!", Toast.LENGTH_SHORT).show();
                        databaseHelper.addData(databaseHelper.getReadableDatabase(), dialogName.getText().toString(), dialogAge.getText().toString());
                        onResume();
                        addDialog.hide();
                    }
                });
                addDialog.show();
    }

    private void onShowEditDialog(long id){
        Dialog editDialog = new Dialog(MainActivity.this, R.style.Theme_SQLiteDb);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100,0,0,0)));
        editDialog.setContentView(R.layout.edit_data_dialog);
        editDialog.cancel();

        EditText dialogName = editDialog.findViewById(R.id.editPersonName);
        EditText dialogAge = editDialog.findViewById(R.id.editPersonNumber);

        userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_USERS + " where " + DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        userCursor.moveToFirst();
        dialogName.setText(userCursor.getString(1));
        dialogAge.setText(String.valueOf(userCursor.getInt(2)));


        editDialog.findViewById(R.id.buttonEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_NAME, dialogName.getText().toString());
                cv.put(DatabaseHelper.COLUMN_AGE, Integer.parseInt(dialogAge.getText().toString()));

                db.update(DatabaseHelper.TABLE_USERS, cv, DatabaseHelper.COLUMN_ID + "=" + id, null);

                Toast.makeText(MainActivity.this, "Данные обновлены!", Toast.LENGTH_SHORT).show();
                onResume();
                editDialog.hide();
            }
        });
        editDialog.show();
    }
}