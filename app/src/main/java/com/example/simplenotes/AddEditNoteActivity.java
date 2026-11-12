package com.example.simplenotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.simplenotes.database.Note;
import com.example.simplenotes.viewmodel.NoteViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditNoteActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.simplenotes.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.simplenotes.EXTRA_TITLE";
    public static final String EXTRA_CONTENT = "com.example.simplenotes.EXTRA_CONTENT";

    private TextInputLayout layoutTitle;
    private TextInputEditText etTitle;
    private TextInputEditText etContent;
    private NoteViewModel noteViewModel;

    private int noteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        layoutTitle = findViewById(R.id.layout_title);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        MaterialButton btnSave = findViewById(R.id.btn_save);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            noteId = intent.getIntExtra(EXTRA_ID, -1);
            setTitle(R.string.edit_note);
            etTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            etContent.setText(intent.getStringExtra(EXTRA_CONTENT));
        } else {
            setTitle(R.string.add_note);
        }

        btnSave.setOnClickListener(v -> saveNote());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        if (deleteItem != null) {
            deleteItem.setVisible(noteId != -1);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_save) {
            saveNote();
            return true;
        } else if (itemId == R.id.action_delete) {
            confirmDeleteNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";

        if (title.isEmpty()) {
            layoutTitle.setError(getString(R.string.title_required));
            etTitle.requestFocus();
            return;
        }

        layoutTitle.setError(null);

        if (noteId == -1) {
            Note note = new Note(title, content, System.currentTimeMillis());
            noteViewModel.insert(note);
            Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show();
        } else {
            Note note = new Note(title, content, System.currentTimeMillis());
            note.setId(noteId);
            noteViewModel.update(note);
            Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void confirmDeleteNote() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_note_confirmation_title)
                .setMessage(R.string.delete_note_confirmation_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    Note note = new Note("", "", 0);
                    note.setId(noteId);
                    noteViewModel.delete(note);
                    Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}

