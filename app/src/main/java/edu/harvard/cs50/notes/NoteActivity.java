package edu.harvard.cs50.notes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class NoteActivity extends AppCompatActivity {
    private EditText editText;
    private int id;
    private String contents;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        String contents = getIntent().getStringExtra("contents");
        id = getIntent().getIntExtra("id",-1);

        // Set editText view to the contents of the notes
        editText = findViewById(R.id.edit_text);
        editText.setText(contents);

        // Create Shared Preferences file to temporarily save data
        sp = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("contents", contents);
        editor.putInt("id", id);
        editor.commit();
    }

    @Override
    protected void onPause() {
        // Save note only if text is changed.
        String edits = editText.getText().toString();
        Date date = new Date();
        contents = sp.getString("contents","");
        id = sp.getInt("id", -1);

        // if the contents remain the same do nothing, just exit.
        if((contents.isEmpty() && !edits.isEmpty()) || !contents.equals(edits)){
            // if the note didn't exist (i.e. the id is 0 or null, create a new note)
            if (id == 0){
                MainActivity.database.noteDao().create(edits, date, date);
            } else if (id != -1){
                MainActivity.database.noteDao().save(edits, date, id);
            }
        }
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}