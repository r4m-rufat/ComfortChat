package com.example.comfortchat.Adapters;

import android.content.Context;
import android.os.Build;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comfortchat.Models.CallList;
import com.example.comfortchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerCallsAdapter extends RecyclerView.Adapter<RecyclerCallsAdapter.ViewHolder> {

    private Context context;
    private List<CallList> callList;

    public RecyclerCallsAdapter(Context context, List<CallList> callList) {
        this.context = context;
        this.callList = callList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_for_calls, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.username.setText(callList.get(position).getUsername());
        holder.date.setText(callList.get(position).getDate());

        if (callList.get(position).getCallType().equals("incoming")){
            holder.arrowType.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward));
        }else if(callList.get(position).getCallType().equals("outgoing")){
            holder.arrowType.setImageDrawable(context.getDrawable(R.drawable.ic_upward));
        }

        Glide.with(context).load(callList.get(position).getUrlProfile()).into(holder.profileImage);

    }

    @Override
    public int getItemCount() {
        return callList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, date;
        private ImageView arrowType;
        private CircleImageView profileImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.profilenameforCalls);
            date = itemView.findViewById(R.id.dateforCalls);
            arrowType = itemView.findViewById(R.id.arrowforCalling);
            profileImage = itemView.findViewById(R.id.circleProfileImageForCalls);

        }
    }
}
