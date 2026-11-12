package com.example.simplenotes.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM notes")
    void deleteAllNotes();

    @Query("SELECT * FROM notes ORDER BY date DESC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM notes WHERE title LIKE :query OR content LIKE :query ORDER BY date DESC")
    LiveData<List<Note>> searchNotes(String query);
}

