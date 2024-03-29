package com.cuit.likedu.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cuit.likedu.R;
import com.cuit.likedu.base.Constant;
import com.cuit.likedu.bean.RankingList;
import com.cuit.likedu.common.OnRvItemClickListener;
import com.likedu.easyadapter.glide.GlideCircleTransform;

import java.util.List;

public class TopRankAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private LayoutInflater inflater;

    private List<RankingList.MaleBean> groupArray;
    private List<List<RankingList.MaleBean>> childArray;

    private OnRvItemClickListener<RankingList.MaleBean> listener;

    public TopRankAdapter(Context context, List<RankingList.MaleBean> groupArray, List<List<RankingList.MaleBean>> childArray) {
        this.childArray = childArray;
        this.groupArray = groupArray;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return groupArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childArray.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupArray.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childArray.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final View group = inflater.inflate(R.layout.item_top_rank_group, null);

        ImageView ivCover = (ImageView) group.findViewById(R.id.ivRankCover);
        if (!TextUtils.isEmpty(groupArray.get(groupPosition).cover)) {
            Glide.with(mContext).load(Constant.IMG_BASE_URL + groupArray.get(groupPosition).cover).placeholder(R.drawable.avatar_default)
                    .transform(new GlideCircleTransform(mContext)).into(ivCover);
            group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(group, groupPosition, groupArray.get(groupPosition));
                    }
                }
            });
        }

        TextView tvName = (TextView) group.findViewById(R.id.tvRankGroupName);
        tvName.setText(groupArray.get(groupPosition).title);

        ImageView ivArrow = (ImageView) group.findViewById(R.id.ivRankArrow);
        if (childArray.get(groupPosition).size() > 0) {
            if (isExpanded) {
                ivArrow.setImageResource(R.drawable.rank_arrow_up);
            } else {
                ivArrow.setImageResource(R.drawable.rank_arrow_down);
            }
        } else {
            ivArrow.setVisibility(View.GONE);
        }
        return group;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final View child = inflater.inflate(R.layout.item_top_rank_child, null);

        TextView tvName = (TextView) child.findViewById(R.id.tvRankChildName);
        tvName.setText(childArray.get(groupPosition).get(childPosition).title);

        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(child, childPosition, childArray.get(groupPosition).get(childPosition));
            }
        });
        return child;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setItemClickListener(OnRvItemClickListener<RankingList.MaleBean> listener) {
        this.listener = listener;
    }
}
