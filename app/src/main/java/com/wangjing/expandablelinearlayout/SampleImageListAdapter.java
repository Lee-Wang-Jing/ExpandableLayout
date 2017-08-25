package com.wangjing.expandablelinearlayout;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wangjing.expandablelayout.ExpandableImageView;
import com.wangjing.expandablelayout.ExpandableTextview;

public class SampleImageListAdapter extends BaseAdapter {

    private final Context mContext;
    private final SparseBooleanArray mCollapsedStatus;
    private final String[] sampleStrings;

    public SampleImageListAdapter(Context context) {
        mContext  = context;
        mCollapsedStatus = new SparseBooleanArray();
        sampleStrings = mContext.getResources().getStringArray(R.array.sampleStrings);
    }

    @Override
    public int getCount() {
        return sampleStrings.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item1, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.expandableImageView =  convertView.findViewById(R.id.expand_imageview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.expandableImageView.setText(sampleStrings[position], mCollapsedStatus, position);

        return convertView;
    }


    private static class ViewHolder{
        ExpandableImageView expandableImageView;
    }
}