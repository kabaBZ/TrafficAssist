package com.zjy.trafficassist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zjy.trafficassist.R;
import com.zjy.trafficassist.listener.RecyclerItemClickListener;
import com.zjy.trafficassist.model.AlarmHistory;
import com.zjy.trafficassist.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by ZJY on 2016/4/20.
 */
public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<AlarmHistory> alarmHistories;
    private RecyclerItemClickListener mItemClickListener;

    public HistoryListAdapter(Context context, ArrayList<AlarmHistory> alarmHistories) {
        this.mContext = context;
        this.alarmHistories = alarmHistories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(v, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text_id.setText("ID：" + String.valueOf(getItemId(position) + 1));
        holder.text_username.setText("用户名：" + alarmHistories.get(position).getUsername());
        holder.text_acctag.setText("详情：" + alarmHistories.get(position).getaccidentTags());
        if(alarmHistories.get(position).getPicUrl() != null && alarmHistories.get(position).getPicUrl().size() > 0){
            Glide.with(mContext)
                    .load(alarmHistories.get(position).getPicUrl().get(0))
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.his_pic);
            LogUtil.i(position + "  " + alarmHistories.get(position).getPicUrl().get(0));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return alarmHistories == null ? 0 : alarmHistories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text_id;
        TextView text_username;
        TextView text_acctag;
        ImageView his_pic;
        RecyclerItemClickListener ItemClickListener;

        ViewHolder(View itemView, RecyclerItemClickListener ItemClickListener) {
            super(itemView);
            text_id = (TextView) itemView.findViewById(R.id.text_id);
            text_username = (TextView) itemView.findViewById(R.id.text_username);
            text_acctag = (TextView) itemView.findViewById(R.id.text_acctag);
            his_pic = (ImageView) itemView.findViewById(R.id.his_pic);

            this.ItemClickListener = ItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView){
                if(this.ItemClickListener != null){
                    this.ItemClickListener.onItemClick(view, getLayoutPosition());
                }
            }
        }
    }

    public void setOnRecyclerItemClickListener(RecyclerItemClickListener listener){
        this.mItemClickListener = listener;
    }
}
