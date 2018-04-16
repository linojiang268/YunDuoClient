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
import com.ydclient.activity.SceneLightPage;
import com.ydclient.model.DeviceInfo;

public class GvSceneLightAdapter extends SuperAdapter {
	private CallStartActivityForResult callStartActivityForResult;

	// private Gson gson;
	public GvSceneLightAdapter(Context context, CallStartActivityForResult callStartActivityForResult) {
		super(context);
		this.callStartActivityForResult = callStartActivityForResult;
		// gson = new Gson();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_gv_light, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DeviceInfo deviceInfo = (DeviceInfo) getItem(position);
		holder.deviceInfo = deviceInfo;
		holder.mibtLight = (ImageButton) convertView.findViewById(R.id.ibtLight);
		holder.mtvLight = (TextView) convertView.findViewById(R.id.tvLight);
		holder.mtvLight.setText(holder.deviceInfo.name);
		holder.setListener();
		return convertView;
	}

	class ViewHolder {
		public ImageButton mibtLight;
		public TextView mtvLight;
		public DeviceInfo deviceInfo;
		public OnClickListener clickListener;

		public void setListener() {
			clickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, SceneLightPage.class);
					intent.putExtra("DeviceInfo", deviceInfo);
					// intent.putExtra("DeviceInfo", gson.toJson(deviceInfo));
					callStartActivityForResult.start(intent);
				}
			};
			mibtLight.setOnClickListener(clickListener);
			mtvLight.setOnClickListener(clickListener);
		}
	}

}
