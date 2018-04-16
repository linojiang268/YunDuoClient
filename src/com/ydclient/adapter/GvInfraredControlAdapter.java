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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ydclient.activity.AirLearnPage;
import com.ydclient.activity.InfraredControlItemsPage;
import com.ydclient.activity.R;
import com.ydclient.activity.TvLearnPage;
import com.ydclient.model.DeviceInfo;
import com.ydclient.model.DeviceUpdateModel;
import com.ydclient.type.TypeDevice;

import frame.ydclient.socket.MyCon;

/**
 * 红外设备列表
 * 
 * @author ouArea
 * 
 */
public class GvInfraredControlAdapter extends SuperAdapter {
	private int mType;

	public GvInfraredControlAdapter(Context context) {
		super(context);
	}

	public void setType(int type) {
		this.mType = type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_gv_infrared_control, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DeviceInfo deviceInfo = (DeviceInfo) getItem(position);
		holder.deviceInfo = deviceInfo;
		holder.mibt = (ImageButton) convertView.findViewById(R.id.ibt);
		holder.mtv = (TextView) convertView.findViewById(R.id.tv);
		holder.mtv.setText(holder.deviceInfo.name);
		holder.setListener(mType);
		return convertView;
	}

	class ViewHolder {
		public ImageButton mibt;
		public TextView mtv;
		public DeviceInfo deviceInfo;
		public OnClickListener clickListener;
		public OnLongClickListener longClickListener;
		public Builder builder1, builder2;
		public Builder delBuilder;

		public void setListener(final int type) {
			clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = null;
					switch (type) {
					case InfraredControlItemsPage.TYPE_TV:
						intent = new Intent(mContext, TvLearnPage.class);
						intent.putExtra("control_device_mac", deviceInfo.getMark());
						// intent.putExtra("DeviceInfo",
						// gson.toJson(deviceInfo));
						mContext.startActivity(intent);
						break;
					case InfraredControlItemsPage.TYPE_AIR:
						intent = new Intent(mContext, AirLearnPage.class);
						intent.putExtra("control_device_mac", deviceInfo.getMark());
						// intent.putExtra("DeviceInfo",
						// gson.toJson(deviceInfo));
						mContext.startActivity(intent);
						break;
					default:
						break;
					}
				}
			};
			longClickListener = new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					builder1.show();
					return false;
				}
			};
			mibt.setOnClickListener(clickListener);
			mtv.setOnClickListener(clickListener);
			mibt.setOnLongClickListener(longClickListener);
			mtv.setOnLongClickListener(longClickListener);
			builder1 = new AlertDialog.Builder(mContext);
			builder1.setTitle(mContext.getString(R.string.set_device));
			builder1.setItems(new String[] { mContext.getString(R.string.update_device), mContext.getString(R.string.delete_device) }, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						builder2.show();
						break;
					case 1:
						delBuilder.show();
						break;
					default:
						break;
					}
				}
			});
			final EditText editText = new EditText(mContext);
			editText.setText(deviceInfo.getName());
			builder2 = new AlertDialog.Builder(mContext).setTitle(mContext.getString(R.string.set_device)).setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (editText.getText().toString().trim().length() > 0) {
						showToast(mContext.getString(R.string.save_waiting));
						deviceInfo.setName(editText.getText().toString().trim());
						DeviceUpdateModel deviceUpdateModel = new DeviceUpdateModel();
						deviceUpdateModel.deviceInfo = deviceInfo;
						MyCon.con(mContext).sendZlib((int) (System.currentTimeMillis() / 1000), 6002, new Gson().toJson(deviceUpdateModel));
						notifyDataSetChanged();
					}
				}
			}).setNegativeButton("取消", null);
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
