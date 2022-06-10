package com.simane988.videogametracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class VideoGameAdapter extends ArrayAdapter<VideoGame> {

    private final Context context;
    private final ArrayList<VideoGame> videoGameList;


    public VideoGameAdapter(Context context, int resource, ArrayList<VideoGame> list) {
        super(context, resource, list);
        this.context = context;
        this.videoGameList = list;
    }


    public void removeGameAtPosition(int gameIndex) {
        videoGameList.remove(gameIndex);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.all_games_list_item, parent, false);

        VideoGame current_game = videoGameList.get(position);


        listItem.setTag(current_game.getName());


        TextView name = (TextView) listItem.findViewById(R.id.video_game_name);
        name.setText(current_game.getName());


        TextView rating = (TextView) listItem.findViewById(R.id.video_game_rating);
        String rating_text = "Rating: " + current_game.getRating();
        rating.setText(rating_text);


        TextView completion_status = (TextView) listItem.findViewById(R.id.video_game_status);
        String c_status_text = "Status: " + current_game.getCompletionStatus();
        completion_status.setText(c_status_text);

        return listItem;
    }
}
