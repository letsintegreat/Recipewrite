package com.example.finaldemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Map;

import io.appwrite.coroutines.CoroutineCallback;
import io.appwrite.models.Document;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton fab;
    String name;
    ListView listView;
    ArrayList<RecipeModel> recipes = new ArrayList<>();
    ArrayAdapter<RecipeModel> arr;
    String search = "";
    ArrayList<Document> recipesDocuments = new ArrayList<>();
    Boolean loading = false;

    private void reloadList() {
        loading = true;
        Appwrite.listRecipes(search, "", new CoroutineCallback<>((result, error) -> {
            if (error != null) {
                error.printStackTrace();
                return;
            }

            /* Clear recipes list first as we have fresh data */
            recipes.clear();
            recipesDocuments.clear();
            if (result != null) {
                for (Object document: result.getDocuments()) {
                    /* Extract JSON data from the response */
                    Map<String, String> data = (Map<String, String>) ((Document) document).getData();

                    /* Create recipe model from that JSON data */
                    RecipeModel recipe = new RecipeModel(data);

                    /* Add that model to our list of recipes */
                    recipes.add(recipe);
                    recipesDocuments.add((Document) document);
                }
            }

            /* notify the adapter about the changes in list of recipes */
            runOnUiThread(() -> arr.notifyDataSetChanged());
            loading = false;
        }));

    }

    private void loadNextPage() {
        loading = true;
        String lastDocumentId = "";

        /* find the documentId of the last document we have on UI */
        if (!recipesDocuments.isEmpty()) {
            lastDocumentId = recipesDocuments.get(recipesDocuments.size() - 1).getId();
        }

        Appwrite.listRecipes(search, lastDocumentId, new CoroutineCallback<>((result, error) -> {
            if (error != null) {
                error.printStackTrace();
                return;
            }

            if (result != null) {
                for (Object document: result.getDocuments()) {
                    /* Extract JSON data from the response */
                    Map<String, String> data = (Map<String, String>) ((Document) document).getData();

                    /* Create recipe model from that JSON data */
                    RecipeModel recipe = new RecipeModel(data);

                    /* Add that model to our list of recipes */
                    recipes.add(recipe);
                    recipesDocuments.add((Document) document);
                }

                /* notify the adapter about the changes in list of recipes if list is changed */
                if (result.getTotal() != 0) {
                    runOnUiThread(() -> arr.notifyDataSetChanged());
                }
            }
            loading = false;
        }));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        name = getIntent().getStringExtra("name");
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this);

        listView = findViewById(R.id.list_view);
        arr = new ArrayAdapter<>(
            this,
            R.layout.list_item,
            recipes
        );
        listView.setAdapter(arr);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                /* Find out the last item which is visible on our UI */
                int lastInScreen = firstVisibleItem + visibleItemCount;

                if (lastInScreen == totalItemCount) { /* User is looking at the last item of our list, load next page if we aren't already loading. */
                    if (!loading) loadNextPage();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Appwrite.onLogout(this);
        } else if (id == R.id.action_search) {

            /* Declare an AlertDialog to capture search query from user */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Search Recipe");

            /* Initialize a new text field to take user input */
            final EditText input = new EditText(this);

            /* Set its initial value to be the current search query */
            input.setText(search);
            builder.setView(input);

            /* Add a 'Search' button to perform the search */
            builder.setPositiveButton("Search", (dialog, which) -> {

                /* Extract user input */
                search = input.getText().toString();
                /* reload the list */
                reloadList();
            });

            /* Add a 'Clear' button to remove current search query */
            builder.setNegativeButton("Clear", (dialog, which) -> {
                /* reset search to empty string */
                search = "";
                /* reload the list */
                reloadList();
                /* close the dialog */
                dialog.cancel();
            });

            /* Finally show the dialog */
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch ((view.getId())) {

            case R.id.floatingActionButton:

                /* Declare the builder */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                /* Set its title */
                builder.setTitle("Add Recipe");

                /* Create a new text field for recipe */
                final EditText input = new EditText(this);
                builder.setView(input);

                /* Add an 'Add' button to the dialog */
                builder.setPositiveButton("Add", (dialog, which) -> {

                    /* Extract user input */
                    String recipe = input.getText().toString();

                    /* Create a new recipe model */
                    RecipeModel recipeModel = new RecipeModel(name, recipe);

                    /* Add the new recipe to database */
                    Appwrite.onAddRecipe(recipeModel, new CoroutineCallback<>((result, error) -> {
                        /* Do something on success addition to database */
                        reloadList();
                    }));
                });

                /* Add a 'Cancel' button to cancel the dialog */
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                /* Finally show the dialog */
                builder.show();
                break;
        }
    }
}