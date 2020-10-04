package edu.harvard.cs50.notes;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> implements Filterable {

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout containerView;
        private TextView noteTextView;
        private TextView noteTitleView;
        private TextView dateView;
        private ImageView imageView;


        NoteViewHolder(View v) {
            super(v);
            containerView = v.findViewById(R.id.note_row);
            noteTextView = v.findViewById(R.id.note_row_text);
            noteTitleView = v.findViewById(R.id.note_row_title);
            dateView = v.findViewById(R.id.note_row_date);
            imageView = v.findViewById(R.id.favorite_imageView);

            // set click listener, so NoteActivity is started on item click
            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // When Note Row in recycler view is opened a Note
                    // Pass data to NoteActivity Class
                    final Note current = (Note) containerView.getTag();
                    Intent intent = new Intent(view.getContext(), NoteActivity.class);
                    intent.putExtra("id",current.id);
                    intent.putExtra("contents", current.contents);

                    view.getContext().startActivity(intent);
                }
            });

        }
    }

    // class to create filter to search for Notes
    private class NoteFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            String searchText = "%" + charSequence.toString() + "%";
            List<Note> filteredNotes = MainActivity.database.noteDao().getNotes(searchText);
            results.values = filteredNotes;
            results.count = filteredNotes.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            notes = (List<Note>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    private List<Note> notes = new ArrayList<>();
    final int MAX_TITLE_LENGTH = 30;

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_row, parent, false);

        return new NoteViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note current = notes.get(position);

        // Set the date in the view
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy\nhh:mma");
        holder.dateView.setText(format.format(current.dateEdited));

        // Display or hide the favorite heart
        if(current.favorite){
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        //set the notes contents into the view as title and preview
        String title = "";
        String preview = "";
        String noteContents = current.contents;

        // Set the Text of Title and preview based on note contents
        int subEnd = (noteContents.length() < MAX_TITLE_LENGTH) ? noteContents.length() : MAX_TITLE_LENGTH;
        Log.d("Notes Adapter String", noteContents);
        //Log.d("Notes Adapter strend", String.valueOf(subEnd));

        // if there is a return within the first few characters stop there
        // if there is not use spaces
        // if nothing grab the first few lines
        holder.noteTextView.setVisibility(TextView.VISIBLE);
        if (noteContents.substring(0, subEnd).contains(Character.toString('\n'))) {
            int returnIndex = noteContents.indexOf('\n');
            Log.d("Notes Adapter set text", "Return Character exits at " + String.valueOf(returnIndex));
            title = noteContents.substring(0, returnIndex);
            preview = noteContents.substring(returnIndex);
        } else if (subEnd < MAX_TITLE_LENGTH){
            title = noteContents;
            holder.noteTextView.setVisibility(TextView.INVISIBLE);
        } else if (noteContents.substring(0,subEnd).contains(Character.toString(' '))){

            int start = 0;
            int spaceIndex;

            //Log.d("Notes Adapter space", "start: "+String.valueOf(start)+"end: "+String.valueOf(spaceIndex));
             //Log.d("Notes Adapter substr",noteContents.substring(start,spaceIndex));

            do {
                //Log.d("Note Adapter Contains Space", String.valueOf(noteContents.substring(start).contains(" ")));
                spaceIndex = noteContents.substring(start, subEnd).indexOf(' ') + start;
                if(spaceIndex <= start){ break; }
                //Log.d("Notes Adapter substr",noteContents.substring(start,spaceIndex));
                Log.d("Notes Adapter space", "start: "+String.valueOf(start)+"end: "+String.valueOf(spaceIndex) + "subend: "+String.valueOf(subEnd));
                title += noteContents.substring(start, spaceIndex+1);
                start = spaceIndex+1;
            } while ( start < subEnd && noteContents.substring(start).contains(" "));


            preview = noteContents;

        } else {
            title = noteContents.substring(0,subEnd);
            preview = noteContents;
        }

        holder.noteTitleView.setText(title);
        holder.noteTextView.setText(preview.replace('\n',' ').trim());

        // passes along current note
        holder.containerView.setTag(current);

    }

    @Override
    public int getItemCount() {
        Log.d("Notes Adapter size", String.valueOf(notes.size()));
        return notes.size();
    }

    @Override
    public Filter getFilter() {
        return new NoteFilter();
    }

    public List<Note> getNotes(){
        return notes;
    }

    public void reload() {
        notes = MainActivity.database.noteDao().getAllNotes();
        notifyDataSetChanged();
    }

}

