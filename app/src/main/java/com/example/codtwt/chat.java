package com.example.codtwt;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class chat extends RecyclerView.Adapter<chat.ViewHolder> {
    private List<ChatMessage> dataList;
    private Context context;
    private updateDb updb;

    public chat(Context context, List<ChatMessage> dataList) {

        this.context = context;
        this.dataList = dataList;
        updb=new updateDb();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage c=dataList.get(position);
        holder.textView.setText(c.getName());
        holder.msgText.setText(c.getMessage());
        String url=c.getUrl();
        Picasso.get().load(url).into(holder.profile);
        holder.likeCountTextView.setText(""+c.getLikeCount());
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.likeCountTextView.setText(""+(c.getLikeCount()+1));
                holder.likeBtn.setImageResource(R.drawable.ic_baseline_thumb_up_24_blue);
                updb.updateLike(c.getMsgId(),context);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToSend="Name :"+c.getName()+"\n"+"Message : "+c.getMessage();
                Intent sendIntent=new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT,textToSend);
                Intent shareIntent=Intent.createChooser(sendIntent,"Share via");
                if(shareIntent.resolveActivity(context.getPackageManager())!=null){
                    context.startActivity(shareIntent);
                }
            }
        });

        holder.replyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,Replies.class);
                intent.putExtra("key",c.getMsgId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView msgText,likeCountTextView;
        public ImageView profile,likeBtn,share,replyIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.name);
            msgText=itemView.findViewById(R.id.msgText);
            profile=itemView.findViewById(R.id.profileImg);
            likeCountTextView=itemView.findViewById(R.id.likeCountTextView);
            likeBtn=itemView.findViewById(R.id.likeBtn);
            share=itemView.findViewById(R.id.share);
            replyIcon=itemView.findViewById(R.id.replyIcon);


        }
    }
}
