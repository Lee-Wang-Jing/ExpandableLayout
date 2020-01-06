package com.wangjing.expandablelayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 作者：Created by WangJing on 2017/7/17.
 * 邮箱：wangjinggm@gmail.com
 * 描述：展开收起的Layout
 * 最近修改：2017/7/17 15:26 by WangJing
 */

public class ExpandableTextview extends LinearLayout implements View.OnClickListener {

    //默认的收起状态显示最多的行数
    private static final int MAX_COLLAPSED_LINES = 8;
    //默认的展开收起动画时间
    private static final int DEFAULT_ANIM_DURATION = 300;
    //动画开始时默认的alpha值
    private static final float DEFAULT_ANIM_ALPHA_START = 0.7f;
    private static final String DEFAULT_COLLAPSEDTEXT = "收起";
    private static final String DEFAULT_EXPANDTEXT = "展开";

    private int mMaxCollapsedLines;
    private int mAnimationDuration;
    private float mAnimAlphaStart;
    private String collapsedText;
    private String expandText;

    //是否正在执行动画
    private boolean mAnimating;
    //是否需要重新计算宽高
    private boolean mRelayout;

    protected TextView mTv;
    protected TextView mExpandTv;


    //是否显示展开收起按钮
    private boolean isShowCollapsed = true;

    //最大高度
    private int mTextHeightWithMaxLines;
    //默认是收起的
    private boolean mCollapsed = true;
    //内容文字区域点击是否可以展开收起，默认false
    private boolean contentClick = false;
    //收起状态的高度
    private int mCollapsedHeight;

    private int mMarginBetweenTxtAndBottom;

    /* 在ListView中使用的时候需要保存每个position的状态 */
    private SparseBooleanArray mCollapsedStatus;
    private int mPosition;

    /* Listener for callback */
    private OnExpandStateChangeListener mListener;

    public interface OnExpandStateChangeListener {
        /**
         * Called when the expand/collapse animation has been finished
         *
         * @param textView   - TextView being expanded/collapsed
         * @param isExpanded - true if the TextView has been expanded
         */
        void onExpandStateChanged(TextView textView, boolean isExpanded);
    }

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
        collapsedText = typedArray.getString(R.styleable.ExpandableTextView_collapsedText);
        expandText = typedArray.getString(R.styleable.ExpandableTextView_expandText);
        contentClick = typedArray.getBoolean(R.styleable.ExpandableTextView_contentClick, false);
        mCollapsed = typedArray.getBoolean(R.styleable.ExpandableTextView_contentClick, true);
        typedArray.recycle();
        if (TextUtils.isEmpty(collapsedText)) {
            collapsedText = DEFAULT_COLLAPSEDTEXT;
        }
        if (TextUtils.isEmpty(expandText)) {
            expandText = DEFAULT_EXPANDTEXT;
        }
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


    public boolean isShowCollapsed() {
        return isShowCollapsed;
    }

    public void setShowCollapsed(boolean showCollapsed) {
        isShowCollapsed = showCollapsed;
    }

    private void findViews() {
        mTv = findViewById(R.id.expandable_text);
//        mTv.setOnClickListener(this);
        mExpandTv = findViewById(R.id.expand_collapse);
        mExpandTv.setText(mCollapsed ? expandText : collapsedText);
        mExpandTv.setOnClickListener(this);
        if (contentClick) {
            mTv.setOnClickListener(this);
        }
    }

    private int getTargetHeight() {
        if (mCollapsed) {
            return mCollapsedHeight;
        } else {
            return getHeight() +
                    mTextHeightWithMaxLines - mTv.getHeight();
        }
    }


    @Override
    public void onClick(View view) {
        if (mExpandTv.getVisibility() != VISIBLE) {
            return;
        }
        //设置展开状态
        mCollapsed = !mCollapsed;
        mExpandTv.setText(mCollapsed ? expandText : collapsedText);

        //在Listview中使用的时候需要保存状态
        if (mCollapsedStatus != null) {
            mCollapsedStatus.put(mPosition, mCollapsed);
        }
        //标记动画正在进行
        mAnimating = true;
        Animation animation;
        animation = new ExpandCollapseAnimation(this, getHeight(), getTargetHeight());
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                applyAlphaAnimation(mTv, mAnimAlphaStart);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // clear animation here to avoid repeated applyTransformation() calls
                clearAnimation();
                // clear the animation flag
                mAnimating = false;

                // notify the listener
                if (mListener != null) {
                    mListener.onExpandStateChanged(mTv, !mCollapsed);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        clearAnimation();
        startAnimation(animation);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mRelayout || getVisibility() == GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        mRelayout = false;
        if (!isShowCollapsed) {
            //1.没有展开按钮
            mExpandTv.setVisibility(GONE);
            mTv.setMaxLines(Integer.MAX_VALUE);
            //计算宽高
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            //1.没有展开按钮
            mExpandTv.setVisibility(GONE);
            mTv.setMaxLines(Integer.MAX_VALUE);
            //计算宽高
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            //如果行数小于等于收起状态显示最大行数,则return
            if (mTv.getLineCount() <= mMaxCollapsedLines) {
                return;
            }
            //以下是行数大于最大行数的
            //保存textview的高度
            mTextHeightWithMaxLines = getRealTextViewHeight(mTv);
            if (mCollapsed) {//如果是收起的
                mTv.setMaxLines(mMaxCollapsedLines);
            }
            mExpandTv.setVisibility(View.VISIBLE);
            //计算宽高
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (mCollapsed) {
                //获取Textview距离视图底部的高度
                mTv.post(new Runnable() {
                    @Override
                    public void run() {
                        mMarginBetweenTxtAndBottom = getHeight() - mTv.getHeight();
                    }
                });
                // 保存收起状态的高度
                mCollapsedHeight = getMeasuredHeight();
            }
        }
    }

    @Nullable
    public CharSequence getText() {
        if (mTv == null) {
            return "";
        }
        return mTv.getText();
    }

    public void setText(@Nullable CharSequence text) {
        mRelayout = true;
        mTv.setText(text);
        setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    public void setText(@Nullable CharSequence text, @NonNull SparseBooleanArray collapsedStatus, int position) {
        mCollapsedStatus = collapsedStatus;
        mPosition = position;
        boolean isCollapsed = collapsedStatus.get(position, true);
        clearAnimation();
        mCollapsed = isCollapsed;
        mExpandTv.setText(mCollapsed ? expandText : collapsedText);
        setText(text);
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        requestLayout();
    }

    public void setOnExpandStateChangeListener(@Nullable OnExpandStateChangeListener listener) {
        mListener = listener;
    }

    public TextView getmTv() {
        return mTv;
    }

    public TextView getmExpandTv() {
        return mExpandTv;
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


    class ExpandCollapseAnimation extends Animation {
        private final View mTargetView;
        private final int mStartHeight;
        private final int mEndHeight;

        public ExpandCollapseAnimation(View view, int startHeight, int endHeight) {
            mTargetView = view;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            setDuration(mAnimationDuration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final int newHeight = (int) ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight);
            mTv.setMaxHeight(newHeight - mMarginBetweenTxtAndBottom);
            if (Float.compare(mAnimAlphaStart, 1.0f) != 0) {
                applyAlphaAnimation(mTv, mAnimAlphaStart + interpolatedTime * (1.0f - mAnimAlphaStart));
            }
            mTargetView.getLayoutParams().height = newHeight;
            mTargetView.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void applyAlphaAnimation(View view, float alpha) {
        if (isPostHoneycomb()) {
            view.setAlpha(alpha);
        } else {
            AlphaAnimation alphaAnimation = new AlphaAnimation(alpha, alpha);
            // make it instant
            alphaAnimation.setDuration(0);
            alphaAnimation.setFillAfter(true);
            view.startAnimation(alphaAnimation);
        }
    }

    private static boolean isPostHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnimating = false;
        clearAnimation();
        getLayoutParams().height = getTargetHeight();
        requestLayout();
    }
}
