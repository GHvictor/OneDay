package com.android.oneday.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.oneday.R;


public class GPSAdapter extends BaseAdapter {
	/**
	 * 保存当前适配器所在的窗体上下文对象
	 */
	private Context context;
	
	/**
	 * 构造方法，由当前的上下文对象构造这个用于显示GPS定位的适配器
	 * @param context
	 */
	public GPSAdapter(Context context) {
		this.context = context;
	}
	
	/**
	 * 返回列表的列表的数目，这里只有一个
	 * @return 列表的总数
	 */
	@Override
	public int getCount() {
		return 1;
	}

	/**
	 * 由列表的索引值得到列表的内容
	 * @param position 对应列表的索引值
	 * @return 列表内容
	 */
	@Override
	public Object getItem(int position) {
		//得到当前的资源文件对象
		Resources resource = context.getResources();
		return resource.getString(R.string.locale_city);
	}

	/**
	 * 返回当前位置元素的Id
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * 得到列表的视图
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater layout=LayoutInflater.from(context);
			convertView = layout.inflate(R.layout.gps_one_list, null);
		}
		return convertView;
	}

}
