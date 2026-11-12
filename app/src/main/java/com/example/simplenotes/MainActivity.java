package com.example.simplenotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenotes.adapter.NoteAdapter;
import com.example.simplenotes.database.Note;
import com.example.simplenotes.viewmodel.NoteViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private View layoutEmpty;
    private LiveData<List<Note>> currentLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        layoutEmpty = findViewById(R.id.layout_empty);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        observeNotes(noteViewModel.getAllNotes());

        adapter.setOnItemClickListener(note -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
            intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
            intent.putExtra(AddEditNoteActivity.EXTRA_CONTENT, note.getContent());
            startActivity(intent);
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            startActivity(intent);
        });

        setupSwipeToDelete();
    }

    private void observeNotes(LiveData<List<Note>> liveData) {
        if (currentLiveData != null) {
            currentLiveData.removeObservers(this);
        }
        currentLiveData = liveData;
        currentLiveData.observe(this, notes -> {
            adapter.submitList(notes);
            boolean isEmpty = (notes == null || notes.isEmpty());
            layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                Note deletedNote = adapter.getNoteAt(position);
                noteViewModel.delete(deletedNote);

                Snackbar.make(coordinatorLayout, R.string.note_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, v -> noteViewModel.insert(deletedNote))
                        .show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setQueryHint(getString(R.string.search_hint));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText == null || newText.trim().isEmpty()) {
                            observeNotes(noteViewModel.getAllNotes());
                        } else {
                            observeNotes(noteViewModel.searchNotes(newText.trim()));
                        }
                        return true;
                    }
                });
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete_all) {
            confirmDeleteAllNotes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDeleteAllNotes() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_all_confirmation_title)
                .setMessage(R.string.delete_all_confirmation_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    noteViewModel.deleteAllNotes();
                    Toast.makeText(this, R.string.all_notes_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
