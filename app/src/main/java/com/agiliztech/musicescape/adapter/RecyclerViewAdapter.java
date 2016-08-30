package com.agiliztech.musicescape.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agiliztech.musicescape.R;
import com.agiliztech.musicescape.models.SongsModel;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;

/**
 * Created by Asif on 14-08-2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public interface IClickListener {
        void playSelectedSong(int position, View view);
    }


    IClickListener iClickListener;
    List<SongsModel> listOfSongs;
    Context context;

    public RecyclerViewAdapter(List<SongsModel> listOfSongs, IClickListener iClickListener, Context context) {
        this.listOfSongs = listOfSongs;
        this.iClickListener = iClickListener;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return listOfSongs.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        final int pos = position;
        SongsModel model = listOfSongs.get(position);
        viewBinderHelper.bind(holder.swipe_layout, String.valueOf(model.getId()));
        viewBinderHelper.bind(holder.rv_ll, String.valueOf(model.getId()));

        holder.rv_song_name.setText(model.getTitle());
        holder.rv_song_detail.setText(model.getArtist());
        holder.rv_ll.setTag(pos);
        holder.rv_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // iClickListener.playSelectedSong(pos,holder.rv_ll);
            }
        });
        holder.swipe_layout.setSwipeListener(new SwipeRevealLayout.SimpleSwipeListener() {
            @Override
            public void onClosed(SwipeRevealLayout view) {
                super.onClosed(view);
                holder.rv_ll.setLockDrag(false);
            }

            @Override
            public void onOpened(SwipeRevealLayout view) {
                super.onOpened(view);
                holder.rv_ll.setLockDrag(true);
            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {
                //super.onSlide(view, slideOffset);
                if (holder.swipe_layout.isOpened()) {
                    holder.swipe_layout.close(true);
                } else if (holder.rv_ll.isOpened()) {
                    holder.rv_ll.close(true);
                }
            }
        });

        holder.rv_ll.setSwipeListener(new SwipeRevealLayout.SimpleSwipeListener() {
            @Override
            public void onClosed(SwipeRevealLayout view) {
                super.onClosed(view);
                holder.swipe_layout.setLockDrag(false);
            }

            @Override
            public void onOpened(SwipeRevealLayout view) {
                super.onOpened(view);
                holder.swipe_layout.setLockDrag(true);
            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {
                //super.onSlide(view, slideOffset);
                if (holder.swipe_layout.isOpened()) {
                    holder.swipe_layout.close(true);
                } else if (holder.rv_ll.isOpened()) {
                    holder.rv_ll.close(true);
                }
            }
        });

        holder.rv_song_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickListener.playSelectedSong(pos, holder.rv_ll);
            }
        });

        holder.rv_retag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, "Retag Clicked", Toast.LENGTH_SHORT).show();
                if (holder.swipe_layout.isOpened()) {
                    holder.swipe_layout.close(true);
                } else if (holder.rv_ll.isOpened()) {
                    holder.rv_ll.close(true);
                }


            }
        });

        holder.rv_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.swipe_layout.isOpened()) {
                    holder.swipe_layout.close(true);
                } else if (holder.rv_ll.isOpened()) {
                    holder.rv_ll.close(true);
                }
            }
        });

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView rv_song_name;
        private TextView rv_song_detail;
        private SwipeRevealLayout rv_ll, swipe_layout;
        private ImageView rv_retag;
        private ImageView rv_swap;

        public MyViewHolder(View itemView) {
            super(itemView);
            swipe_layout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            rv_song_name = (TextView) itemView.findViewById(R.id.rv_song_name);
            rv_ll = (SwipeRevealLayout) itemView.findViewById(R.id.click_layout);
            rv_song_detail = (TextView) itemView.findViewById(R.id.rv_song_detail);
            rv_retag = (ImageView) itemView.findViewById(R.id.rv_retag);
            rv_swap = (ImageView) itemView.findViewById(R.id.rv_swap);
        }
    }
}
