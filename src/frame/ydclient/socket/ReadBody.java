package frame.ydclient.socket;

import org.json.JSONException;
import org.json.JSONObject;

public class ReadBody {
	public int mark;
	public int type;
	// public int length;
	public String msg;

	public ReadBody() {
		super();
	}

	public ReadBody(int mark, int type, String msg) {
		super();
		this.mark = mark;
		this.type = type;
		this.msg = msg;
	}

	public boolean parse(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			this.mark = jsonObject.getInt("mark");
			this.type = jsonObject.getInt("type");
			this.msg = jsonObject.getString("msg");
			return true;
		} catch (JSONException e) {
			// e.printStackTrace();
			this.mark = -1;
			return false;
		}
	}
}
