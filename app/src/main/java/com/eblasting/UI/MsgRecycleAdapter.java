package com.eblasting.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eblasting.Bean.MsgBean;
import com.eblasting.R;

import java.util.List;

public class MsgRecycleAdapter extends RecyclerView.Adapter<MsgRecycleAdapter.ViewHolder>{

    private Context mContext;
    List<MsgBean> list;

    public MsgRecycleAdapter(Context context, List<MsgBean> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public MsgRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MsgRecycleAdapter.ViewHolder holder = new MsgRecycleAdapter.ViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.layout_msglistunit, parent,
                false));
        return holder;
    }


    @Override
    public void onBindViewHolder(MsgRecycleAdapter.ViewHolder holder, final int position)
    {

        final int mposition = position%list.size();
        holder.tv_msgInfo.setText(list.get(mposition).getMsgInfo());
        holder.tv_msgTime.setText(list.get(mposition).getMsgTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onClick(mposition);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
        //return list.size();
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    MsgRecycleAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(MsgRecycleAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }




    class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView tv_msgInfo;
        TextView tv_msgTime;


        public ViewHolder(View view)
        {
            super(view);
            tv_msgInfo = (TextView)view.findViewById(R.id.tv_msgInfo);
            tv_msgTime = (TextView)view.findViewById(R.id.tv_msgTime);


        }


    }
}