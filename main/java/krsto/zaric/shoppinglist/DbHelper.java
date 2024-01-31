package krsto.zaric.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.UUID;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "shared_list_app.db";
    public static final int DATABASE_VERSION = 1;

    public static final String lists_table_name = "LISTS";
    public static final String lists_column_list_name = "listName";
    public static final String lists_column_list_creator = "listCreator";
    public static final String lists_column_list_shared = "listShared";

    public static final String users_table_name = "USERS";
    public static final String users_column_username = "username";
    public static final String users_column_email = "email";
    public static final String users_column_password = "password";

    public static final String items_table_name = "ITEMS";
    public static final String items_column_name = "name";
    public static final String items_column_list_name = "listName";
    public static final String items_column_checked = "checked";
    public static final String items_column_id = "ID";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + items_table_name + "(" +
                items_column_name + " TEXT, " +
                items_column_list_name + " TEXT, " +
                items_column_checked + " TEXT, " +
                items_column_id + " TEXT PRIMARY KEY);");

        sqLiteDatabase.execSQL("CREATE TABLE " + users_table_name + "(" +
                users_column_username + " TEXT PRIMARY KEY, " +
                users_column_email + " TEXT, " +
                users_column_password + " TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE " + lists_table_name + "(" +
                lists_column_list_name + " TEXT PRIMARY KEY, " +
                lists_column_list_creator + " TEXT, " +
                lists_column_list_shared + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertList (CharacterModel cm, String username) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(lists_column_list_name, cm.getText());
        values.put(lists_column_list_creator, username);
        values.put(lists_column_list_shared, Boolean.toString(cm.isBul()));

        long rowInserted = db.insert(lists_table_name, null, values);
        close();

        return rowInserted;
    }

    public long insertUser (String username, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(users_column_username, username);
        values.put(users_column_email, email);
        values.put(users_column_password, password);

        long rowInserted = db.insert(users_table_name, null, values);
        close();

        return rowInserted;
    }

    public long insertTask (CharacterModel cm, String list) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(items_table_name, null, null, null, null, null, null);
        ContentValues values = new ContentValues();
        values.put(items_column_name, cm.getText());
        values.put(items_column_list_name, list);
        values.put(items_column_checked, Boolean.toString(cm.isBul()));
        values.put(items_column_id, cm.getTaskId());

        long rowInserted = db.insert(items_table_name, null, values);
        close();

        return rowInserted;
    }

    public String readListCreator (String listName) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(lists_table_name, null, lists_column_list_name + "=?", new String[] { listName }, null, null, null);
        cursor.moveToFirst();

        close();
        return cursor.getString(cursor.getColumnIndexOrThrow(lists_column_list_creator));
    }

    public CharacterModel[] readLists (String username) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(lists_table_name, null, lists_column_list_creator + "=? OR " + lists_column_list_shared + "=?", new String[] {username, "true"}, null, null, null);
        if(cursor.getCount() <= 0) {
            return null;
        }
        CharacterModel[] lists = new CharacterModel[cursor.getCount()];
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            lists[i++] = createList(cursor);
        }

        close();
        return lists;
    }

    public CharacterModel[] readMyLists (String username) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(lists_table_name, null, lists_column_list_creator + "=?", new String[] {username}, null, null, null);
        if(cursor.getCount() <= 0) {
            return null;
        }
        CharacterModel[] lists = new CharacterModel[cursor.getCount()];
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            lists[i++] = createList(cursor);
        }

        close();
        return lists;
    }

    public boolean readList (String name, String creator) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(lists_table_name, null, lists_column_list_name + "=? AND " + lists_column_list_creator + "=?", new String[] { name, creator }, null, null, null);
        if(cursor.getCount() <= 0) {
            return false;
        }
        close();
        return true;
    }

    public boolean readUser (String username, String password) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(users_table_name, null, users_column_username + "=? AND " + users_column_password + "=?", new String[] { username, password }, null, null, null);
        if(cursor.getCount() <= 0) {
            return false;
        }
        close();
        return true;
    }

    public boolean readTask (String id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(items_table_name, null, items_column_id + "=?", new String[] { id }, null, null, null);
        if(cursor.getCount() <= 0) {
            return false;
        }
        close();
        return true;
    }

//    // ======================
//    public boolean readUserFilip () {
//        SQLiteDatabase db = getReadableDatabase();
//
//        Cursor cursor = db.query(users_table_name, null, users_column_username + "=?", new String[] { "Filip" }, null, null, null);
//        if(cursor.getCount() <= 0) {
//            Log.d("myTag", "No user found");
//            return false;
//        }else {
//            cursor.moveToFirst();
//            Log.d("myTag", cursor.getString(cursor.getColumnIndexOrThrow(users_column_username)));
//            Log.d("myTag", cursor.getString(cursor.getColumnIndexOrThrow(users_column_password)));
//            Log.d("myTag", cursor.getString(cursor.getColumnIndexOrThrow(users_column_email)));
//        }
//
//        close();
//        return true;
//    }
//    // ======================

    public CharacterModel[] readItems (String list) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(items_table_name, null, items_column_list_name + "=?", new String[] {list}, null, null, null);
        if(cursor.getCount() <= 0) {
            return null;
        }
        CharacterModel[] lists = new CharacterModel[cursor.getCount()];
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            lists[i++] = createTask(cursor);
        }

        close();
        return lists;
    }

    public void updateTask(String taskId, String checked) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(items_table_name, null, items_column_id + "=?", new String[] {taskId}, null, null, null);
        cursor.moveToFirst();
        String taskName = cursor.getString(cursor.getColumnIndexOrThrow(items_column_name));
        String taskListName =  cursor.getString(cursor.getColumnIndexOrThrow(items_column_list_name));
        String id =  cursor.getString(cursor.getColumnIndexOrThrow(items_column_id));

        ContentValues cv = new ContentValues();
        cv.put(items_column_name, taskName);
        cv.put(items_column_list_name, taskListName);
        cv.put(items_column_checked, checked);
        cv.put(items_column_id, id);

        db.update(items_table_name, cv, items_column_id + "=?", new String[] {taskId});
        close();
    }

    public void deleteList (String ln) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(lists_table_name, lists_column_list_name + "=?", new String[] { ln });
        db.delete(items_table_name, items_column_list_name + "=?", new String[] { ln });
        close();
    }

    public void deleteTask (String ln) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(items_table_name, items_column_id + " =?", new String[] { ln });
        close();
    }

    private CharacterModel createList (Cursor cursor) {
        String listName = cursor.getString(cursor.getColumnIndexOrThrow(lists_column_list_name));
        String listShared = cursor.getString(cursor.getColumnIndexOrThrow(lists_column_list_shared));
        return new CharacterModel(listName, Boolean.valueOf(listShared), null);
    }

    private CharacterModel createTask (Cursor cursor) {
        String taskName = cursor.getString(cursor.getColumnIndexOrThrow(items_column_name));
        String taskShared = cursor.getString(cursor.getColumnIndexOrThrow(items_column_checked));
        String taskId = cursor.getString(cursor.getColumnIndexOrThrow(items_column_id));
        return new CharacterModel(taskName, Boolean.valueOf(taskShared), taskId);
    }

}
