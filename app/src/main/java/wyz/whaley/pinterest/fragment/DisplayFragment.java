package wyz.whaley.pinterest.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wyz.whaley.pinterest.R;
import wyz.whaley.pinterest.data.LikeShotInfo;
import wyz.whaley.pinterest.data.ShotInfo;
import wyz.whaley.pinterest.db.table.AKBaseTable;
import wyz.whaley.pinterest.db.table.FollowingTable;
import wyz.whaley.pinterest.http.AKHttpRequestEngine;
import wyz.whaley.pinterest.http.AKHttpTask;
import wyz.whaley.pinterest.http.utils.AKCacheTask;
import wyz.whaley.pinterest.http.utils.AKImageLoader;
import wyz.whaley.pinterest.http.AKRequest;
import wyz.whaley.pinterest.http.AKResponseListener;
import wyz.whaley.pinterest.http.utils.AKGifRequest;
import wyz.whaley.pinterest.http.utils.AKHttpConstant;
import wyz.whaley.pinterest.http.utils.AKImageRequest;
import wyz.whaley.pinterest.http.utils.AKJSONRequestNew;
import wyz.whaley.pinterest.impl.OnLoadMoreListener;
import wyz.whaley.pinterest.impl.OnRecyclerViewItemClickListener;
import wyz.whaley.pinterest.maneger.ActivityManeger;
import wyz.whaley.pinterest.utils.BitmapUtils;
import wyz.whaley.pinterest.utils.DribbbleParamsUtils;
import wyz.whaley.pinterest.widget.AKGifImageView;
import wyz.whaley.pinterest.widget.RecyclerViewItemDivider;

/**
 * Created by alwayking on 16/3/9.
 */
public class DisplayFragment extends Fragment {

    private static final String TAG = "DisplayFragment";

    private RecyclerView mShotsRecyclerView;

    private ShotsAdapter mShotsAdapter;

    private GridLayoutManager mLayoutManager;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<ShotInfo> mShotsList = new LinkedList<>();

    private List<LikeShotInfo> mLikesShotsList = new LinkedList<>();

    private String mUrl;

    private Handler mHandler;

    private int mCurrentPage = 1;

    private AKRequest mRquest;

    private int mPosition;

    public DisplayFragment() {
        mHandler = new Handler();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setUrl(String url, int pos) {
        mPosition = pos;
        mUrl = url;
    }

    public void setUrlRefresh(String url, int pos) {
        if (!url.equals(mUrl)) {
            mUrl = url;
            mPosition = pos;
            if (mRquest != null) {
                mRquest.setIsCanceled(true);
            }
            mShotsList.clear();
            mLikesShotsList.clear();
            mShotsAdapter.notifyDataSetChanged();
            mCurrentPage = 1;
            mShotsAdapter.setLoaded();
            requestMainData();

            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AKCacheTask.getInstace().add(new Runnable() {
                    @Override
                    public void run() {
                        final long time = System.currentTimeMillis();
                        final List<ShotInfo> list = FollowingTable.getInstance(getContext()).query(null, new AKBaseTable.DBCallBack() {
                            @Override
                            public void queryComplete(List<ShotInfo> list) {

                            }
                        });

                        if (list != null && list.size() > 0) {

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mShotsAdapter.isLoading) {
                                        int index = mShotsList.size() - 1;
                                        mShotsList.remove(index);
                                        mShotsList.addAll(list);
                                        mShotsAdapter.setLoaded();
                                        mShotsAdapter.notifyItemRemoved(index);
                                        mShotsAdapter.notifyItemRangeInserted(index, list.size() - 1);
                                    } else if (mSwipeRefreshLayout.isRefreshing()) {
                                        if (mPosition < 3) {
                                            mShotsList = list;
                                        } else {
//                                    mLikesShotsList = newLikeList;
                                        }
                                        mSwipeRefreshLayout.setRefreshing(false);
                                        mShotsAdapter.notifyDataSetChanged();
                                        mShotsAdapter.notifyDataSetChanged();
                                    }
                                }
                            });

                        }

                        Log.i(TAG, "query time = " + (System.currentTimeMillis() - time));
                    }
                });
            }
        }, 100);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.base_shots_fragment, container, false);
        mShotsRecyclerView = (RecyclerView) view.findViewById(R.id.shots_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_refresh_colors));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mShotsAdapter.isLoading) {
                    mCurrentPage = 1;
                    requestMainData();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        mShotsAdapter = new ShotsAdapter();
        mLayoutManager = new GridLayoutManager(getContext(), mShotsAdapter.mColumn);
        mShotsRecyclerView.setHasFixedSize(true);

        mShotsRecyclerView.setLayoutManager(mLayoutManager);
        mShotsRecyclerView.addItemDecoration(new RecyclerViewItemDivider(getContext()));
        mShotsRecyclerView.setAdapter(mShotsAdapter);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int span;
                switch (mShotsAdapter.getItemViewType(position)) {
                    case ShotsAdapter.VIEW_TYPE_LOADING:
                        span = mShotsAdapter.mColumn;
                        break;
                    case ShotsAdapter.VIEW_TYPE_ITEM:
                        span = 1;
                        break;
                    default:
                        span = -1;
                        break;
                }
                return span;
            }
        });
        mShotsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e(TAG, "Load More");
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mShotsList.add(null);
                    mShotsAdapter.notifyItemInserted(mShotsList.size() - 1);
                    mCurrentPage++;
                    requestMainData();
                }
            }
        });

        mShotsAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, ShotInfo shotInfo) {
                Intent intent = new Intent();
                intent.putExtra(ActivityManeger.INTENT_SHOT, shotInfo);
                ActivityManeger.jump(DisplayFragment.this.getContext(), ActivityManeger.DETAIL, intent);
            }
        });

        return view;
    }

    private void requestMainData() {
        AKRequest request = new AKJSONRequestNew(mUrl, new AKResponseListener() {
            @Override
            public void onSuccess(final Object in) {
                Gson gson = new GsonBuilder().create();
                String string = (String) in;
                try {
                    Log.i(TAG, string);
                    List<ShotInfo> list = null;
                    List<LikeShotInfo> likeList = null;
                    if (mPosition < 3) {
                        list = gson.fromJson(string, new TypeToken<List<ShotInfo>>() {
                        }.getType());
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            FollowingTable.getInstance(getContext()).clear();
                            FollowingTable.getInstance(getContext()).insertList(list);
                        }
                    } else {
                        likeList = gson.fromJson(string, new TypeToken<List<LikeShotInfo>>() {
                        }.getType());
                    }
                    Log.i(TAG, "mShotsList.size()" + mShotsList.size());
                    final List<ShotInfo> newList = list;
                    final List<LikeShotInfo> newLikeList = likeList;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mShotsAdapter.isLoading) {
                                int index = mShotsList.size() - 1;
                                mShotsList.remove(index);
                                mShotsList.addAll(newList);
                                mShotsAdapter.setLoaded();
                                mShotsAdapter.notifyItemRemoved(index);
                                mShotsAdapter.notifyItemRangeInserted(index, newList.size() - 1);
                            } else if (mSwipeRefreshLayout.isRefreshing()) {
                                if (mPosition < 3) {
                                    mShotsList = newList;
                                } else {
                                    mLikesShotsList = newLikeList;
                                }
                                mSwipeRefreshLayout.setRefreshing(false);
                                mShotsAdapter.notifyDataSetChanged();
                                mShotsAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                } catch (JsonSyntaxException e) {
                }
            }

            @Override
            public void onFailed(Exception e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "http exception", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestMainData();
                    }
                }, 2000);
            }
        }, AKHttpRequestEngine.REQUEST_GET, getContext());
        request.setParams(AKHttpConstant.getPageParam(mCurrentPage));
//        AKHttpTask.getInstace().addToRequestQueue(request);
        AKHttpTask.getInstace().add(request);
        mRquest = request;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestMainData();
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }, 100);
    }


    private class ShotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
        public static final int VIEW_TYPE_ITEM = 0;
        public static final int VIEW_TYPE_LOADING = 1;

        private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

        private boolean isLoading;
        private int visibleThreshold = 10;
        private int lastVisibleItem, totalItemCount;

        private final int mColumn = 2;

        private BitmapDrawable mDefaultBitmap;

        private Bitmap mAvartBitmap;


        private int mScrollMode = RecyclerView.SCROLL_STATE_IDLE;


        private OnLoadMoreListener mOnLoadMoreListener;

        public ShotsAdapter() {
            mDefaultBitmap = new BitmapDrawable(getResources(), BitmapUtils.createColorBitmap(getContext(), 400, 300,
                    getResources().getColor(R.color.lightgray)));
            mAvartBitmap = BitmapUtils.createColorBitmap(getContext(), 100, 100,
                    getResources().getColor(R.color.recyclerview_bg));
            mShotsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = mLayoutManager.getItemCount();
                    lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && mShotsList.size() > 0 && !mSwipeRefreshLayout.isRefreshing()) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.i(TAG, "newState = " + newState + "   preState = " + mScrollMode);
                    if ((mScrollMode == RecyclerView.SCROLL_STATE_SETTLING && newState != RecyclerView.SCROLL_STATE_SETTLING)) {
                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            if (recyclerView.getChildAt(i) != null) {
                                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                                if (viewHolder instanceof ItemViewHolder) {


                                    final ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                                    if (itemViewHolder.mShotGIV.getTag() instanceof AKImageRequest) {
                                        itemViewHolder.mShotGIV.setMovie(null);
                                    } else if (itemViewHolder.mShotGIV.getTag() instanceof AKGifRequest) {

                                    } else {
                                        itemViewHolder.mShotGIV.setMovie(null);
                                        final String preUrl = (String) itemViewHolder.mShotGIV.getTag();
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (itemViewHolder.mShotGIV.getTag().equals(preUrl)) {
                                                    AKImageLoader.getInstance().displayImage(DisplayFragment.this.getActivity(),
                                                            itemViewHolder.mShotGIV, itemViewHolder.mShotGIV.getTag().toString(), mScrollMode == RecyclerView.SCROLL_STATE_SETTLING, 400);
                                                }
                                            }
                                        }, 100);
                                    }


                                    if (itemViewHolder.mAvatarCV.getTag() instanceof AKImageRequest) {
                                    } else {
                                        final String preUrl = (String) itemViewHolder.mAvatarCV.getTag();

                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (itemViewHolder.mAvatarCV.getTag().equals(preUrl)) {
                                                    AKImageLoader.getInstance().displayAvatar(DisplayFragment.this.getActivity(), itemViewHolder.mAvatarCV,
                                                            itemViewHolder.mAvatarCV.getTag().toString(), mScrollMode == RecyclerView.SCROLL_STATE_SETTLING);
                                                }
                                            }
                                        }, 100);
                                    }

                                }
                            }
                        }

                    }
                    mScrollMode = newState;
                }
            });
        }

        public void setOnLoadMoreListener(OnLoadMoreListener listener) {
            this.mOnLoadMoreListener = listener;
        }

        public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
            this.mOnRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
        }

        @Override
        public void onClick(View v) {
            if (mOnRecyclerViewItemClickListener != null) {
                ShotInfo shotInfo = (ShotInfo) v.getTag();
                mOnRecyclerViewItemClickListener.onItemClick(v, shotInfo);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shot_item, parent, false);
                v.setOnClickListener(this);
                return new ItemViewHolder(v);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_item, parent, false);
                return new LoadMoreViewHold(v);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemViewHolder) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                if (itemViewHolder.mShotGIV.getTag() instanceof AKImageRequest) {
                    Log.i(TAG, "setIsCanceled(true)");
                    ((AKImageRequest) itemViewHolder.mShotGIV.getTag()).setIsCanceled(true);

                }
//                else if (itemViewHolder.mShotGIV.getTag() instanceof GifRequest) {
//                    Log.i(TAG, "setIsCanceled(true)");
//                    ((GifRequest) itemViewHolder.mShotGIV.getTag()).setIsCanceled(true);
//                    ((GifRequest) itemViewHolder.mShotGIV.getTag()).finishSelf();
//                }

                if (itemViewHolder.mAvatarCV.getTag() instanceof AKImageRequest) {
                    ((AKImageRequest) itemViewHolder.mAvatarCV.getTag()).setIsCanceled(true);
                }
                ShotInfo shotInfo;
                if (mPosition < 3) {
                    shotInfo = mShotsList.get(position);
                } else {
                    shotInfo = mLikesShotsList.get(position).getShot();
                }

                ItemViewHolder h = (ItemViewHolder) holder;
                String shotUrl = DribbbleParamsUtils.getSmallImage(shotInfo.getImages());

                if (h.mShotGIV.getLayerType() == View.LAYER_TYPE_SOFTWARE) {
                    h.mShotGIV.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                }
                h.itemView.setTag(shotInfo);
                h.mUserNameTV.setText(shotInfo.getUser().getName());
                h.mShotTitle.setText(shotInfo.getTitle());
                h.mShotGIV.setImageDrawable(mDefaultBitmap);

                h.mViewCountTV.setText(String.valueOf(shotInfo.getViews_count()));
                h.mCommentCountTV.setText(String.valueOf(shotInfo.getComments_count()));
                h.mFavoriteCountTV.setText(String.valueOf(shotInfo.getLikes_count()));

                h.mAvatarCV.setImageBitmap(mAvartBitmap);

                if (shotInfo.isAnimated()) {
                    h.mGifCV.setVisibility(View.VISIBLE);
                } else {
                    h.mGifCV.setVisibility(View.GONE);
                }

                h.mShotGIV.setMovie(null);
                AKImageLoader.getInstance().displayImage(DisplayFragment.this.getActivity(),
                        h.mShotGIV, shotUrl, mScrollMode == RecyclerView.SCROLL_STATE_SETTLING, 400);
                AKImageLoader.getInstance().displayAvatar(DisplayFragment.this.getActivity(), h.mAvatarCV,
                        shotInfo.getUser().getAvatar_url(), mScrollMode == RecyclerView.SCROLL_STATE_SETTLING);
                Log.i(TAG, "holder.mUserNameTV.setText(mShotsList) pos = " + position);
            } else if (holder instanceof LoadMoreViewHold) {
                LoadMoreViewHold h = (LoadMoreViewHold) holder;
                h.mProgressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mPosition < 3) {
                return mShotsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
            } else {
                return mLikesShotsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
            }
        }

        @Override
        public int getItemCount() {
            if (mPosition < 3) {
                return mShotsList.size();
            } else {
                return mLikesShotsList.size();
            }
        }

        public void setLoaded() {
            isLoading = false;
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public TextView mUserNameTV;
            public TextView mShotTitle;
            public AKGifImageView mShotGIV;
            public CircleImageView mAvatarCV;
            public CardView mGifCV;

            public TextView mViewCountTV;
            public TextView mCommentCountTV;
            public TextView mFavoriteCountTV;

            public ItemViewHolder(View itemView) {
                super(itemView);
                mUserNameTV = (TextView) itemView.findViewById(R.id.shot_item_username_tv);
                mShotTitle = (TextView) itemView.findViewById(R.id.shot_item_title_tv);
                mShotGIV = (AKGifImageView) itemView.findViewById(R.id.item_giv);
                mAvatarCV = (CircleImageView) itemView.findViewById(R.id.shot_avatar_civ);
                mGifCV = (CardView) itemView.findViewById(R.id.item_gif_cv);
                mGifCV.setTag(mShotGIV);
                mGifCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        GifImageView view = (GifImageView) mGifCV.getTag();
//                        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                        view.startGif();
//                        v.setVisibility(View.GONE);
                    }
                });

                mViewCountTV = (TextView) itemView.findViewById(R.id.shot_view_count_tv);
                mCommentCountTV = (TextView) itemView.findViewById(R.id.shot_comment_count_tv);
                mFavoriteCountTV = (TextView) itemView.findViewById(R.id.shot_favorite_count_tv);
            }
        }

        public class LoadMoreViewHold extends RecyclerView.ViewHolder {
            public ProgressBar mProgressBar;

            public LoadMoreViewHold(View itemView) {
                super(itemView);
                mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            }
        }

    }
}
