package com.xinze.xinze.module.main.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vondear.rxtools.view.RxToast;
import com.xinze.xinze.App;
import com.xinze.xinze.R;
import com.xinze.xinze.base.BaseFragment;
import com.xinze.xinze.module.main.adapter.OrderRecycleViewAdapter;
import com.xinze.xinze.module.main.modle.OrderItem;
import com.xinze.xinze.module.main.presenter.OrderPresenterImp;
import com.xinze.xinze.module.main.view.IOrderView;
import com.xinze.xinze.module.order.OrderDetailActivity;
import com.xinze.xinze.widget.SimpleToolbar;

import java.util.List;

import butterknife.BindView;

/**
 * 首页
 *
 * @author lxf
 * Created by lxf on 2016/5/15.
 */
public class OrderFragment extends BaseFragment implements IOrderView {

    @BindView(R.id.order_rv)
    RecyclerView orderRv;
    @BindView(R.id.order_tool_bar)
    SimpleToolbar orderToolBar;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.order_search_bar)
    RelativeLayout orderSearchBar;
    @BindView(R.id.order_srl)
    SmartRefreshLayout mOrderSmartRefresh;
    private OrderPresenterImp opi;
    private int pageNo = 1;
    private SmartRefreshLayout layout;
    private OrderRecycleViewAdapter orva;
    private List<OrderItem> data;

    /**
     * RecycleView条目被点击的位置
     */
    private int mPosition = 0;
    private LinearLayoutManager layoutManager;

    @Override
    public void onResume() {
        super.onResume();
        if (App.mUser.isLogin()){
            opi = new OrderPresenterImp(this, mActivity);
            opi.getOderList(1, 10);
        }
    }

    @Override
    protected int initLayout() {
        return R.layout.main_fragment_order;
    }

    @Override
    protected void initView() {
        orderToolBar.setMainTitle(R.string.orderList);
        layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        orderRv.setLayoutManager(layoutManager);
        orva = new OrderRecycleViewAdapter(mActivity);
        orderRv.setAdapter(orva);
        orva.setOnItemClickListener(new OrderRecycleViewAdapter.OnRecyclerViewItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                mPosition = position;
                OrderItem orderItem = data.get(position);
                String orderId = orderItem.getOrderid();
                openActivity(OrderDetailActivity.class, "orderId", orderId);
            }
        });
        mOrderSmartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                if (App.mUser.isLogin()) {
                    opi.getOderList(pageNo, 10);
                } else {
                    refreshLayout.finishRefresh();
                }
            }
        });
        mOrderSmartRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNo++;
                if (App.mUser.isLogin()) {
                    opi.getOderList(pageNo, 10 );
                } else {
                    refreshLayout.finishLoadMore();
                }
            }
        });
        layout = mOrderSmartRefresh.getLayout();

    }

    public static OrderFragment newInstance() {
        return new OrderFragment();
    }


    public void setData(final List<OrderItem> data) {
        if (data != null && data.size()>0){
            if (pageNo == 1){
                this.data = data;
            }else{
                this.data.addAll(data);
            }
            orva.setData(this.data);
        }
    }

    @Override
    public void getOrderListSuccess() {
        if (pageNo == 1) {
            layout.finishRefresh(2000);
        } else {
            layout.finishLoadMore(2000);
        }
        moveToPosition(layoutManager,orderRv,mPosition);
    }

    @Override
    public void getOrderListFailed() {
        layout.finishRefresh(false);
    }

    @Override
    public void shotToast(String msg) {
        RxToast.showToast(msg);
    }

    public void refresh() {
        if (!App.mUser.isLogin() && orva != null) {
            orva.clearData();
        } else {
            opi = new OrderPresenterImp(this, mActivity);
            opi.getOderList(1, 10);
        }
    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager   设置RecyclerView对应的manager
     * @param mRecyclerView  当前的RecyclerView
     * @param n  要跳转的位置
     */
    public  void moveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {
        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(n);
        }
    }


}
