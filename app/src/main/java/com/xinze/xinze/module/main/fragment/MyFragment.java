package com.xinze.xinze.module.main.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xinze.xinze.App;
import com.xinze.xinze.R;
import com.xinze.xinze.base.BaseFragment;
import com.xinze.xinze.config.AppConfig;
import com.xinze.xinze.http.config.HttpConfig;
import com.xinze.xinze.module.about.view.AboutUsActivity;
import com.xinze.xinze.module.certification.view.CertificationActivity2;
import com.xinze.xinze.module.change.view.ChangePassWordActivity;
import com.xinze.xinze.module.drivers.view.MyDriverActivity;
import com.xinze.xinze.module.invite.view.InviteActivity;
import com.xinze.xinze.module.line.view.LineActivity;
import com.xinze.xinze.module.login.view.LoginActivity;
import com.xinze.xinze.module.main.adapter.MyRecycleViewAdapter;
import com.xinze.xinze.module.main.bean.MyRecycleViewItem;
import com.xinze.xinze.module.main.constant.MyItemSelected;
import com.xinze.xinze.module.main.presenter.MyPresenterImp;
import com.xinze.xinze.module.main.view.IMyView;
import com.xinze.xinze.module.message.view.SystemMsgActivity;
import com.xinze.xinze.module.register.view.RegisterActivity;
import com.xinze.xinze.module.trucks.view.MyTruckActivity;
import com.xinze.xinze.utils.DialogUtil;
import com.xinze.xinze.utils.MessageEvent;
import com.xinze.xinze.widget.SimpleToolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 首页
 *
 * @author lxf
 * Created by lxf on 2016/5/15.
 */
public class MyFragment extends BaseFragment implements View.OnClickListener, IMyView {

    /**
     * 布局类型
     */
    private static final int ITEM_TYPE_ONE = 1;
    private static final int ITEM_TYPE_ZERO = 2;

    @BindView(R.id.my_register)
    Button myRegister;
    @BindView(R.id.my_unLogin)
    TextView myUnLogin;
    @BindView(R.id.my_login)
    Button myLogin;
    @BindView(R.id.profile_image)
    CircleImageView portrait;
    @BindView(R.id.my_rv)
    RecyclerView myRv;
    @BindView(R.id.my_tool_bar)
    SimpleToolbar myToolBar;


    private ArrayList<MyRecycleViewItem> myRecycleViewItems = new ArrayList<>();
    private MyRecycleViewAdapter mAdapter;
    private MyRecycleViewItem myDrivers;
    private MyRecycleViewItem myInvitation;
    private MyRecycleViewItem myChangePwd;
    private MyRecycleViewItem myLoginOut;
    private MyRecycleViewItem driverCer;
    private MyRecycleViewItem myCars;
    private MyRecycleViewItem myRoutes;
    private MyRecycleViewItem mySystemMsg;
    private MyRecycleViewItem myAboutUs;
    private MyRecycleViewItem myHelp;
    private MyPresenterImp mPresenter;

    @Override
    protected int initLayout() {
        return R.layout.main_fragment_my;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        initTitleBar();
        myRegister.setOnClickListener(this);
        myLogin.setOnClickListener(this);

        driverCer = new MyRecycleViewItem("司机认证", R.mipmap.certification, true, true, true, true, "未认证", true, ITEM_TYPE_ZERO);
        myDrivers = new MyRecycleViewItem("我的司机", R.mipmap.my_ic_driver, true, false, true, false, "0人", false, ITEM_TYPE_ZERO);
        myCars = new MyRecycleViewItem("我的车辆", R.mipmap.my_ic_cars, true, false, true, false, "0辆", true, ITEM_TYPE_ZERO);
        myRoutes = new MyRecycleViewItem("常跑路线", R.mipmap.my_ic_routes, true, false, false, true, "", true, ITEM_TYPE_ZERO);
        myInvitation = new MyRecycleViewItem("我的邀请", R.mipmap.my_ic_invitation, true, true, false, true, "", false, ITEM_TYPE_ZERO);
        myChangePwd = new MyRecycleViewItem("修改密码", R.mipmap.my_ic_change_pwd, false, false, false, false, "", false, ITEM_TYPE_ZERO);
        mySystemMsg = new MyRecycleViewItem("系统消息", R.mipmap.my_ic_sysmsg, true, true, true, true, "0条", true, ITEM_TYPE_ZERO);
        myAboutUs = new MyRecycleViewItem("关于我们", R.mipmap.my_ic_about_us, true, false, false, true, "", true, ITEM_TYPE_ZERO);
        myHelp = new MyRecycleViewItem("帮助", R.mipmap.my_ic_help, true, true, false, true, "", false, ITEM_TYPE_ZERO);
        myLoginOut = new MyRecycleViewItem("退出登录", 0, false, true, false, true, "", true, ITEM_TYPE_ONE);


        myRecycleViewItems.add(driverCer);
        myRecycleViewItems.add(myCars);
        myRecycleViewItems.add(myRoutes);
        myRecycleViewItems.add(mySystemMsg);
        myRecycleViewItems.add(myAboutUs);
        myRecycleViewItems.add(myHelp);
        myRecycleViewItems.add(myLoginOut);
        mAdapter = new MyRecycleViewAdapter(mActivity, myRecycleViewItems);
        myRv.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        myRv.setAdapter(mAdapter);
        myRv.setNestedScrollingEnabled(false);
        mAdapter.setOnItemClickListener(new MyRecycleViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String title = mAdapter.getTitle(position);
                switch (title) {
                    case "司机认证":
                        doType(MyItemSelected.DRIVER_CERTIFICATION);
                        break;
                    case "我的车辆":
                        doType(MyItemSelected.MY_CARS);
                        break;
                    case "常跑路线":
                        doType(MyItemSelected.MY_ROUTES);
                        break;
                    case "系统消息":
                        doType(MyItemSelected.MY_SYSTEM_MESSAGE);
                        break;
                    case "关于我们":
                        mActivity.startActivity(new Intent(mActivity, AboutUsActivity.class));
                        break;
                    case "退出登录":
                        mPresenter.loginOut();
                        break;
                    case "修改密码":
                        doType(MyItemSelected.MY_CHANGE_PWD);
                        break;
                    case "我的邀请":
                        doType(MyItemSelected.MY_INVITATION);
                        break;
                    case "我的司机":
                        doType(MyItemSelected.MY_DRIVERS);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void initTitleBar() {
        myToolBar.setMainTitle(getString(R.string.my));
        myToolBar.setLeftTitleGone();
        myToolBar.setTitleMarginTop();
    }

    /**
     * 判断有没有登录，没有登录显示未登录对话框，登录了跳转到不同界面
     *
     * @param type 不同界面的标识
     */
    private void doType(String type) {
        if (isLogin()) {
            doSomething(type);
        } else {
            DialogUtil.showUnloginDialog(mActivity);
        }
    }

    /**
     * 根据type跳转到不同界面
     *
     * @param type 不同界面的标识
     */
    private void doSomething(String type) {
        switch (type) {
            case MyItemSelected.DRIVER_CERTIFICATION:
                if (!"1".equals(App.mUser.getVertifyFlag())) {
                    openActivity(CertificationActivity2.class);
                }
//                else {
//                    DialogUtil.showUnIdentificationDialog(mActivity);
//                }
                break;
            case MyItemSelected.MY_CARS:
                if ("1".equals(App.mUser.getVertifyFlag())) {
                    openActivity(MyTruckActivity.class);
                } else {
                    DialogUtil.showUnIdentificationDialog(mActivity);
                }

                break;
            case MyItemSelected.MY_ROUTES:
                openActivity(LineActivity.class);
                break;
            case MyItemSelected.MY_SYSTEM_MESSAGE:
                openActivity(SystemMsgActivity.class);
                break;
            case MyItemSelected.MY_CHANGE_PWD:
                openActivity(ChangePassWordActivity.class);
                break;
            case MyItemSelected.MY_INVITATION:
                openActivity(InviteActivity.class);
                break;
            case MyItemSelected.MY_DRIVERS:
                if ("1".equals(App.mUser.getVertifyFlag())) {
                    openActivity(MyDriverActivity.class);
                } else {
                    DialogUtil.showUnIdentificationDialog(mActivity);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        myRv.setAdapter(mAdapter);

    }

    public void refreshPage() {
        if (mPresenter == null) {
            mPresenter = new MyPresenterImp(MyFragment.this, mActivity);
        }
        if (isLogin()) {
            mPresenter.getCount("");
            if (!myRecycleViewItems.contains(myDrivers) && !myRecycleViewItems.contains(myInvitation) && !myRecycleViewItems.contains(myChangePwd)) {
                myDrivers.setShowTopLine(true);
                myRecycleViewItems.add(1, myDrivers);
                myRecycleViewItems.add(4, myInvitation);
                myRecycleViewItems.add(5, myChangePwd);
            }
            Glide.with(this).load(HttpConfig.IMAGE_BASE_URL + App.mUser.getPhoto()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    portrait.setImageDrawable(resource);
                }
            });
            myChangePwd.setShowTopLine(true);
            myRoutes.setShowSpace(false);
            myRegister.setVisibility(View.GONE);
            myLogin.setVisibility(View.GONE);
            myUnLogin.setText(App.mUser.getLogin_name());
            driverCer.setRightText(App.mUser.getVertifyDescription());

        } else {
            if (myRecycleViewItems.contains(myDrivers) && myRecycleViewItems.contains(myInvitation) && myRecycleViewItems.contains(myChangePwd) && myRecycleViewItems.contains(myLoginOut)) {
                myRecycleViewItems.remove(myDrivers);
                myRecycleViewItems.remove(myInvitation);
                myRecycleViewItems.remove(myChangePwd);
            }
            myCars.setRightText("0辆");
            mySystemMsg.setRightText("0条");
            portrait.setImageResource(R.mipmap.my_ic_default);
            myRoutes.setShowSpace(true);
            mySystemMsg.setShowBottomLine(true);
            driverCer.setRightText("未认证");
            myRegister.setVisibility(View.VISIBLE);
            myLogin.setVisibility(View.VISIBLE);
            myUnLogin.setText(R.string.unLogin);
        }
        mAdapter.notifyDataSetChanged();
    }

    public static MyFragment newInstance() {
        return new MyFragment();
    }

    @Override
    protected void initData() {
        super.initData();


    }

    @Override
    @OnClick({R.id.my_register, R.id.my_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_register:
                openActivity(RegisterActivity.class);
                break;
            case R.id.my_login:
                openActivity(LoginActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    public void loginOutSuccess() {
        refreshPage();
        EventBus.getDefault().post(new MessageEvent(AppConfig.CLEAR_DATA));
        shotToast("注销成功");
    }

    @Override
    public void loginOutFailed() {
        shotToast("注销失败");
    }

    @Override
    public void getCountSuccess(String system) {

    }

    @Override
    public void getCountFailed(String system) {
        shotToast(system);
    }

    @Override
    public void refresh(String driverCount, String truckCount, String systemMsgCount) {

        myDrivers.setRightText(TextUtils.isEmpty(truckCount) ? "0人" : driverCount + "人");
        myCars.setRightText(TextUtils.isEmpty(driverCount) ? "0辆" : truckCount + "辆");
        mySystemMsg.setRightText(systemMsgCount + "条");
        myRecycleViewItems.set(1, myDrivers);
        myRecycleViewItems.set(2, myCars);
        myRecycleViewItems.set(6, mySystemMsg);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 判断当前用户是否登录
     *
     * @return true为登录状态  false为注销状态
     */
    public boolean isLogin() {

        return App.mUser != null && App.mUser.isLogin();

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void clear(MessageEvent messageEvent) {
        switch (messageEvent.getMessage()) {
            case AppConfig.UNLOGIN:
            case AppConfig.CLEAR_DATA:
                if (mAdapter != null) {
                    refreshPage();
                }
                break;
            case AppConfig.SYSTEM_MSG_READ:
                mPresenter.getCount("system");
                break;
            case AppConfig.UPDATE_COUNT:
                mPresenter.getCount("");
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
