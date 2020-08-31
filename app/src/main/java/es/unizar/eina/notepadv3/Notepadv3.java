package es.unizar.eina.notepadv3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import es.unizar.eina.send.SendAbstractionImpl;


public class Notepadv3 extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int ACTIVITY_SEND=2;

    private static final int INSERT_ID = 1;
    private static final int INSERT_CAT = 2;
    private static final int DELETE_ID = 3;
    private static final int EDIT_ID = 4;
    private static final int DELETE_CAT = 5;
    private static final int EDIT_CAT = 6;
    private static final int SHOW_CAT = 7;
    private static final int SHOW_NOT = 8;
    private static final int SHOW_NOT_BY_CAT = 11;
    private static final int SEND_ID = 9;
    private static final int SYSTEM_TEST = 10;
    private static final int SORT_NOTES = 12;
    private static final int OVERLOAD_TEST = 13;
    private static final int VOLUME_TEST = 14;
    private Long mRowId;
    private static int id_mostrar = 0;
    private Menu menu;

    private NotesDbAdapter mDbHelper;
    private CategoriesDbAdapter mDbHelperCat;
    private ListView mList;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepadv3);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mDbHelperCat = new CategoriesDbAdapter(this);
        mDbHelperCat.open();
        //mDbHelperCat.resetCategories(); // Para refrescar la base de datos al iniciar la app
        //mDbHelper.resetNotes();  // en caso de ser necesario.
        mList = (ListView)findViewById(R.id.list);
        fillData();
        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        registerForContextMenu(mList);
        //mDbHelper.resetNotes(); // Para resetear la base de datos después de las pruebas de
        // sobrecarga y volumen.

    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor cursor;
        if(id_mostrar == 0){
             cursor = mDbHelper.fetchAllNotes();
            startManagingCursor(cursor);
            // Create an array to specify the fields we want to display in the list (only TITLE)
            String[] from = new String[] { NotesDbAdapter.KEY_TITLE };

            // and an array of the fields we want to bind those fields to (in this case just text1)
            int[] to = new int[] { R.id.text1 };

            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter notes =
                    new SimpleCursorAdapter(this, R.layout.notes_row, cursor, from, to);
            mList.setAdapter(notes);

        }else{
            cursor = mDbHelperCat.fetchAllCategories();
            startManagingCursor(cursor);
            // Create an array to specify the fields we want to display in the list (only TITLE)
            String[] from = new String[] { CategoriesDbAdapter.KEY_TITLE };

            // and an array of the fields we want to bind those fields to (in this case just text1)
            int[] to = new int[] { R.id.text2 };

            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter categories =
                    new SimpleCursorAdapter(this, R.layout.categories_row, cursor, from, to);
            mList.setAdapter(categories);
        }

    }
    private void fillDataByCategories() {
        // Get all of the notes from the database and create the item list
        Cursor cursor;
        cursor = mDbHelper.fetchAllNotesByCat();
        startManagingCursor(cursor);
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] { NotesDbAdapter.KEY_TITLE };

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.text1 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, cursor, from, to);
        mList.setAdapter(notes);

    }

    private void fillDataOfACategory(String idCategory) {
        // Get all of the notes from the database and create the item list
        Cursor cursor;
        cursor = mDbHelper.fetchNotesOfACategory(idCategory);
        startManagingCursor(cursor);
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] { NotesDbAdapter.KEY_TITLE };

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.text1 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, cursor, from, to);
        mList.setAdapter(notes);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        this.menu = menu;
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_insert);
        menu.add(Menu.NONE, INSERT_CAT, Menu.NONE, R.string.menu_insert_cat);
        menu.add(Menu.NONE, SHOW_CAT, Menu.NONE, R.string.menu_show_cat);
        menu.add(Menu.NONE, SHOW_NOT, Menu.NONE, R.string.menu_show_not);
        menu.add(Menu.NONE, SHOW_NOT_BY_CAT, Menu.NONE, R.string.menu_show_not_by_cat);
        menu.add(Menu.NONE, SYSTEM_TEST, Menu.NONE, R.string.test);
        menu.add(Menu.NONE, OVERLOAD_TEST, Menu.NONE, R.string.overload_test);
        menu.add(Menu.NONE, VOLUME_TEST, Menu.NONE, R.string.volume_test);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
            case INSERT_CAT:
                createCategory();
                return true;
            case SHOW_CAT:
                id_mostrar = 1;
                fillData();
                return true;
            case SHOW_NOT:
                id_mostrar = 0;
                fillData();
                return true;
            case SHOW_NOT_BY_CAT:
                id_mostrar = 0;
                fillDataByCategories();
                return true;
            case SYSTEM_TEST:
                test();
                return true;
            case OVERLOAD_TEST:
                try {
                    testSobrecarga();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            case VOLUME_TEST:
                try {
                    testVolumen();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(id_mostrar == 0){
            menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete);
            menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.menu_edit);
            menu.add(Menu.NONE, SEND_ID, Menu.NONE, R.string.menu_send);
        }else{
            menu.add(Menu.NONE, DELETE_CAT, Menu.NONE, R.string.menu_delete_cat);
            menu.add(Menu.NONE, EDIT_CAT, Menu.NONE, R.string.menu_edit_cat);
            menu.add(Menu.NONE, SORT_NOTES, Menu.NONE, R.string.sort_notes);
        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        if(id_mostrar == 0){
            switch(item.getItemId()) {
                case DELETE_ID:
                    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    mDbHelper.deleteNote(info.id);
                    fillData();
                    return true;
                case EDIT_ID:
                    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    editNote(info.position, info.id);
                    return true;
                case SEND_ID:
                    SendAbstractionImpl sai = new SendAbstractionImpl(this, "");
                    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    Cursor c = mDbHelper.fetchNote(info.id);
                    startManagingCursor (c) ;
                    String subject = c.getString(
                            c.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
                    String body = c.getString(
                            c.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
                    sai.send(subject, body);
                    return true;

            }
        }else{
            switch(item.getItemId()) {
                case DELETE_CAT:
                    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    Cursor c = mDbHelperCat.fetchCategory(info.id);
                    String categoria =
                            c.getString(c.getColumnIndexOrThrow(CategoriesDbAdapter.KEY_TITLE));
                    try{
                        Cursor cursor = mDbHelper.fetchAllNotes();
                        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            // The Cursor is now set to the right position
                            String categoriaNota = cursor.getString(
                                    cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_CATEGORY));
                            Long idNota = cursor.getLong(
                                    cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID));
                            String bodyNota = cursor.getString(
                                    cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
                            String tituloNota = cursor.getString(
                                    cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
                            if(categoria.equals(categoriaNota)){
                                mDbHelper.updateNote(idNota, tituloNota, bodyNota, "none");
                            }

                        }
                    }catch(Exception e){

                    }

                    mDbHelperCat.deleteCategory(info.id);
                    fillData();
                    return true;

                case EDIT_CAT:
                    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    editCategory(info.position, info.id);
                    return true;

                case SORT_NOTES:
                    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    fillDataOfACategory(Long.toString(info.id));
                    return true;


            }
        }

        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void createCategory() {
        Intent i = new Intent(this, CategoryEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void test() {
        Test test = new Test(mDbHelper, mDbHelperCat);
        test.testSistema();
    }

    private void testSobrecarga() throws InterruptedException {
        Test test = new Test(mDbHelper, mDbHelperCat);
        test.testSobrecarga();
    }

    private void testVolumen() throws InterruptedException {
        Test test = new Test(mDbHelper, mDbHelperCat);
        test.testVolumen();
    }


    protected void editCategory(int position, long id) {
        Intent i = new Intent(this, CategoryEdit.class);
        i.putExtra(CategoriesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    protected void editNote(int position, long id) {
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}
