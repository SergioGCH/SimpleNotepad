package es.unizar.eina.notepadv3;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class CategoryEdit extends AppCompatActivity {

    private EditText mTitleText;
    private Long mRowId;
    private CategoriesDbAdapter mDbHelper;
    private EditText mRowIdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new CategoriesDbAdapter(this);
        mDbHelper.open();
        setContentView(R.layout.category_edit);
        setTitle(R.string.edit_category);

        mRowIdText = (EditText) findViewById((R.id.ID2));
        mTitleText = (EditText) findViewById(R.id.title2);
        Button confirmButton = (Button) findViewById(R.id.confirm2);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(CategoriesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(CategoriesDbAdapter.KEY_ROWID)
                    : null ;
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    private void populateFields () {
        if ( mRowId != null ) {
            Cursor category = mDbHelper.fetchCategory( mRowId ) ;
            startManagingCursor (category) ;
            mTitleText.setText(category.getString(
                    category.getColumnIndexOrThrow(CategoriesDbAdapter.KEY_TITLE)));

            mRowIdText.setText(category.getString(category.getColumnIndexOrThrow(CategoriesDbAdapter.KEY_ROWID)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(CategoriesDbAdapter.KEY_ROWID, mRowId);
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
        Log.d("myTag", "Titulando"+title+"trump");
        if (mRowId == null) {
            long id = mDbHelper.createCategory(title, mRowId);

            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateCategory(mRowId, title);
        }

    }
}
