package edu.harvard.cs50.notes;

// Data access object (Dao) - leverages Dao
// Interface to imitate the methods in the interface with some queries
// Room library generates the classes while compiling
// annotation @Dao tells room what to do with it
// other annotations

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.List;

/***
 * Database Access Object for Notes
 * Uses the DateTypeConverter
 * Contains necessary queries
 */
@Dao
@TypeConverters({DateTypeConverter.class})
public interface NoteDao {
    @Query("INSERT INTO notes (contents) VALUES ('')")
    void create();

    @Query("INSERT INTO notes (contents, dateCreated, dateEdited, favorite) VALUES (:contents, :noteCreated, :noteEdited, 0)")
    void create(String contents, Date noteCreated, Date noteEdited);

    @Query("SELECT * FROM notes ORDER BY favorite DESC, dateEdited DESC")
    List<Note> getAllNotes();

    @Query("SELECT * FROM notes WHERE contents like :text ORDER BY favorite DESC, dateEdited DESC")
    List<Note> getNotes(String text);

    // takes advantage of parameter binding. Colon notation allows for this
    // This SQL binding protects from SQL injection
    @Query("UPDATE notes SET contents = :contents, dateEdited = :noteEdited  where id = :id")
    void save(String contents, Date noteEdited, int id);

    @Query("DELETE FROM notes where id = :id")
    void delete(int id);

    @Query("SELECT * FROM notes ORDER BY id DESC LIMIT 1")
    Note getLastNote();

    @Query("UPDATE notes set favorite = :isFavorite WHERE id = :id")
    void setFavorite(int id, int isFavorite);


}
