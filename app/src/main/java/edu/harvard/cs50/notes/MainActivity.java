package edu.harvard.cs50.notes;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/***
 * MainActivity Class
 * This class opens when the app opens and allows users to:
 *      a. Scroll through notes saved in the Room database
 *      b. Create new notes using FloatingActionButton
 *      c. Delete notes with swipe action using TouchItemHelper and allow users to undo with
 *          snackbar.
 *      d. Favorite notes with swipe action using TouchItemHelper
 *      e. Filter notes from Search Bar
 */
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static NoteDatabase database;

    // Simple Callback needed to implement swipe actions delete & favorite
    // For swipes dragDirs = 0
    // allowable swipes are left and right
    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT){

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            // Not moving any items
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            Note note = (Note) viewHolder.itemView.getTag();

            // determine if the favorite or unfavorite icon should be shown based on the favorite status of the item
            int favoriteIcon;
            if (note.favorite) {
                favoriteIcon = R.drawable.ic_baseline_unfavorite_24;
            } else {
                favoriteIcon = R.drawable.ic_baseline_favorite_24;
            }

            // create recycler view swipe decorator
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(getResources().getColor(R.color.trashSwipeColor))
                    .addSwipeRightBackgroundColor(getResources().getColor(R.color.favoriteSwipeColor))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeRightActionIcon(favoriteIcon)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // get data of tag (note) and the position of the recycler view
            final int position = viewHolder.getAdapterPosition();
            final Note note = (Note) viewHolder.itemView.getTag();
            final int id = note.id;
            // Handle various swipe directions

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    // Delete Note
                    Log.d("MainActivity id", String.valueOf(id));
                    // delete note from the display
                    notesAdapter.getNotes().remove(position);
                    notesAdapter.notifyItemRemoved(position);

                    // create snackbar to allow user to undo delete action
                    String title = ((TextView) viewHolder.itemView.findViewById(R.id.note_row_title))
                            .getText().toString();
                    Snackbar.make(recyclerView, "Deleting Note: " + title, 3500 )
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Add item back to screen on undo click
                                    notesAdapter.getNotes().add(position,note);
                                    notesAdapter.notifyItemInserted(position);

                                }
                            })
                            .addCallback(new Snackbar.Callback(){
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    // Delete the object since the user didn't click the undo button
                                    if(event != DISMISS_EVENT_ACTION) {
                                        database.noteDao().delete(id);
                                    }
                                }
                            })
                            .show();
                    break;
                case ItemTouchHelper.RIGHT:
                    // Favorite item
                    note.setFavorite(!note.favorite);
                    List<Note> tempNotes = database.noteDao().getAllNotes();
                    // Get new position of the note
                    int newPosition = tempNotes.indexOf(note);
                    // Rearrange Note in notes list
                    notesAdapter.getNotes().remove(note);
                    notesAdapter.getNotes().add(newPosition,note);
                    // swipe - item return to position
                    notesAdapter.notifyItemMoved(position,newPosition);
                    notesAdapter.notifyItemChanged(newPosition);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // open or create database
        database = Room.databaseBuilder(getApplicationContext(), NoteDatabase.class, "notes2")
                .allowMainThreadQueries()
                .addMigrations(NoteDatabase.MIGRATION_1_2, NoteDatabase.MIGRATION_2_3)
                .build();

        // instantiate view objects
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        notesAdapter = new NotesAdapter();

        // create and attach itemTouchHelper which uses the callback used for swiping actions
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // set layout manager
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(notesAdapter);

        // define floating action button used to create new note.
        // The new note should take you to a new activity.
        FloatingActionButton button = findViewById(R.id.add_note_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //database.noteDao().create();
                //Note current = database.noteDao().getLastNote();
                //notesAdapter.reload();

                // Open a new activity to start drafting a new note. This doesn't commit the note to the db
                Intent intent = new Intent(view.getContext(),NoteActivity.class);
                intent.putExtra("contents","");
                intent.putExtra("id",0);

                view.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menu bar with search field
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // read notes from database
        notesAdapter.reload();
    }


    @Override
    public boolean onQueryTextSubmit(String filterText) {
        return onQueryTextChange(filterText);
    }

    @Override
    public boolean onQueryTextChange(String filterText) {
        // Filter Notes in RecyclerView
        notesAdapter.getFilter().filter(filterText);
        return false;
    }
}