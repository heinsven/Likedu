package com.justwayward.reader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.justwayward.reader.R;
import com.justwayward.reader.base.BaseRVActivity;
import com.justwayward.reader.base.Constant;
import com.justwayward.reader.bean.CommentList;
import com.justwayward.reader.bean.Disscussion;
import com.justwayward.reader.common.OnRvItemClickListener;
import com.justwayward.reader.component.AppComponent;
import com.justwayward.reader.component.DaggerCommunityComponent;
import com.justwayward.reader.ui.adapter.BestCommentListAdapter;
import com.justwayward.reader.ui.contract.ComOverallDetailContract;
import com.justwayward.reader.ui.easyadapter.CommentListAdapter;
import com.justwayward.reader.ui.presenter.ComOverallDetailPresenter;
import com.justwayward.reader.utils.GlideCircleTransform;
import com.justwayward.reader.utils.RelativeDateFormat;
import com.justwayward.reader.view.BookContentTextView;
import com.justwayward.reader.view.SupportDividerItemDecoration;
import com.justwayward.reader.view.recyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ComOverallDetailActivity extends BaseRVActivity implements ComOverallDetailContract
        .View, OnRvItemClickListener<CommentList.CommentsBean> {

    private static final String INTENT_ID = "id";

    public static void startActivity(Context context, String id) {
        context.startActivity(new Intent(context, ComOverallDetailActivity.class)
                .putExtra(INTENT_ID, id));
    }

    private String id;

    @Inject
    ComOverallDetailPresenter mPresenter;

    private List<CommentList.CommentsBean> mBestCommentList = new ArrayList<>();
    private BestCommentListAdapter mBestCommentListAdapter;

    private HeaderViewHolder headerViewHolder;

    static class HeaderViewHolder {
        @Bind(R.id.ivBookCover)
        ImageView ivAvatar;
        @Bind(R.id.tvBookTitle)
        TextView tvNickName;
        @Bind(R.id.tvTime)
        TextView tvTime;
        @Bind(R.id.tvTitle)
        TextView tvTitle;
        @Bind(R.id.tvContent)
        BookContentTextView tvContent;
        @Bind(R.id.tvBestComments)
        TextView tvBestComments;
        @Bind(R.id.rvBestComments)
        RecyclerView rvBestComments;
        @Bind(R.id.tvCommentCount)
        TextView tvCommentCount;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);   //view绑定
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_com_overall_detail;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerCommunityComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("详情");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        id = getIntent().getStringExtra(INTENT_ID);

        mPresenter.attachView(this);
        mPresenter.getDisscussionDetail(id);
        mPresenter.getBestComments(id);
        mPresenter.getDisscussionComments(id,start, limit);
    }

    @Override
    public void configViews() {
        mAdapter = new CommentListAdapter(mContext);
        modiifyAdapter(false, true);

//        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, 1,0,0);
//        itemDecoration.setDrawLastItem(true);
//        itemDecoration.setDrawHeaderFooter(true);
//        mRecyclerView.addItemDecoration(itemDecoration);

        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                View headerView =  LayoutInflater.from(ComOverallDetailActivity.this).inflate(R.layout.header_view_com_overall_detail, parent, false);
                return headerView;
            }

            @Override
            public void onBindView(View headerView) {
                headerViewHolder = new HeaderViewHolder(headerView);
            }
        });

    }

    @Override
    public void showDisscussion(Disscussion disscussion) {
        Glide.with(mContext).load(Constant.IMG_BASE_URL + disscussion.post.author.avatar)
                .placeholder(R.drawable.avatar_default)
                .transform(new GlideCircleTransform(mContext))
                .into(headerViewHolder.ivAvatar);

        headerViewHolder.tvNickName.setText(disscussion.post.author.nickname);
        headerViewHolder.tvTime.setText(RelativeDateFormat.format(disscussion.post.created));
        headerViewHolder.tvTitle.setText(disscussion.post.title);
        headerViewHolder.tvContent.setText(disscussion.post.content);
    }

    @Override
    public void showBestComments(CommentList list) {
        if(list.comments.isEmpty()){
            gone(headerViewHolder.tvBestComments, headerViewHolder.rvBestComments);
        }else{
            mBestCommentList.addAll(list.comments);
            headerViewHolder.rvBestComments.setHasFixedSize(true);
            headerViewHolder.rvBestComments.setLayoutManager(new LinearLayoutManager(this));
            headerViewHolder.rvBestComments.addItemDecoration(new SupportDividerItemDecoration(mContext, LinearLayoutManager.VERTICAL, true));
            mBestCommentListAdapter = new BestCommentListAdapter(mContext, mBestCommentList);
            mBestCommentListAdapter.setOnItemClickListener(this);
            headerViewHolder.rvBestComments.setAdapter(mBestCommentListAdapter);
            visible(headerViewHolder.tvBestComments, headerViewHolder.rvBestComments);
        }
    }

    @Override
    public void showDisscussionComments(CommentList list) {
        headerViewHolder.tvCommentCount.setText(String.format(mContext.getString(R.string.comment_comment_count), list.comments.size()));
        mAdapter.addAll(list.comments);
        start=start+list.comments.size();
    }

    @Override
    public void complete() {

    }

    @Override
    public void onLoadMore() {
        super.onLoadMore();
        mPresenter.getDisscussionComments(id,start, limit);
    }

    @Override
    public void onItemClick(View view, int position, CommentList.CommentsBean data) {

    }

    @Override
    public void onItemClick(int position) {
        CommentList.CommentsBean data  = (CommentList.CommentsBean) mAdapter.getItem(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}