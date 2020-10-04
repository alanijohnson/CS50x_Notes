package edu.harvard.cs50.notes;

// annotation @Database lets Dao use the database

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;

/**
 * Database that contains the Note Entity
 * Contains only one migration from version 1 to 2
 */
@Database(entities = {Note.class}, version = 3)
public abstract class NoteDatabase extends RoomDatabase {
    // Room library will write this code.
    public abstract NoteDao noteDao();

    /**
     * Migration from Version 1 to 2
     * Added 3 columns:
     * 1. Date Created - Date was initialized to date the migration was run
     * 2. Date Edited - Date was initialized to date the migration was run
     * 3. Favorite - indicates if the user favorited a note or not.
     */
    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //database.execSQL("ALTER TABLE notes2 RENAME TO notes");
            database.execSQL("ALTER TABLE notes ADD COLUMN dateCreated INTEGER DEFAULT " + new Date().getTime());
            database.execSQL("ALTER TABLE notes ADD COLUMN dateEdited INTEGER DEFAULT " + new Date().getTime());
            database.execSQL("ALTER TABLE notes ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0");

        }
    };

    /**
     * Migration from Version 1 to 2
     * Added 3 columns:
     * 1. Date Created - Date was initialized to date the migration was run
     * 2. Date Edited - Date was initialized to date the migration was run
     * 3. Favorite - indicates if the user favorited a note or not.
     */
    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE notesTemp (id INTEGER NOT NULL, contents TEXT, dateCreated INTEGER, dateEdited INTEGER, favorite INTEGER NOT NULL, PRIMARY KEY(id))");
            database.execSQL("INSERT INTO notesTemp SELECT * FROM notes");
            database.execSQL("DROP TABLE notes");
            database.execSQL("ALTER TABLE notesTemp RENAME TO notes");
        }
    };

}
