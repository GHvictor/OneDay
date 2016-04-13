package com.android.oneday.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.oneday.R;
import com.android.oneday.vo.ScheduleVO;

import java.util.ArrayList;


/**
 * Created by dell on 4/13/2016.
 */
public class SortAdapter extends BaseAdapter{

    private ArrayList<ScheduleVO> list = null;
    private Context mContext;

    public SortAdapter(Context mContext, ArrayList<ScheduleVO> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(ArrayList<ScheduleVO> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final ScheduleVO mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.schedule_one_list, null);
            viewHolder.schOneType = (TextView) view.findViewById(R.id.schOneType);
            viewHolder.schOneDate = (TextView) view.findViewById(R.id.schOneDate);
            viewHolder.schOneRemind = (TextView) view.findViewById(R.id.schOneRemind);
            viewHolder.schOneContent = (TextView) view.findViewById(R.id.schOneContent);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        /*// 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        viewHolder.tvUserName.setText(list.get(position).getScheduleDate());
        Bitmap photo = null;
        try {
            photo = this.list.get(position).getPortrait();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Drawable drawable = new BitmapDrawable(view.getResources(),photo);
        viewHolder.ivUserPhoto.setImageDrawable(drawable);*/
        viewHolder.schOneType.setText(list.get(position).getScheduleTypeID());
        viewHolder.schOneDate.setText(list.get(position).getScheduleDate());
        viewHolder.schOneRemind.setText(list.get(position).getRemindID());
        viewHolder.schOneContent.setText(list.get(position).getScheduleContent());

        return view;
    }

    final static class ViewHolder {
        TextView schOneType;
        TextView schOneDate;
        TextView schOneRemind;
        TextView schOneContent;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getScheduleDate().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getScheduleDate();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    public Object[] getSections() {
        return null;
    }
}
