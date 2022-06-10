package com.simane988.videogametracker;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class DisplayGameDataActivity extends AppCompatActivity {

    protected TextView game_name;
    protected TextView game_owned;
    protected TextView game_completion;
    protected TextView game_rating;
    protected TextView game_genres;
    protected Button edit_button;
    protected Button delete_button;

    protected VideoGame currentVG;
    protected int gameIndex = -1;

    static final int DELETE_GAME = 3;
    static final int EDIT_GAME = 4;
    static final int GAME_WAS_EDITED = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_game_data);


        Intent mIntent = getIntent();
        final String selected_game_name = mIntent.getStringExtra("Video Game Name");
        gameIndex = mIntent.getIntExtra("index", -1);


        TinyDB tinyDB = new TinyDB(this);
        ArrayList<VideoGame> videoGames = tinyDB.getGamesList("Games");
        currentVG = new VideoGame();


        for (VideoGame vg : videoGames) {
            if (vg.getName().equals(selected_game_name))
                currentVG = vg;
        }

        displayGameData();


        edit_button = (Button) findViewById(R.id.display_game_edit_btn);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DisplayGameDataActivity.this, EditGameDataActivity.class);

                intent.putExtra("Name", currentVG.getName());
                intent.putExtra("Rating", currentVG.getRatingAsString());
                intent.putExtra("Completion", currentVG.getCompletionStatus());
                intent.putExtra("Owned", currentVG.isOwned());
                intent.putExtra("Genre", currentVG.getGenres());
                startActivityForResult(intent, EDIT_GAME);
            }
        });


        delete_button = (Button) findViewById(R.id.display_game_delete_btn);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGamePrompt(currentVG.getName(), gameIndex);
            }
        });
    }


    public void displayGameData() {

        game_name = (TextView) findViewById(R.id.display_game_name);
        game_name.setText(currentVG.getName());

        game_owned = (TextView) findViewById(R.id.display_game_owned);
        if (currentVG.isOwned())
            game_owned.setText(R.string.owned_status);
        else
            game_owned.setText(R.string.not_owned_status);

        game_completion = (TextView) findViewById(R.id.display_game_completion);
        game_completion.setText(currentVG.getCompletionStatus());

        game_rating = (TextView) findViewById(R.id.display_game_rating);
        game_rating.setText(currentVG.getRatingAsString());

        game_genres = (TextView) findViewById(R.id.display_game_genres);
        game_genres.setText(currentVG.getGenres());
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


        Intent intent = new Intent();
        intent.putExtra("index", gameIndex);
        setResult(DELETE_GAME, intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_GAME) {
            if (resultCode == RESULT_OK) {

                Log.i("DisplayGame", "ActivityResult: Game Edited Successful!");
                String name = data.getStringExtra("New Game Name");
                Intent intent = new Intent();
                intent.putExtra("New Game Name", name);
                intent.putExtra("Index", gameIndex);
                Log.i("Edited Game Name", name);
                setResult(GAME_WAS_EDITED, intent);
                finish();
            } else {

                Log.i("DisplayGame", "ActivityResult: Game Edit Cancelled!");
                String cancelled = "Editing cancelled!";
                Toast.makeText(getApplicationContext(), cancelled, Toast.LENGTH_LONG).show();
            }
        }
    }
}
