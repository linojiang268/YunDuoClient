package com.ydclient.xmpp;

import java.util.Iterator;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;
import frame.ydclient.socket.ReadBody;

public class MyXMPP {
	// public static boolean isInitXmpp = false;

	// =============================================================================
	public static void setValue(Context context, String chatAddress, String adminName, String groupServerName) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("xmpp", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("isSet", true);
		editor.putString("chatAddress", chatAddress);
		editor.putString("adminName", adminName);
		editor.putString("groupServerName", groupServerName);
		editor.commit();
	}

	public static boolean isConnect() {
		if (null != con && con.isConnected()) {
			return true;
		} else {
			closeConnection();
			return false;
		}
	}

	public static boolean isSet(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("xmpp", Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean("isSet", false);
	}

	public static String getChatAddress(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("xmpp", Context.MODE_PRIVATE);
		return sharedPreferences.getString("chatAddress", "127.0.0.1");
	}

	public static String getAdminName(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("xmpp", Context.MODE_PRIVATE);
		return sharedPreferences.getString("adminName", "admin");
	}

	public static String getGroupServerName(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("xmpp", Context.MODE_PRIVATE);
		return sharedPreferences.getString("groupServerName", "conference.127.0.0.1");
	}

	// =============================================================================
	private static Context context;
	private static XMPPConnection con = null;
	private static MultiUserChat mul;

	// private static ChatPacketListener chatPacketListener;

	public static void initXmpp(final Context context, final Handler handler) throws XMPPException {
		if (!isSet(context)) {
			return;
		}
		MyXMPP.context = context;
		initCon();
		initMul();
		mul.addMessageListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				// System.out.println("消息格式:" + packet.toXML());
				Message message = (Message) packet;
				if (null != message.getBody()) {
					DelayInformation inf = (DelayInformation) message.getExtension("x", "jabber:x:delay");
					if (null == inf && message.getFrom().contains("/" + getAdminName(context))) {
						// System.out.println("新消息来了");
						String content = message.getBody();
						Log.i("MyXMPP read", content);
						content = content.substring(content.indexOf("<yd>"));
						if (content.startsWith("<yd>") && content.endsWith("<yd>")) {
							content = content.substring(content.indexOf("<yd>") + 4, content.lastIndexOf("<yd>"));
							try {
								JSONObject contentJson = new JSONObject(content);
								if (contentJson.has("server") && contentJson.has("mark") && contentJson.has("type") && contentJson.has("msg")) {
									android.os.Message msg = new android.os.Message();
									msg.what = 66;
									// Bundle bd = new Bundle();
									// bd.putString("from", message.getFrom());
									// bd.putString("body", content);
									// msg.setData(bd);
									msg.obj = new ReadBody(contentJson.getInt("mark"), contentJson.getInt("type"), contentJson.getString("msg"));
									handler.sendMessage(msg);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
		// isInitXmpp = true;
	}

	public static void closeConnection() {
		if (null != con && null != mul) {
			if (con.isConnected() && mul.isJoined()) {
				mul.leave();
			}
		}
		mul = null;
		// ==========================
		if (null != con) {
			if (con.isConnected()) {
				con.disconnect();
			}
		}
		con = null;
		// isInitXmpp = false;
	}

	public static void sendMulMsg(int mark, int type, String msg) throws MyXMPPException, XMPPException, JSONException {
		if (null == mul) {
			throw new MyXMPPException();
		}
		JSONObject sendJson = new JSONObject();
		sendJson.put("client", 0);
		sendJson.put("mark", mark);
		sendJson.put("type", type);
		sendJson.put("msg", msg);
		mul.sendMessage("<yd>" + sendJson.toString() + "<yd>");
		Log.i("MyXMPP send", sendJson.toString());
	}

	private static void initCon() throws XMPPException {
		ConnectionConfiguration connConfig = new ConnectionConfiguration(getChatAddress(context), 5222);
		con = new XMPPConnection(connConfig);
		con.connect();
		con.loginAnonymously();
		Presence presence = new Presence(Presence.Type.available);
		con.sendPacket(presence);
	}

	private static void initMul() throws XMPPException {
		mul = new MultiUserChat(con, getAdminName(context) + "@" + getGroupServerName(context));
		// mul.join(ComTool.getPhoneModel() + " " +
		// ComTool.getPhoneOSVersion());
		mul.join(String.valueOf((int) (System.currentTimeMillis() / 1000)));
		try {
			Form form = mul.getConfigurationForm();
			System.out.println("form:" + form.toString());
			// 根据原始表单创建一个要提交的新表单。
			Form submitForm = form.createAnswerForm();
			// 向要提交的表单添加默认答复
			for (Iterator<FormField> fields = form.getFields(); fields.hasNext();) {
				FormField field = (FormField) fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
					// 设置默认值作为答复
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// 设置聊天室是持久聊天室，即将要被保存下来
			submitForm.setAnswer("muc#roomconfig_persistentroom", true);
			// 房间仅对成员开放
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// 允许占有者邀请其他人
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			// 登录房间对话
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// 仅允许注册的昵称登录
			submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
			// 允许使用者修改昵称
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
			// 允许用户注册房间
			submitForm.setAnswer("x-muc#roomconfig_registration", false);
			// 发送已完成的表单（有默认值）到服务器来配置聊天室
			mul.sendConfigurationForm(submitForm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
