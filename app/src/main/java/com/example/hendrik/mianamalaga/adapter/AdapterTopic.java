package com.example.hendrik.mianamalaga.adapter;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.hendrik.mianamalaga.R;
import com.example.hendrik.mianamalaga.container.Topic;

import java.io.File;
import java.util.ArrayList;


public class AdapterTopic extends RecyclerView.Adapter<AdapterTopic.TopicViewHolder>{

    public interface OnItemClickListener{
        void onItemClicked(int position, TopicViewHolder viewHolder);
    }

    public interface OnItemLongClickListener{
        boolean onItemLongClicked(int position, TopicViewHolder viewHolder);
    }

    private ArrayList<Topic> mTopicArrayList;
    private Context mContext;
    private int mResourceId = 0;
    public OnItemClickListener mOnItemClickListener;
    public OnItemLongClickListener mOnItemCLongClickListener;
    private int mExpandedPosition = -1;
    private int mPreviousExpandedPosition;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mOnItemCLongClickListener = listener;
    }


    public class TopicViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitleTextView;
        public TextView mInfoTextView;
        public ImageView mImageView;
        public ImageView mIconImageView;
        public View mBaseView;
        public TextView mMoreButton;
        public RelativeLayout mDetailsLayout;
        public TextView mSizeTextView;
        public TextView mAuthorName;

        TopicViewHolder(View baseView){
            super(baseView);
            mTitleTextView = baseView.findViewById(R.id.topic_listView_element_title);
            mInfoTextView = baseView.findViewById(R.id.topic_listView_element_info);
            mImageView = baseView.findViewById(R.id.topic_listView_element_image);
            mIconImageView = baseView.findViewById(R.id.topic_listView_element_loadIcon);
            mMoreButton = baseView.findViewById(R.id.topic_listView_element_more);
            mDetailsLayout = baseView.findViewById(R.id.topic_listView_element_long_layout);
            mSizeTextView = baseView.findViewById(R.id.topic_listView_element_size);
            mAuthorName = baseView.findViewById(R.id.topic_listView_element_author_name);
            mBaseView = baseView;
        }

    }

    public AdapterTopic(Context context, ArrayList<Topic> arrayList){
        mTopicArrayList = arrayList;
        mContext = context;
    }

    public AdapterTopic(Context context, ArrayList<Topic> arrayList, int resourceId){
        mTopicArrayList = arrayList;
        mContext = context;
        mResourceId = resourceId;
    }

    @Override
    public AdapterTopic.TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType){             // Create new views (invoked by the layout manager)
        View baseView;
        if (mResourceId != 0){
            baseView = LayoutInflater.from(parent.getContext()).inflate(mResourceId, parent, false);
        } else {
            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_topic, parent, false);
        }

        TopicViewHolder viewHolder = new TopicViewHolder(baseView);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(TopicViewHolder viewHolder, int position){
        Topic topic = mTopicArrayList.get(position);

        viewHolder.mTitleTextView.setText(topic.getName());
        viewHolder.mInfoTextView.setText(topic.getShortInfo());

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .override(300, 300);

        Glide.with(mContext)
                .load(Uri.fromFile(new File(topic.getImageFileString())))
                .apply(requestOptions)
                .into(viewHolder.mImageView);

        viewHolder.mBaseView.setOnClickListener(v -> mOnItemClickListener.onItemClicked(position, viewHolder ));
        viewHolder.mInfoTextView.setOnClickListener(v -> mOnItemClickListener.onItemClicked(position, viewHolder ));

        viewHolder.mBaseView.setOnLongClickListener(v -> {
            mOnItemCLongClickListener.onItemLongClicked(position, viewHolder);
            return true;
        });

        viewHolder.mInfoTextView.setOnLongClickListener(v -> {
            mOnItemCLongClickListener.onItemLongClicked(position, viewHolder);
            return true;
        });

        final boolean isExpanded = position == mExpandedPosition;
        viewHolder.mDetailsLayout.setVisibility( isExpanded ? View.VISIBLE : View.GONE);
        viewHolder.itemView.setActivated(isExpanded);

        if ( isExpanded )
            mPreviousExpandedPosition = position;

        viewHolder.mMoreButton.setOnClickListener(v -> {
            mExpandedPosition = isExpanded ? -1 : position;
            notifyItemChanged(mPreviousExpandedPosition);
            notifyItemChanged(position);
        });

        viewHolder.mSizeTextView.setText( topic.getFolderSize()/1000000 + " MB");
        viewHolder.mAuthorName.setText( topic.getAuthor() );

    }

    @Override
    public int getItemCount(){
        return mTopicArrayList == null ? 0 : mTopicArrayList.size();
    }


    public void remove(int position){
        mTopicArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(Topic topic) {
        mTopicArrayList.add(topic);
        notifyDataSetChanged();
    }

    public void set(int position, Topic topic) {
        mTopicArrayList.set(position, topic);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }
}


