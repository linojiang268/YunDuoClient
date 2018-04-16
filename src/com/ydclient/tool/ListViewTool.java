package com.ydclient.tool;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewTool {
	// 重设list高度
	public synchronized static void reSetListViewHeight(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	// 重设gridwiew宽度
	public synchronized static void reSetGridViewHViewWidth(GridView gridView) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalWidth = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, gridView);
			listItem.measure(0, 0);
			totalWidth += listItem.getMeasuredWidth();
		}
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.width = (int) (totalWidth + (20 * (listAdapter.getCount() - 1)));
		gridView.setLayoutParams(params);
	}
}
