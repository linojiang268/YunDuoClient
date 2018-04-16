package com.ydclient.adapter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ydclient.activity.R;
import com.ydclient.model.SceneInfo;

import frame.ydclient.socket.MyCon;

/**
 * 
 * @author ouArea
 * 
 */
public class SceneAdapter extends SuperAdapter {
	private ProgressDialog mProgressDialog;

	public SceneAdapter(Context context, boolean needCache) {
		super(context);
	}

	public void setDialog(ProgressDialog progressDialog) {
		this.mProgressDialog = progressDialog;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_scene, null);
			holder = new ViewHolder();
			holder.mibt = (ImageView) convertView.findViewById(R.id.ibt);
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
		final SceneInfo sceneInfo = (SceneInfo) getItem(position);
		holder.mtv.setText(sceneInfo.getName());
		holder.mibt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (null != mProgressDialog) {
					mProgressDialog.setMessage(mContext.getString(R.string.scene_do_waiting));
					mProgressDialog.show();
				} else {
					showToast(mContext.getString(R.string.scene_do_waiting));
				}
				MyCon.con(mContext).sendZlib((int) (System.currentTimeMillis() / 1000), 6510, "{\"_id\":" + sceneInfo.get_id() + "}");
			}
		});
		return convertView;
	}

	private class ViewHolder {
		public ImageView mibt;
		public TextView mtv;
		public SceneInfo sceneInfo;

		public OnClickListener clickListener;
		public OnLongClickListener longClickListener;
		public Builder builder;

		public void setListener() {
			clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 设置为常用场景
					showToast(mContext.getString(R.string.scene_set_used_waiting));
					MyCon.con(mContext).sendZlib((int) (System.currentTimeMillis() / 1000), 6506, "{\"_id\":" + sceneInfo._id + ",\"isUsed\":1}");
				}
			};
			longClickListener = new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					builder.show();
					return false;
				}
			};
			mibt.setOnClickListener(clickListener);
			mtv.setOnClickListener(clickListener);
			mibt.setOnLongClickListener(longClickListener);
			mtv.setOnLongClickListener(longClickListener);
			builder = new AlertDialog.Builder(mContext);
			builder.setTitle(mContext.getString(R.string.set_device));
			builder.setItems(new String[] { mContext.getString(R.string.scene_set_to_used), mContext.getString(R.string.scene_delete) }, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						// 设置为常用场景
						showToast(mContext.getString(R.string.scene_set_used_waiting));
						MyCon.con(mContext).sendZlib((int) (System.currentTimeMillis() / 1000), 6506, "{\"_id\":" + sceneInfo._id + ",\"isUsed\":1}");
						break;
					case 1:
						MyCon.con(mContext).sendZlib((int) (System.currentTimeMillis() / 1000), 6504, "{\"_id\":" + sceneInfo._id + "}");
						showToast(mContext.getString(R.string.scene_delete_waiting));
						break;
					default:
						break;
					}
				}
			});
		}
	}
}