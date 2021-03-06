package com.example.ui.adapter;

import java.util.HashSet;
import java.util.List;

import com.example.R;
import com.example.logic.ui.util.ViewHolder;
import com.example.logic.weibo.ui.picActivity;
import com.example.logic.weibo.ui.weiboContent;
import com.example.ui.HomeActivity;
import com.example.ui.Status;
import com.example.ui.logic.imaCache.Anseylodar;
import com.example.util.Utility;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeiboAdapter  extends  BaseAdapter{
	public List<Status> status;//微博信息
	
	public HashSet<Long> dictLike = new HashSet<Long>();
	
	//public ViewHolder vHolder;
     Context mContext;//上下文
     Anseylodar ansylodar;
     
	 public WeiboAdapter(Context context,List<Status> allstatus){
		 status=allstatus;
		 mContext=context;
		 ansylodar=new Anseylodar();
	 }
	@Override
	public int getCount() {
		//由于我们最后一项时更多 所以这里getcount+1
		return status.size();
	}
	@Override
	public Object getItem(int arg0) {
		return status.get(arg0);
	}
	
    //请求更多的数据
	public void addmoreDate(List<Status> addmore){
		if (status!=null) {
			this.status.addAll(addmore);//吧新传得数据加到现在的list中
			this.notifyDataSetChanged();//将数据追加到ListView中显示
		}
	}
	
	public void refreshData(List<Status> data) {
		if(status != null) {
			this.status.clear();
			this.status.addAll(data);
			this.notifyDataSetChanged();
		}
	}
	@Override
	public long getItemId(int index) {
			return Long.valueOf( status.get(index).getId() );
	}

	@SuppressWarnings({ "static-access", "deprecation" })
	@Override
	public View getView(final int position, View conterView, ViewGroup arg2) {
		
		View statusView=null;
		if ((conterView!=null)&&(conterView.findViewById(R.id.contentPic)!=null)) {
			statusView=conterView;
		}else {//如果缓存中没有就重新创建
			//加载一个新的View当 Root为null时可以拿到所有XML的资源文件
			statusView=LayoutInflater.from(mContext).inflate(R.layout.itemview, null);
		}
		ViewHolder vHolder=null;
		vHolder=new ViewHolder();
		vHolder.tvitemName=(TextView) statusView.findViewById(R.id.tvItemName);
		vHolder.tvContent=(TextView)statusView.findViewById(R.id.tvItemContent);
		vHolder.ivitemPorprait=(ImageView)statusView.findViewById(R.id.ivItemPortrait);
		vHolder.tvitemTimeData=(TextView)statusView.findViewById(R.id.tvItemDate);
		vHolder.subLayoutView=(LinearLayout) statusView.findViewById(R.id.subLayout);
		vHolder.tvitemSubcontent=(TextView) statusView.findViewById(R.id.tvItemSubContent);
		vHolder.contentPic=(ImageView)statusView.findViewById(R.id.contentPic);
		vHolder.subContenPic=(ImageView)statusView.findViewById(R.id.subContentPic);
		vHolder.tvitemFrom =(TextView)statusView.findViewById(R.id.tvItemFrom); 
		vHolder.ivItemGood = (ImageView) statusView.findViewById(R.id.ivItemGood);
		
		Status mstatus=status.get(position);

	//	if (mstatus.getUser().isVerified()) {
			vHolder.ivitemV=(ImageView) statusView.findViewById(R.id.ivItemV);
			vHolder.ivitemV.setVisibility(View.VISIBLE);
	//	}
		//设定发表微博的用户的昵称 
		vHolder.tvitemName.setText(mstatus.getUser().getName());
		//设定内容
		vHolder.tvContent.setText(mstatus.getText());
		vHolder.tvitemFrom.setText(" From: " + mstatus.getSource().getName());
		vHolder.ivItemGood.setImageResource(R.drawable.good30); 
		//设定表发微博的时间
		//mstatus
		vHolder.tvitemTimeData.setText(Utility.showTime(mstatus.getCreatedAt()));
		vHolder.ivItemGood.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageView ivItemGood = (ImageView) v;
				ivItemGood.setImageResource(R.drawable.gooood30);
				dictLike.add(Long.valueOf(status.get(position).getId()));
			}
		});
		
		//加载用户头像
		
		String usericon=mstatus.getUser().getProfileImageURL().toString();
		ansylodar.showimgAnsy(vHolder.ivitemPorprait, usericon);
		//判断是否又转发
		vHolder.contentPic.setVisibility(View.GONE);
		if (mstatus.getRetweetedStatus()!=null) {
			vHolder.subLayoutView.setVisibility(View.VISIBLE);//设置有转发布局可见
			vHolder.subContenPic.setVisibility(View.GONE);
		    Status comentsStatus=status.get(position).getRetweetedStatus();//获取又转发内容
			//vHolder.tvitemSubcontent.setText("@" + comentsStatus.getUser().getScreenName() + ":" + comentsStatus.getText());
			String text = "@" + comentsStatus.getUser().getScreenName() + ":";
			SpannableString ss = new SpannableString(text + comentsStatus.getText());
			ss.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			vHolder.tvitemSubcontent.setText(ss);
			
			//vHolder.tvitemSubcontent
		    String subconpic=comentsStatus.getThumbnailPic();
		  //  comentsStatus.getThumbnailPic()
		    if (null!=subconpic) {
		    	vHolder.subContenPic.setVisibility(View.VISIBLE);//设置又转发图片可见
				ansylodar.showimgAnsy(vHolder.subContenPic, subconpic);
		    }
		}else {//没有又转发 我们设置当前微博内容图片可见
			String pic_path=mstatus.getThumbnailPic();
			if (null!=pic_path) {
				   vHolder.contentPic.setVisibility(View.VISIBLE);
			       ansylodar.showimgAnsy(vHolder.contentPic, pic_path);
		    }
			vHolder.contentPic.setVisibility(View.GONE);
			vHolder.subLayoutView.setVisibility(View.GONE);//设置又转发布局不可见
		}
		vHolder.subContenPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//ImageView t = (ImageView) 
				String pic_path = status.get(position).getRetweetedStatus().getOriginalPic();
				if (pic_path == null) {
					pic_path = status.get(position).getRetweetedStatus().getBmiddlePic();
				} 
				if (pic_path == null) {
					pic_path = status.get(position).getRetweetedStatus().getThumbnailPic();
				}
				if (pic_path != null) { 
				
				System.out.println("a = " + status.get(position).getRetweetedStatus().getThumbnailPic() + " b = " + status.get(position).getRetweetedStatus().getBmiddlePic() + " c = " + status.get(position).getRetweetedStatus().getOriginalPic());
				Intent intent = new Intent( mContext, picActivity.class);
				
				intent.putExtra("pic_path", pic_path);
			//	startActivity(intent);
				mContext.startActivity(intent);	
				}
			}
		});
		
		
		vHolder.contentPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//ImageView t = (ImageView) v;
				String pic_path = status.get(position).getOriginalPic();
				//System.out.println("a = " + pic_path);
				if (pic_path == null) {
					pic_path = status.get(position).getBmiddlePic();
				} 
				//System.out.println("b = " + pic_path);
				if (pic_path == null) {
					pic_path = status.get(position).getThumbnailPic();
				}
				//System.out.println("c = "+ pic_path);
				System.out.println("a = " + status.get(position).getThumbnailPic() + " b = " + status.get(position).getBmiddlePic() + " c = " + status.get(position).getOriginalPic());
				if (pic_path != null) {  
				Intent intent = new Intent( mContext, picActivity.class);
				
				intent.putExtra("pic_path", pic_path);
			//	startActivity(intent);
				mContext.startActivity(intent);	
				}
			}
		});
		
		return statusView;
	}
	 

}
