package es.unizar.eina.notepadv3;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NoteEdit extends AppCompatActivity {

    private EditText mTitleText;
    private EditText mBodyText;
    private Spinner mCategoryText;
    private Long mRowId;
    private Long mRowIdCat;
    private EditText mRowIdText;
    private NotesDbAdapter mDbHelper;
    private CategoriesDbAdapter mDbHelperCat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mDbHelperCat = new CategoriesDbAdapter(this);
        mDbHelperCat.open();
        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        mRowIdText = (EditText) findViewById((R.id.ID));
        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mCategoryText = (Spinner) findViewById(R.id.spinner);
        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        mRowIdCat = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(CategoriesDbAdapter.KEY_TITLE);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null ;
        }

        if (mRowIdCat == null) {
            Bundle extras = getIntent().getExtras();
            mRowIdCat = (extras != null) ? extras.getLong(CategoriesDbAdapter.KEY_TITLE)
                    : null ;
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String title = mTitleText.getText().toString();
                String body = mBodyText.getText().toString();
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    private void populateFields () {
        Cursor cursor = mDbHelperCat.fetchAllCategories();
        startManagingCursor(cursor);
        ArrayList<String> categoriesList = new ArrayList<String>();
        cursor.moveToFirst();
        if(categoriesList.isEmpty()){
            categoriesList.add("None");
        }
        while (!cursor.isAfterLast()) {
            categoriesList.add(cursor.getString(cursor.getColumnIndexOrThrow(CategoriesDbAdapter.KEY_TITLE)));
            cursor.moveToNext();
        }

        System.out.println("La categoria es: " +categoriesList);
        ArrayAdapter categories = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categoriesList);
        mCategoryText.setAdapter(categories);
        mDbHelperCat.fetchAllCategories();
        if ( mRowId != null ) {
            Cursor note = mDbHelper.fetchNote( mRowId ) ;
            startManagingCursor (note) ;
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            mRowIdText.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID)));
            try{
                Cursor category = mDbHelperCat.fetchCategory(Long.parseLong(note.getString(
                        note.getColumnIndexOrThrow(NotesDbAdapter.KEY_CATEGORY))));
                String categoriaDeNota =
                        category.getString(category.getColumnIndexOrThrow(CategoriesDbAdapter.KEY_TITLE));
                if(!categoriaDeNota.equals(null)){
                    TextView texto = (TextView) findViewById(R.id.textView);
                    texto.setText("Current Category: " +categoriaDeNota);
                }
            }catch(Exception e){

            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveState();
    }
    @Override
    protected void onResume(){
        super.onResume();
        populateFields();
    }

    private void saveState(){
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        String category = mCategoryText.getSelectedItem().toString();
        String idCategory = mDbHelperCat.fetchCategoryByTitle(category);
        Log.d("myTag", title);
        Log.d("myTag", body);
        if (mRowId == null) {
            long id = mDbHelper.createNote(title, body, mRowId, idCategory);

            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body, idCategory);
        }
    }


    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
