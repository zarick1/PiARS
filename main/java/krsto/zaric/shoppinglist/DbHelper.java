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

/**
 * Database helper class for managing SQLite database operations in the Shopping List application.
 * Handles creation, insertion, reading, updating, and deletion of users, lists, and items.
 *
 * @author Krsto Zaric
 */
public class DbHelper extends SQLiteOpenHelper {

    /** Name of the SQLite database file. */
    public static final String DATABASE_NAME = "shared_list_app.db";
    /** Version of the database schema. */
    public static final int DATABASE_VERSION = 1;

    /** Name of the table for storing shopping lists. */
    public static final String lists_table_name = "LISTS";
    /** Column for the list name in the LISTS table. */
    public static final String lists_column_list_name = "listName";
    /** Column for the list creator in the LISTS table. */
    public static final String lists_column_list_creator = "listCreator";
    /** Column indicating if the list is shared in the LISTS table. */
    public static final String lists_column_list_shared = "listShared";

    /** Name of the table for storing user data. */
    public static final String users_table_name = "USERS";
    /** Column for the username in the USERS table. */
    public static final String users_column_username = "username";
    /** Column for the user email in the USERS table. */
    public static final String users_column_email = "email";
    /** Column for the user password in the USERS table. */
    public static final String users_column_password = "password";

    /** Name of the table for storing list items. */
    public static final String items_table_name = "ITEMS";
    /** Column for the item name in the ITEMS table. */
    public static final String items_column_name = "name";
    /** Column for the associated list name in the ITEMS table. */
    public static final String items_column_list_name = "listName";
    /** Column indicating if the item is checked in the ITEMS table. */
    public static final String items_column_checked = "checked";
    /** Column for the unique item ID in the ITEMS table. */
    public static final String items_column_id = "ID";

    /**
     * Constructs a new DbHelper instance.
     *
     * @param context The application context, used to access the database.
     */
    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the database tables for users, lists, and items when the database is first created.
     *
     * @param sqLiteDatabase The SQLite database instance.
     */
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

    /**
     * Handles database schema upgrades. Currently empty as no upgrades are defined.
     *
     * @param sqLiteDatabase The SQLite database instance.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // No upgrade logic implemented
    }

    /**
     * Inserts a new shopping list into the LISTS table.
     *
     * @param cm The CharacterModel containing list data (name and shared status).
     * @param username The username of the list creator.
     * @return The row ID of the inserted list, or -1 if insertion fails.
     */
    public long insertList(CharacterModel cm, String username) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(lists_column_list_name, cm.getText());
        values.put(lists_column_list_creator, username);
        values.put(lists_column_list_shared, Boolean.toString(cm.isBul()));

        long rowInserted = db.insert(lists_table_name, null, values);
        close();

        return rowInserted;
    }

    /**
     * Inserts a new user into the USERS table.
     *
     * @param username The username of the user.
     * @param email The email address of the user.
     * @param password The password of the user.
     * @return The row ID of the inserted user, or -1 if insertion fails.
     */
    public long insertUser(String username, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(users_column_username, username);
        values.put(users_column_email, email);
        values.put(users_column_password, password);

        long rowInserted = db.insert(users_table_name, null, values);
        close();

        return rowInserted;
    }

    /**
     * Inserts a new task (item) into the ITEMS table.
     *
     * @param cm The CharacterModel containing task data (name, checked status, and ID).
     * @param list The name of the list to which the task belongs.
     * @return The row ID of the inserted task, or -1 if insertion fails.
     */
    public long insertTask(CharacterModel cm, String list) {
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

    /**
     * Retrieves the creator of a specific shopping list.
     *
     * @param listName The name of the list.
     * @return The username of the list creator, or throws an exception if not found.
     */
    public String readListCreator(String listName) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(lists_table_name, null, lists_column_list_name + "=?", new String[] { listName }, null, null, null);
        cursor.moveToFirst();

        close();
        return cursor.getString(cursor.getColumnIndexOrThrow(lists_column_list_creator));
    }

    /**
     * Retrieves all lists accessible to a user (created or shared).
     *
     * @param username The username of the user.
     * @return An array of CharacterModel objects representing the lists, or null if none exist.
     */
    public CharacterModel[] readLists(String username) {
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

    /**
     * Retrieves lists created by a specific user.
     *
     * @param username The username of the user.
     * @return An array of CharacterModel objects representing the user's lists, or null if none exist.
     */
    public CharacterModel[] readMyLists(String username) {
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

    /**
     * Checks if a specific list exists for a given creator.
     *
     * @param name The name of the list.
     * @param creator The username of the list creator.
     * @return True if the list exists, false otherwise.
     */
    public boolean readList(String name, String creator) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(lists_table_name, null, lists_column_list_name + "=? AND " + lists_column_list_creator + "=?", new String[] { name, creator }, null, null, null);
        if(cursor.getCount() <= 0) {
            return false;
        }
        close();
        return true;
    }

    /**
     * Validates a user's credentials.
     *
     * @param username The username to check.
     * @param password The password to check.
     * @return True if the credentials are valid, false otherwise.
     */
    public boolean readUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(users_table_name, null, users_column_username + "=? AND " + users_column_password + "=?", new String[] { username, password }, null, null, null);
        if(cursor.getCount() <= 0) {
            return false;
        }
        close();
        return true;
    }

    /**
     * Checks if a task exists by its ID.
     *
     * @param id The unique ID of the task.
     * @return True if the task exists, false otherwise.
     */
    public boolean readTask(String id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(items_table_name, null, items_column_id + "=?", new String[] { id }, null, null, null);
        if(cursor.getCount() <= 0) {
            return false;
        }
        close();
        return true;
    }

    /**
     * Retrieves all items for a specific shopping list.
     *
     * @param list The name of the list.
     * @return An array of CharacterModel objects representing the items, or null if none exist.
     */
    public CharacterModel[] readItems(String list) {
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

    /**
     * Updates the checked status of a task in the ITEMS table.
     *
     * @param taskId The unique ID of the task.
     * @param checked The new checked status ("true" or "false").
     */
    public void updateTask(String taskId, String checked) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(items_table_name, null, items_column_id + "=?", new String[] {taskId}, null, null, null);
        cursor.moveToFirst();
        String taskName = cursor.getString(cursor.getColumnIndexOrThrow(items_column_name));
        String taskListName = cursor.getString(cursor.getColumnIndexOrThrow(items_column_list_name));
        String id = cursor.getString(cursor.getColumnIndexOrThrow(items_column_id));

        ContentValues cv = new ContentValues();
        cv.put(items_column_name, taskName);
        cv.put(items_column_list_name, taskListName);
        cv.put(items_column_checked, checked);
        cv.put(items_column_id, id);

        db.update(items_table_name, cv, items_column_id + "=?", new String[] {taskId});
        close();
    }

    /**
     * Deletes a shopping list and its associated items from the database.
     *
     * @param ln The name of the list to delete.
     */
    public void deleteList(String ln) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(lists_table_name, lists_column_list_name + "=?", new String[] { ln });
        db.delete(items_table_name, items_column_list_name + "=?", new String[] { ln });
        close();
    }

    /**
     * Deletes a specific task from the ITEMS table.
     *
     * @param ln The unique ID of the task to delete.
     */
    public void deleteTask(String ln) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(items_table_name, items_column_id + "=?", new String[] { ln });
        close();
    }

    /**
     * Creates a CharacterModel for a shopping list from a database cursor.
     *
     * @param cursor The cursor containing list data.
     * @return A CharacterModel representing the list.
     */
    private CharacterModel createList(Cursor cursor) {
        String listName = cursor.getString(cursor.getColumnIndexOrThrow(lists_column_list_name));
        String listShared = cursor.getString(cursor.getColumnIndexOrThrow(lists_column_list_shared));
        return new CharacterModel(listName, Boolean.valueOf(listShared), null);
    }

    /**
     * Creates a CharacterModel for a task from a database cursor.
     *
     * @param cursor The cursor containing task data.
     * @return A CharacterModel representing the task.
     */
    private CharacterModel createTask(Cursor cursor) {
        String taskName = cursor.getString(cursor.getColumnIndexOrThrow(items_column_name));
        String taskShared = cursor.getString(cursor.getColumnIndexOrThrow(items_column_checked));
        String taskId = cursor.getString(cursor.getColumnIndexOrThrow(items_column_id));
        return new CharacterModel(taskName, Boolean.valueOf(taskShared), taskId);
    }
}