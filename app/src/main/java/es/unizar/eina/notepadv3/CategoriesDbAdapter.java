package es.unizar.eina.notepadv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CategoriesDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "CategoriesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table categories (_id integer primary key autoincrement, "
                    + "title text not null unique);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "categories";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("myTag", "Comprobando si se crea las malditas categorias");
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS categories");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public CategoriesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the categories database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public CategoriesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new category using the title and body provided. If the category is
     * successfully created return the new rowId for that category, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the category
     * @return rowId or -1 if failed
     */
    public long createCategory(String title, Long id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_ROWID, id);

        return mDb.insertOrThrow(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the category with the given rowId
     *
     * @param rowId id of category to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteCategory(Long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID+ "=" +rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all categories in the database
     *
     * @return Cursor over all categories
     */
    public Cursor fetchAllCategories() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE},
                null, null, null, null, KEY_TITLE);
    }

    /**
     * Return a Cursor positioned at the category that matches the given rowId
     *
     * @param rowId id of category to retrieve
     * @return Cursor positioned to matching category, if found
     * @throws SQLException if category could not be found/retrieved
     */
    public Cursor fetchCategory(Long rowId) throws SQLException {

        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_TITLE}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Return a String positioned at the category that matches the given title
     *
     * @param title title of category to retrieve
     * @return Cursor positioned to matching title, if found
     * @throws SQLException if category could not be found/retrieved
     */
    public String fetchCategoryByTitle(String title) throws SQLException {

        Cursor mCursor = fetchAllCategories();
        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()) {
            if(title.equals(mCursor.getString(mCursor.getColumnIndexOrThrow(CategoriesDbAdapter.KEY_TITLE)))){
                return mCursor.getString(mCursor.getColumnIndexOrThrow(CategoriesDbAdapter.KEY_ROWID));
            };
            mCursor.moveToNext();
        }

        return "0"; //.getString(mCursor.getColumnIndexOrThrow(CategoriesDbAdapter.KEY_ROWID));

    }

    /**
     * Update the category using the details provided. The category to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @param title value to set category title to
     * @return true if the category was successfully updated, false otherwise
     */
    public boolean updateCategory(Long rowId, String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_ROWID, rowId);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchCategory(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_TITLE}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public void resetCategories(){
        mDb.execSQL("DROP TABLE IF EXISTS categories");
        mDb.execSQL(DATABASE_CREATE);
    }
}
