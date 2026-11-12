package com.example.simplenotes.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.simplenotes.database.Note;
import com.example.simplenotes.database.NoteDao;
import com.example.simplenotes.database.NoteDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {

    private final NoteDao noteDao;
    private final LiveData<List<Note>> allNotes;
    private final ExecutorService executor;

    public NoteRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<List<Note>> searchNotes(String query) {
        return noteDao.searchNotes("%" + query + "%");
    }

    public void insert(Note note) {
        executor.execute(() -> noteDao.insert(note));
    }

    public void update(Note note) {
        executor.execute(() -> noteDao.update(note));
    }

    public void delete(Note note) {
        executor.execute(() -> noteDao.delete(note));
    }

    public void deleteAllNotes() {
        executor.execute(noteDao::deleteAllNotes);
    }
}

