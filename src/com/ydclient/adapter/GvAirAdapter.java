package com.ydclient.adapter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ydclient.activity.AirCtrlPage;
import com.ydclient.activity.AirLearnPage;
import com.ydclient.activity.R;
import com.ydclient.model.DeviceInfo;
import com.ydclient.type.TypeDevice;

import frame.ydclient.socket.MyCon;

/**
 * 空调列表
 * 
 * @author ouArea
 * 
 */
public class GvAirAdapter extends SuperAdapter {

	public GvAirAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_gv_air, null);
			holder = new ViewHolder();
			holder.mibt = (ImageButton) convertView.findViewById(R.id.ibt);
			holder.mtv = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DeviceInfo deviceInfo = (DeviceInfo) getItem(position);
		holder.deviceInfo = deviceInfo;
		holder.mtv.setText(deviceInfo.name);
		holder.setListener();
		return convertView;
	}

	class ViewHolder {
		public ImageButton mibt;
		public TextView mtv;
		public DeviceInfo deviceInfo;
		public OnClickListener clickListener;
		public OnLongClickListener longClickListener;
		public Builder builder;
		public Builder delBuilder;

		public void setListener() {
			clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, AirCtrlPage.class);
					intent.putExtra("DeviceInfo", deviceInfo);
					// intent.putExtra("DeviceInfo", gson.toJson(deviceInfo));
					mContext.startActivity(intent);
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
			builder.setItems(new String[] { mContext.getString(R.string.update_device), mContext.getString(R.string.delete_device) }, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						Intent intent = new Intent(mContext, AirLearnPage.class);
						intent.putExtra("DeviceInfo", deviceInfo);
						mContext.startActivity(intent);
						break;
					case 1:
						delBuilder.show();
						break;
					default:
						break;
					}
				}
			});
			delBuilder = new Builder(mContext);
			if (TypeDevice.hasFeedBack(deviceInfo.type)) {
				delBuilder.setMessage(mContext.getString(R.string.delete_fb_notice));
			} else {
				delBuilder.setMessage(mContext.getString(R.string.delete_no_fb_notice));
			}
			delBuilder.setTitle(mContext.getString(R.string.warn));
			delBuilder.setPositiveButton(mContext.getString(R.string.delete_sure), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// dialog.dismiss();
					MyCon.con(mContext).sendZlib((int) (System.currentTimeMillis() / 1000), 6004, "{\"_id\":" + deviceInfo._id + "}");
					showToast(mContext.getString(R.string.delete_device_waiting));
				}
			});
			delBuilder.setNegativeButton(mContext.getString(R.string.delete_cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// dialog.dismiss();
				}
			});
			// delBuilder.create().show();
		}
	}

}
