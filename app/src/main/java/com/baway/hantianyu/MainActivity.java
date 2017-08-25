package com.baway.hantianyu;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 类描述：
 * 创建人：韩天宇
 * 班级：移动通信1504D
 * 创建时间：2017/7/28 15:44
 */
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private int page=1;
    private static final String TAG = "MainActivity";
    private List<NewsBean.DataBean> list;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView= (RecyclerView) findViewById(R.id.recycler);
        //判断网络链接
        if(!IsNet.isNetworkConnected(this)){
            Toast.makeText(this,"请开启网络",Toast.LENGTH_SHORT).show();
        }else {
            list=new ArrayList<>();
            adapter = new Adapter();

            //设置recyclerView管理者
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter.setMyItemOnclick(new MyItemOnclick() {
                @Override
                public void onItemClick(View view, int postion) {
                    Toast.makeText(MainActivity.this,list.get(postion).getIntroduction(),Toast.LENGTH_SHORT).show();
                }
            });
            //recyclerView滚动监听  到底部自动加载
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int totalItemCount = recyclerView.getAdapter().getItemCount();
                    int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                    int visibleItemCount = recyclerView.getChildCount();
                    //重新调取网络获取
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
                        page=page+1;
                        getData();

                    }


                }
            });
//设置适配器
            recyclerView.setAdapter(adapter);
            //网络获取
            getData();
        }

    }

//网络请求 解析数据
    private void getData() {
        //okhttpGET请求
        OkHttpClient okHttpClient=new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url("http://www.yulin520.com/a2a/impressApi/news/mergeList?sign=C7548DE604BCB8A17592EFB9006F9265&pageSize=20&gender=2&ts=1871746850&page="+page);
        Request request = requestBuilder.build();
        Call call= okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Logger.d(string);
                Gson gson=new Gson();
                //解析数据
                NewsBean newsBean = gson.fromJson(string, NewsBean.class);
                List<NewsBean.DataBean> data = newsBean.getData();
                list.addAll(data);
                Logger.d(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //刷新适配器
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });

    }
    //RecyclerView的适配器
class Adapter extends RecyclerView.Adapter<ViewHolder>implements View.OnClickListener{
    private MyItemOnclick myItemOnclick;

    public void setMyItemOnclick(MyItemOnclick myItemOnclick) {
        this.myItemOnclick = myItemOnclick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(MainActivity.this,R.layout.item,null);
        view.setOnClickListener(this);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }
//绑定视图
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //绑定控件
        holder.itemView.setTag(position);
        holder.userName.setText(list.get(position).getUserName());
        holder.userAge.setText(list.get(position).getUserAge()+"");
        holder.occupation.setText(list.get(position).getOccupation());
        holder.introduction.setText(list.get(position).getIntroduction());
        Glide.with(MainActivity.this).load(list.get(position).getUserImg()).into(holder.image);
        addInAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        if(myItemOnclick!=null){
            myItemOnclick.onItemClick(v, (Integer) v.getTag());
        }
    }
}
    /**
     *添加动画效果
     */ private void addInAnimation(View view) {

        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        alpha.setDuration(2000);
        alpha.start();

    }

class ViewHolder extends RecyclerView.ViewHolder{
    private TextView userName,userAge,occupation,introduction;
    private ImageView image;
    public ViewHolder(View itemView) {
        super(itemView);
        userName= (TextView) itemView.findViewById(R.id.username);
        userAge= (TextView) itemView.findViewById(R.id.userage);
        occupation= (TextView) itemView.findViewById(R.id.occupation);
        introduction= (TextView) itemView.findViewById(R.id.introduction);
       image= (ImageView) itemView.findViewById(R.id.imageview);

    }
}
}

