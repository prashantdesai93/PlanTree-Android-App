package com.ideas2app.plantree;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConcernAdapter extends ArrayAdapter<Concern> {
    private final Context context;
    Concern event;
    //private RemoveThreadCallback callback;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    List<Concern> objects;

    /*public void setCallback(RemoveThreadCallback callback){
        this.callback = callback;
    }*/

    public ConcernAdapter(@NonNull Context context, int resource, @NonNull List<Concern> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        Log.d("demo", "Helloooooooooooooooooo");
        //AllMessageThreads allMessageThreads = getItem(position);
        event = getItem(position);
//        Log.d("demo", "MessageThreadAdapter : getView: t = "+t.toString());
        //Log.d("demo", "getView: "+allMessageThreads.threads.get(0).title);
        ViewHolder viewHolder;
        if(convertView ==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.concern_item ,parent,false);
            viewHolder = new ViewHolder();
            //title, upvote, downvote
            viewHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
            viewHolder.tvUpvote = convertView.findViewById(R.id.tvUpvote);
            viewHolder.tvDownvote = convertView.findViewById(R.id.tvDownvote);
            /*viewHolder.tvMessage = convertView.findViewById(R.id.tvMessage);
            viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
            viewHolder.ivIsRead = convertView.findViewById(R.id.ivIsRead);*/
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //for (int i = 0; i < allMessageThreads.threads.size(); i++) {
//        Log.d("demo", "getView: "+t.title);
        viewHolder.tvTitle.setText(event.cTitle);
        viewHolder.tvUpvote.setText("     "+event.cUpVotes);
        viewHolder.tvDownvote.setText("     "+event.cDownVotes);
        /*viewHolder.tvMessage.setText(m.message);

        viewHolder.tvDate.setText(formatDate(m.date));
        if(m.isMessageRead){
            viewHolder.ivIsRead.setImageResource(R.drawable.circle_grey);
        }else{
            viewHolder.ivIsRead.setImageResource(R.drawable.circle_blue);
        }*/
        return convertView;
    }

    public String formatDate(String input){
        //SimpleDateFormat parser = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
        //SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy, hh:mm a");
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        String formattedDate=null;
        Date date=null;
        date= new Date(input);
        formattedDate= formatter.format(date);
        return formattedDate;
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private static class ViewHolder{
        TextView tvTitle;
        TextView tvUpvote;
        TextView tvDownvote;
        /*TextView tvDate;
        TextView tvMessage;
        ImageView ivIsRead;*/
    }
}
