package com.simane988.videogametracker;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class EditGameDataActivity extends AppCompatActivity {

    protected EditText gameName;
    protected EditText gameRating;
    protected EditText gameCompletion;
    protected EditText gameGenres;
    protected Spinner gameOwned;
    protected Button finishButton;
    protected Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game_data);


        gameName = (EditText) findViewById(R.id.edit_game_name_input);
        gameRating = (EditText) findViewById(R.id.edit_game_rating_input);
        gameCompletion = (EditText) findViewById(R.id.edit_game_comp_input);
        gameGenres = (EditText) findViewById(R.id.edit_game_genres_input);
        gameOwned = (Spinner) findViewById(R.id.edit_game_owned_spinner);
        finishButton = (Button) findViewById(R.id.edit_game_finish_btn);
        cancelButton = (Button) findViewById(R.id.edit_game_cancel_btn);


        Intent mIntent = getIntent();
        final String original_game_name = mIntent.getStringExtra("Name");
        final String original_game_rating = mIntent.getStringExtra("Rating");
        final String original_game_compl = mIntent.getStringExtra("Completion");
        final Boolean original_game_owned = mIntent.getBooleanExtra("Owned", false);
        final String original_game_genre = mIntent.getStringExtra("Genre");


        gameName.setText(original_game_name);
        gameRating.setText(original_game_rating);
        gameCompletion.setText(original_game_compl);
        if (original_game_owned)
            gameOwned.setSelection(0);
        else
            gameOwned.setSelection(1);
        gameGenres.setText(original_game_genre);


        gameRating.setFilters(new InputFilter[]{new InputFilterMinMax("1", "10")});


        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editGameData(original_game_name);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });
    }


    public void editGameData(String original_game_name) {

        if (!checkUserInputExists()) {
            String noInputString = "One or more fields are empty!";
            Toast.makeText(getApplicationContext(), noInputString, Toast.LENGTH_LONG).show();
            return;
        }


        String name = gameName.getText().toString();
        Boolean owned = getOwnedValue();
        String completion = gameCompletion.getText().toString();
        int rating = Integer.parseInt(gameRating.getText().toString());
        String genre = gameGenres.getText().toString();
        VideoGame videoGame = new VideoGame(name, owned, completion, rating, genre);


        addGameToCollection(videoGame, original_game_name);


        Intent intent = new Intent();
        intent.putExtra("New Game Name", name);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    public boolean checkUserInputExists() {
        if (TextUtils.isEmpty(gameName.getText())) {
            Log.i("checkUserInputExists", "Name DNE!");
            return false;
        } else if (TextUtils.isEmpty(gameCompletion.getText())) {
            Log.i("checkUserInputExists", "Completion DNE!");
            return false;
        } else if (TextUtils.isEmpty(gameRating.getText())) {
            Log.i("checkUserInputExists", "Rating DNE!");
            return false;
        } else if (TextUtils.isEmpty(gameGenres.getText())) {
            Log.i("checkUserInputExists", "Genre DNE!");
            return false;
        } else {
            Log.i("checkUserInputExists", "Everything Exists!");
            return true;
        }
    }


    public boolean getOwnedValue() {
        String text = gameOwned.getSelectedItem().toString();
        return text.equals("Yes");
    }


    public void addGameToCollection(VideoGame new_game, String original_game_name) {
        TinyDB tinyDB = new TinyDB(this);


        ArrayList<VideoGame> videoGames = tinyDB.getGamesList("Games");
        tinyDB.remove("Games");


        Iterator itr = videoGames.iterator();
        while (itr.hasNext()) {
            VideoGame vg = (VideoGame) itr.next();
            if (vg.getName().equals(original_game_name)) {
                Log.i("Deleting From TinyDB: ", vg.getName());
                itr.remove();
                break;
            }
        }


        videoGames.add(new_game);
        tinyDB.putGamesList("Games", videoGames);
    }
}
