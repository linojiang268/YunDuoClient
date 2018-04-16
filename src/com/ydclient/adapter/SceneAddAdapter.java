package com.ydclient.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ydclient.activity.R;
import com.ydclient.model.SceneCommandMsg;

public class SceneAddAdapter extends SuperAdapter {

	public SceneAddAdapter(Context context, boolean needCache) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_scene_add, null);
			holder = new ViewHolder();
			holder.mtv = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// if (null == getItem(position)) {
		// holder.mivPhoto.setVisibility(View.INVISIBLE);
		// return convertView;
		// }
		// -------
		SceneCommandMsg sceneCommandMsg = (SceneCommandMsg) getItem(position);
		holder.mtv.setText(String.format(mContext.getString(R.string.scene_add_item_has_set), sceneCommandMsg.getName()));
		return convertView;
	}

	private class ViewHolder {
		public TextView mtv;
	}

}