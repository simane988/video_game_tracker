package com.simane988.videogametracker;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    protected ListView listview;
    protected VideoGameAdapter vg_adapter;
    static final int ADD_GAME_REQUEST = 1;
    static final int DISPLAY_GAME_REQUEST = 2;
    static final int DELETE_GAME = 3;
    static final int GAME_WAS_EDITED = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listview = (ListView) findViewById(R.id.game_list_view);


        TinyDB tinyDB = new TinyDB(this);
        ArrayList<VideoGame> gameList = new ArrayList<VideoGame>();


        if (tinyDB.getBoolean("appHasRunBefore")) {
            Log.i("onCreate", "App has run before!");
            gameList = tinyDB.getGamesList("Games");
        } else {
            Log.i("onCreate", "App has NOT run before!");
            tinyDB.putBoolean("appHasRunBefore", true);
        }


        vg_adapter = new VideoGameAdapter(this, 0, gameList);
        listview.setAdapter(vg_adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String game_name = view.getTag().toString();


                Intent intent = new Intent(MainActivity.this, DisplayGameDataActivity.class);
                intent.putExtra("Video Game Name", game_name);
                intent.putExtra("index", position);
                startActivityForResult(intent, DISPLAY_GAME_REQUEST);
            }
        });

        registerForContextMenu(listview);

        Button add_game = (Button) findViewById(R.id.add_game_button);
        add_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNewGameActivity.class);
                startActivityForResult(intent, ADD_GAME_REQUEST);
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_item_context_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View view = info.targetView;
        int gameIndex = info.position;
        switch (item.getItemId()) {
            case R.id.delete_game:
                String gameToDelete = view.getTag().toString();
                Log.i("Delete Game", gameToDelete);
                deleteGamePrompt(gameToDelete, gameIndex);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void deleteGamePrompt(final String gameToDelete, final int gameIndex) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to delete this game?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Delete Prompt", "Yes button was pressed");
                        deleteGame(gameToDelete, gameIndex);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Delete Prompt", "No button was pressed");
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private void deleteGame(final String gameToDelete, int gameIndex) {

        TinyDB tinyDB = new TinyDB(this);
        ArrayList<VideoGame> videoGames = tinyDB.getGamesList("Games");


        tinyDB.remove("Games");


        Iterator itr = videoGames.iterator();
        while (itr.hasNext()) {
            VideoGame vg = (VideoGame) itr.next();
            if (vg.getName().equals(gameToDelete)) {
                Log.i("Deleting From TinyDB", vg.getName());
                itr.remove();
                break;
            }
        }


        tinyDB.putGamesList("Games", videoGames);


        vg_adapter.removeGameAtPosition(gameIndex);
        vg_adapter.notifyDataSetChanged();
        Log.i("Deleting From ListView", "Success!");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_GAME_REQUEST) {
            handleAddGameResult(resultCode, data);
        } else if (requestCode == DISPLAY_GAME_REQUEST) {
            handleDisplayGameResult(resultCode, data);
        }
    }


    private void handleAddGameResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            VideoGame new_game = getGameByName(data.getStringExtra("New Game Name"));
            vg_adapter.add(new_game);
            String addGameSuccess = "Successfully added game!";
            Toast.makeText(getApplicationContext(), addGameSuccess, Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED) {
            String addGameFail = "Adding game canceled.";
            Toast.makeText(getApplicationContext(), addGameFail, Toast.LENGTH_LONG).show();
        } else {
            String addGameFail = "Adding game failed!";
            Toast.makeText(getApplicationContext(), addGameFail, Toast.LENGTH_LONG).show();
        }
    }


    private void handleDisplayGameResult(int resultCode, Intent data) {
        if (resultCode == DELETE_GAME) {
            Log.i("Returned from DisplayGame, Delete_Game", "Success!");

            int gameIndex = data.getIntExtra("index", -1);
            vg_adapter.removeGameAtPosition(gameIndex);
            vg_adapter.notifyDataSetChanged();

            String delete_success = "Game Deleted Successfully!";
            Toast.makeText(getApplicationContext(), delete_success, Toast.LENGTH_LONG).show();
            Log.i("Delete From ListView", "Success!");
        } else if (resultCode == GAME_WAS_EDITED) {


            VideoGame new_game = getGameByName(data.getStringExtra("New Game Name"));
            vg_adapter.add(new_game);
            vg_adapter.removeGameAtPosition(data.getIntExtra("Index", 0));
            String addGameSuccess = "Successfully Edited Game!";
            Toast.makeText(getApplicationContext(), addGameSuccess, Toast.LENGTH_LONG).show();
        }
    }


    protected VideoGame getGameByName(String gameName) {

        TinyDB tinyDB = new TinyDB(this);
        ArrayList<VideoGame> videoGames = tinyDB.getGamesList("Games");
        VideoGame currentVG = new VideoGame();


        for (VideoGame vg : videoGames) {
            if (vg.getName().equals(gameName))
                currentVG = vg;
        }
        return currentVG;
    }
}
