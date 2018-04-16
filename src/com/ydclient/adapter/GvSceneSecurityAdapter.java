package com.ydclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ydclient.activity.CallStartActivityForResult;
import com.ydclient.activity.R;
import com.ydclient.activity.SceneSecurityPage;
import com.ydclient.model.DeviceInfo;
import com.ydclient.type.TypeDevice;

public class GvSceneSecurityAdapter extends SuperAdapter {
	private CallStartActivityForResult callStartActivityForResult;

	// private Gson gson;
	public GvSceneSecurityAdapter(Context context, CallStartActivityForResult callStartActivityForResult) {
		super(context);
		this.callStartActivityForResult = callStartActivityForResult;
		// gson = new Gson();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_gv_security, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DeviceInfo deviceInfo = (DeviceInfo) getItem(position);
		holder.deviceInfo = deviceInfo;
		holder.mibtSecurity = (ImageButton) convertView.findViewById(R.id.ibtSecurity);
		holder.mtvSecurity = (TextView) convertView.findViewById(R.id.tvSecurity);
		holder.mtvSecurity.setText(holder.deviceInfo.name);
		switch (deviceInfo.type) {
		case TypeDevice.SECURITY_FB:
		case TypeDevice.SECURITY_UN_FB:
			holder.mibtSecurity.setBackgroundResource(R.drawable.bt_main_security_bg_style);
			break;
		case TypeDevice.SECURITY_FB_BLIND:
			holder.mibtSecurity.setBackgroundResource(R.drawable.bt_main_security_blind_bg_style);
			break;
		case TypeDevice.SECURITY_FB_GAS:
			holder.mibtSecurity.setBackgroundResource(R.drawable.bt_main_security_gas_bg_style);
			break;
		case TypeDevice.SECURITY_FB_SMOKE:
			holder.mibtSecurity.setBackgroundResource(R.drawable.bt_main_security_smoke_bg_style);
			break;
		case TypeDevice.SECURITY_FB_TEMPERATURE:
			holder.mibtSecurity.setBackgroundResource(R.drawable.bt_main_security_temperature_bg_style);
			break;
		case TypeDevice.SECURITY_FB_DOOR:
			holder.mibtSecurity.setBackgroundResource(R.drawable.bt_main_security_door_bg_style);
			break;
		default:
			holder.mibtSecurity.setBackgroundResource(R.drawable.bt_main_security_bg_style);
			break;
		}
		holder.setListener();
		return convertView;
	}

	class ViewHolder {
		public ImageButton mibtSecurity;
		public TextView mtvSecurity;
		public DeviceInfo deviceInfo;
		public OnClickListener clickListener;

		public void setListener() {
			clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, SceneSecurityPage.class);
					intent.putExtra("DeviceInfo", deviceInfo);
					// intent.putExtra("DeviceInfo", gson.toJson(deviceInfo));
					callStartActivityForResult.start(intent);
				}
			};
			mibtSecurity.setOnClickListener(clickListener);
			mtvSecurity.setOnClickListener(clickListener);
		}
	}

}
