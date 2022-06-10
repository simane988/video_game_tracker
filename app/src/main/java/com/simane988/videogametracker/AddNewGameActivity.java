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

public class AddNewGameActivity extends AppCompatActivity {

    protected EditText gameName;
    protected EditText gameRating;
    protected EditText gameCompletion;
    protected EditText gameGenres;
    protected Spinner gameOwned;
    protected Button finishButton;
    protected Button cancelButton;

    static final int DELETE_GAME = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_game);


        gameName = (EditText) findViewById(R.id.add_game_name_input);
        gameRating = (EditText) findViewById(R.id.add_game_rating_input);
        gameCompletion = (EditText) findViewById(R.id.add_game_comp_input);
        gameGenres = (EditText) findViewById(R.id.add_game_genres_input);
        gameOwned = (Spinner) findViewById(R.id.add_game_owned_spinner);
        finishButton = (Button) findViewById(R.id.add_game_finish_btn);
        cancelButton = (Button) findViewById(R.id.add_game_cancel_button);


        gameRating.setFilters(new InputFilter[]{new InputFilterMinMax("1", "10")});


        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGame();
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


    public void addGame() {

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
        addGameToCollection(videoGame);


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


    public void addGameToCollection(VideoGame new_game) {
        TinyDB tinyDB = new TinyDB(this);


        ArrayList<VideoGame> videoGames = tinyDB.getGamesList("Games");
        tinyDB.remove("Games");


        videoGames.add(new_game);
        tinyDB.putGamesList("Games", videoGames);
    }
}
