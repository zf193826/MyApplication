package lianxi.com.zf20171017lianxi.Activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lianxi.com.zf20171017lianxi.Activity.View.XListView;
import lianxi.com.zf20171017lianxi.Activity.adapter.ListViewAdapter;
import lianxi.com.zf20171017lianxi.Activity.bean.DataDataBean;
import lianxi.com.zf20171017lianxi.Activity.inter.JsonStringCallaBack;
import lianxi.com.zf20171017lianxi.Activity.util.NetDataUtil;
import lianxi.com.zf20171017lianxi.R;

/**
 * Created by 张峰 on 2017/10/17.
 */

public class Fragment_TuiJian extends Fragment implements XListView.IXListViewListener {

    private XListView xListView;
    private List<DataDataBean.ResultsBean> list = new ArrayList<>();
    private int page_num = 1;
    private ListViewAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tuijian_layout, container, false);
        //找控件
        xListView = (XListView) view.findViewById(R.id.x_list_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
        xListView.setXListViewListener(this);

        //获取数据....解析的方法
        getDataFromNet();
    }

    private void getDataFromNet() {
        //在这里解析获取的数据
        NetDataUtil.getData(getActivity(),"http://gank.io/api/data/Android/10/"+page_num,new JsonStringCallaBack(){
                    @Override
                    public void getJsonString(String json) {
                        Gson gson = new Gson();
                        DataDataBean dataDataBean = gson.fromJson(json, DataDataBean.class);
                        list.addAll(dataDataBean.getResults());
                        //设置适配器
                        setAdapter();


                    }
                }

        );
    }

    private void setAdapter() {
        if (myAdapter == null){
            myAdapter = new ListViewAdapter(getActivity(), list);
            xListView.setAdapter(myAdapter);
        }else {
            myAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        getDataRefresh();
        
        
    }

    @Override
    public void onLoadMore() {
        page_num ++;
        getDataFromNet();

    }


    public void getDataRefresh() {
        //当列表下拉刷新时，接口的页数设置为1，清除原来的列表数据，请求网络

        NetDataUtil.getData(getActivity(), "http://gank.io/api/data/Android/10/1", new JsonStringCallaBack() {
            @Override
            public void getJsonString(String json) {
                //在这里解析
                Gson gson = new Gson();

                DataDataBean dataDataBean = gson.fromJson(json, DataDataBean.class);
                //先清除所有
                list.clear();
                //再添加
                list.addAll(0,dataDataBean.getResults());

                //设置适配器
                setAdapter();

                //停止刷新
                xListView.stopRefresh();
                //设置刷新时间
                Date data = new Date();
                String format = new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()));
                xListView.setRefreshTime(format);
            }
        });

    }
}
