package com.cryptocodes.materialdesign;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jonathanf on 11/3/2015.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<FeedListRowHolder> {

    private List<FeedItem> feedItemList;
    private Context mContext;

    public MyRecyclerAdapter(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public FeedListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
        FeedListRowHolder mh = new FeedListRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(FeedListRowHolder feedListRowHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);

        Picasso.with(mContext).load(feedItem.getThumbnail())
                .error(R.drawable.ic_action_star_rate)
                .placeholder(R.drawable.ic_action_star_rate)
                .into(feedListRowHolder.thumbnail);

        feedListRowHolder.title.setText(Html.fromHtml(feedItem.getTitle()));
        feedListRowHolder.header.setText(Html.fromHtml(feedItem.getHeader()));
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
}
