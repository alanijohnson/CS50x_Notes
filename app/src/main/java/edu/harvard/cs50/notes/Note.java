package edu.harvard.cs50.notes;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

// utilizing room library to make Note object
// Note object is represented in a SQLite table named notes
// the table has 5 columns
// annotations allow for linkage of fields

@Entity(tableName = "notes")
@TypeConverters({DateTypeConverter.class})
public class Note {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "contents")
    public String contents;

    @ColumnInfo(name = "dateCreated")
    public Date dateCreated = new Date();

    @ColumnInfo(name = "dateEdited")
    public Date dateEdited = new Date();

    @ColumnInfo(name = "favorite")
    public boolean favorite = false;

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
        int isFavorite = favorite ? 1 : 0;
        MainActivity.database.noteDao().setFavorite(id, isFavorite);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // Redefine equals method to determine if a note is equivalent to another.
        // If the object is a note and the ids are the same, notes are considered equal

        if( obj.getClass() != this.getClass()) {
            return false;
        }

        Note n = (Note) obj;
        if (n.id == this.id) {
            return true;
        } else {
            return false;
        }
    }
}
