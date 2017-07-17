package com.wangjing.expandablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 作者：Created by WangJing on 2017/7/17.
 * 邮箱：wangjinggm@gmail.com
 * 描述：展开收起的Layout
 * 最近修改：2017/7/17 15:26 by WangJing
 */

public class ExpandableTextview extends LinearLayout implements View.OnClickListener {

    //默认的显示最多的行数
    private static final int MAX_COLLAPSED_LINES = 8;
    //默认的展开收起动画时间
    private static final int DEFAULT_ANIM_DURATION = 300;
    //动画开始时默认的alpha值
    private static final float DEFAULT_ANIM_ALPHA_START = 0.7f;


    private int mMaxCollapsedLines;
    private int mAnimationDuration;
    private float mAnimAlphaStart;

    //是否正在执行动画
    private boolean mAnimating;
    //是否需要重新计算宽高
    private boolean mRelayout;

    protected TextView mTv;
    protected TextView mExpandTv;


    //最大高度
    private int mTextHeightWithMaxLines;
    //默认是收起的
    private boolean mCollapsed = true;


    public ExpandableTextview(Context context) {
        this(context, null);
    }

    public ExpandableTextview(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        mMaxCollapsedLines = typedArray.getInt(R.styleable.ExpandableTextView_maxCollapsedLines, MAX_COLLAPSED_LINES);
        mAnimationDuration = typedArray.getInt(R.styleable.ExpandableTextView_animDuration, DEFAULT_ANIM_DURATION);
        mAnimAlphaStart = typedArray.getFloat(R.styleable.ExpandableTextView_animAlphaStart, DEFAULT_ANIM_ALPHA_START);
        typedArray.recycle();
        setOrientation(VERTICAL);
        setVisibility(GONE);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mAnimating;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViews();
    }


    private void findViews() {
        mTv = (TextView) findViewById(R.id.expandable_text);
        mTv.setOnClickListener(this);
        mExpandTv = (TextView) findViewById(R.id.expand_collapse);
        mExpandTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mRelayout || getVisibility() == GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        mRelayout = false;
        //有几种情况
        //1.没有展开按钮
        mExpandTv.setVisibility(GONE);
        mTv.setMaxLines(Integer.MAX_VALUE);
        //计算宽高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //如果行数小于等于最大行数,则return
        if (mTv.getLineCount() <= mMaxCollapsedLines) {
            return;
        }
        //以下是行数大于最大行数的
        //保存textview的高度
        mTextHeightWithMaxLines = getRealTextViewHeight(mTv);
       if (mCollapsed){//如果是收起的
           mTv.setMaxLines(mMaxCollapsedLines);
       }
       mExpandTv.setVisibility(VISIBLE);
    }

    /**
     * 获取textview高度
     *
     * @param textView
     * @return
     */
    private static int getRealTextViewHeight(@NonNull TextView textView) {
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
        return textHeight + padding;
    }
}
