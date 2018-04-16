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

import com.ydclient.activity.R;
import com.ydclient.activity.SecurityCtrlPage;
import com.ydclient.activity.SecurityIpCameraEditPage;
import com.ydclient.activity.SecurityIpCameraPlayPage;
import com.ydclient.activity.SecurityIpCameraPreparePlayPage;
import com.ydclient.activity.SecurityLearnPage;
import com.ydclient.db.IpCameraInfo;
import com.ydclient.db.IpCameraService;
import com.ydclient.ipcamera.SystemValue;
import com.ydclient.model.DeviceInfo;
import com.ydclient.type.TypeDevice;

import frame.ydclient.socket.MyCon;

/**
 * 监控列表
 * 
 * @author ouArea
 * 
 */
public class GvSecurityAdapter extends SuperAdapter {
	private IpCameraService mIpCameraService;

	// private Gson gson;
	public GvSecurityAdapter(Context context, IpCameraService ipCameraService) {
		super(context);
		this.mIpCameraService = ipCameraService;
		// gson = new Gson();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_gv_security, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (getItem(position) instanceof DeviceInfo) {
			DeviceInfo deviceInfo = (DeviceInfo) getItem(position);
			holder.deviceInfo = deviceInfo;
			holder.mibtSecurity = (ImageButton) convertView.findViewById(R.id.ibtSecurity);
			holder.mtvSecurity = (TextView) convertView.findViewById(R.id.tvSecurity);
			holder.mtvSecurity.setText(holder.deviceInfo.name);
			if (null != deviceInfo.content && deviceInfo.content.trim().equals("0")) {
				switch (deviceInfo.type) {
				case TypeDevice.SECURITY_FB:
				case TypeDevice.SECURITY_UN_FB:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_unchecked);
					break;
				case TypeDevice.SECURITY_FB_BLIND:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_blind_unchecked);
					break;
				case TypeDevice.SECURITY_FB_GAS:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_gas_unchecked);
					break;
				case TypeDevice.SECURITY_FB_SMOKE:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_smoke_unchecked);
					break;
				case TypeDevice.SECURITY_FB_TEMPERATURE:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_temperature_unchecked);
					break;
				case TypeDevice.SECURITY_FB_DOOR:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_door_unchecked);
					break;
				default:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_unchecked);
					break;
				}
			} else {
				switch (deviceInfo.type) {
				case TypeDevice.SECURITY_FB:
				case TypeDevice.SECURITY_UN_FB:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_checked);
					break;
				case TypeDevice.SECURITY_FB_BLIND:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_blind_checked);
					break;
				case TypeDevice.SECURITY_FB_GAS:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_gas_checked);
					break;
				case TypeDevice.SECURITY_FB_SMOKE:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_smoke_checked);
					break;
				case TypeDevice.SECURITY_FB_TEMPERATURE:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_temperature_checked);
					break;
				case TypeDevice.SECURITY_FB_DOOR:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_door_checked);
					break;
				default:
					holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_checked);
					break;
				}
			}
			holder.setListener();
		} else {
			final IpCameraInfo ipCameraInfo = (IpCameraInfo) getItem(position);
			holder.mibtSecurity = (ImageButton) convertView.findViewById(R.id.ibtSecurity);
			holder.mtvSecurity = (TextView) convertView.findViewById(R.id.tvSecurity);
			holder.mtvSecurity.setText(ipCameraInfo.name);
			holder.mibtSecurity.setBackgroundResource(R.drawable.main_security_unchecked);
			final Builder builder = new AlertDialog.Builder(mContext);
			final Builder delBuilder = new Builder(mContext);
			OnClickListener clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, SecurityIpCameraPreparePlayPage.class);
					intent.putExtra("IpCameraInfo", ipCameraInfo);
					mContext.startActivity(intent);
				}
			};
			OnLongClickListener longClickListener = new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					builder.show();
					return false;
				}
			};
			holder.mibtSecurity.setOnClickListener(clickListener);
			holder.mtvSecurity.setOnClickListener(clickListener);
			holder.mibtSecurity.setOnLongClickListener(longClickListener);
			holder.mtvSecurity.setOnLongClickListener(longClickListener);
			builder.setTitle(mContext.getString(R.string.set_device));
			builder.setItems(new String[] { mContext.getString(R.string.update_device), mContext.getString(R.string.delete_device) }, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						Intent intent = new Intent(mContext, SecurityIpCameraEditPage.class);
						intent.putExtra("IpCameraInfo", ipCameraInfo);
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
			delBuilder.setMessage(mContext.getString(R.string.delete_no_fb_notice));
			delBuilder.setTitle(mContext.getString(R.string.warn));
			delBuilder.setPositiveButton(mContext.getString(R.string.delete_sure), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// dialog.dismiss();
					// MyCon.con().sendZlib((int) (System.currentTimeMillis() /
					// 1000), 6004, "{\"_id\":" + deviceInfo._id + "}");
					// showToast(mContext.getString(R.string.delete_device_waiting));
					mIpCameraService.delete(ipCameraInfo.id);
					deleteItem(position);
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
		return convertView;
	}

	class ViewHolder {
		public ImageButton mibtSecurity;
		public TextView mtvSecurity;
		public DeviceInfo deviceInfo;
		public OnClickListener clickListener;
		public OnLongClickListener longClickListener;
		public Builder builder;
		public Builder delBuilder;

		public void setListener() {
			clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, SecurityCtrlPage.class);
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
			mibtSecurity.setOnClickListener(clickListener);
			mtvSecurity.setOnClickListener(clickListener);
			mibtSecurity.setOnLongClickListener(longClickListener);
			mtvSecurity.setOnLongClickListener(longClickListener);
			builder = new AlertDialog.Builder(mContext);
			builder.setTitle(mContext.getString(R.string.set_device));
			builder.setItems(new String[] { mContext.getString(R.string.update_device), mContext.getString(R.string.delete_device) }, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						Intent intent = new Intent(mContext, SecurityLearnPage.class);
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
