package com.ydclient.adapter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ydclient.activity.R;
import com.ydclient.activity.SceneAddPage;
import com.ydclient.model.SceneInfo;

import frame.ydclient.socket.MyCon;

/**
 * 
 * @author ouArea
 * 
 */
public class GvSceneEidtAdapter extends SuperAdapter {

	public GvSceneEidtAdapter(Context context, boolean needCache) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_gv_scene, null);
			holder = new ViewHolder();
			holder.miv = (ImageView) convertView.findViewById(R.id.ibt);
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
		holder.sceneInfo = sceneInfo;
		holder.mtv.setText(sceneInfo.getName());
		holder.setListener();
		// holder.mibt.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// showToast(mContext.getString(R.string.scene_do_waiting));
		// MyCon.con().sendZlib((int) (System.currentTimeMillis() / 1000), 6510,
		// "{\"_id\":" +
		// sceneInfo.get_id() + "}");
		// }
		// });
		return convertView;
	}

	private class ViewHolder {
		public ImageView miv;
		public TextView mtv;
		public SceneInfo sceneInfo;
		public OnClickListener clickListener;
		public OnLongClickListener longClickListener;
		public Builder builder;

		public void setListener() {
			clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					MyCon.con(mContext).sendZlib((int) (System.currentTimeMillis() / 1000), 6506, "{\"_id\":" + sceneInfo.get_id() + ",\"isUsed\":1}");
					showToast(mContext.getString(R.string.scene_set_used_waiting));
				}
			};
			longClickListener = new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					builder.show();
					return false;
				}
			};
			miv.setOnClickListener(clickListener);
			// mtv.setOnClickListener(clickListener);
			miv.setOnLongClickListener(longClickListener);
			// mtvLight.setOnLongClickListener(longClickListener);
			builder = new AlertDialog.Builder(mContext);
			builder.setTitle(mContext.getString(R.string.scene_set));
			builder.setItems(new String[] { mContext.getString(R.string.scene_update), mContext.getString(R.string.scene_delete) }, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						Intent intent = new Intent(mContext, SceneAddPage.class);
						intent.putExtra("SceneInfo", sceneInfo);
						mContext.startActivity(intent);
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