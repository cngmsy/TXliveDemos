package com.open_open.livedemos.presenters;

import com.open_open.livedemos.entity.CurLiveInfo;
import com.open_open.livedemos.entity.MySelfInfo;
import com.orhanobut.logger.Logger;
import com.tencent.ilivesdk.core.ILiveLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 网络请求类,所有的网址都在这里
 */
public class UserServerHelper {
    private static final String TAG = UserServerHelper.class.getSimpleName();
    private static UserServerHelper instance = null;

    public static final String SERVER = "https://sxb.qcloud.com/sxb_dev/index.php?";

    public static final String REGISTER             = SERVER+"svc=account&cmd=regist";
    public static final String LOGIN                = SERVER+"svc=account&cmd=login";
    public static final String LOGOUT               = SERVER+"svc=account&cmd=logout";
    public static final String APPLY_CREATE_ROOM    = SERVER+"svc=live&cmd=create";
    public static final String REPORT_ROOM_INFO     = SERVER+"svc=live&cmd=reportroom";
    public static final String HEART_BEAT           = SERVER+"svc=live&cmd=heartbeat";
    public static final String STOP_ILIVE           = SERVER+"svc=live&cmd=exitroom";
    public static final String GET_ROOMLIST         = SERVER+"svc=live&cmd=roomlist";
    public static final String REPORT_ME            = SERVER+"svc=live&cmd=reportmemid";
    public static final String GET_MEMLIST          = SERVER+"svc=live&cmd=roomidlist";
    public static final String REPORT_RECORD        = SERVER+"svc=live&cmd=reportrecord";
    public static final String GET_REOCORDLIST      = SERVER+"svc=live&cmd=recordlist";
    public static final String GET_PLAYERLIST       = SERVER+"svc=live&cmd=livestreamlist";
    public static final String GET_ROOM_PLAYURL     = SERVER+"svc=live&cmd=getroomplayurl";
    public static final String GET_COS_SIG          = SERVER+"svc=cos&cmd=get_sign";
    public static final String GET_LINK_SIG         = SERVER+"svc=live&cmd=linksig";

    private boolean bDebug = false;

    private String token = ""; //后续使用唯一标示
    private String Sig = ""; //登录唯一标示

    public class RequestBackInfo {

        int errorCode;
        String errorInfo;

        RequestBackInfo(int code, String bad) {
            errorCode = code;
            errorInfo = bad;
        }

        public int getErrorCode() {
            return errorCode;
        }


        public String getErrorInfo() {
            return errorInfo;
        }

    }

    /**
     * 单例
     * @return
     */
    public static UserServerHelper getInstance() {
        if (instance == null) {
            instance = new UserServerHelper();
        }
        return instance;
    }

    /**
     * 心跳上报
     */
    public RequestBackInfo heartBeater (int role) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("role", role);
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("roomnum", MySelfInfo.getInstance().getMyRoomNum());
            jasonPacket.put("thumbup", CurLiveInfo.getAdmires());
            String json = jasonPacket.toString();
            String res = post(HEART_BEAT, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 2222注册ID （独立方式）    注册方法在这里实现
     */
    public RequestBackInfo registerId(String id, String password) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("id", id);
            jasonPacket.put("pwd", password);
            String json = jasonPacket.toString();

            //post发送网络请求
            String res = post(REGISTER, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");

            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

    /**
     * 发送post网络请求——注册
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public String post(String url, String json) throws IOException {
        if (bDebug){
            Logger.d(TAG, "postReq->url:"+url);
            Logger.d(TAG, "postReq->data:"+json);
        }
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String rsp = response.body().string();
            if (bDebug){
                Logger.d(TAG, "postRsp->rsp: "+rsp);
            }
            return rsp;
        } else {
            return "";
        }
    }




    /**
     *   注册完成后登录ID （独立方式）
     */
    public RequestBackInfo loginId(String id, String password) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("id", id);
            jasonPacket.put("pwd", password);
            String json = jasonPacket.toString();
            String res = post(LOGIN, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();

            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            if (code == 0) {
                JSONObject data = response.getJSONObject("data");

                Sig = data.getString("userSig");
                token = data.getString("token");
                MySelfInfo.getInstance().setId(id);
                MySelfInfo.getInstance().setUserSig(Sig);
                MySelfInfo.getInstance().setToken(token);

            }
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





    /**
     * 上报成员
     */
    public RequestBackInfo reportMe(int role, int action) {
        try {
            JSONObject jasonPacket = new JSONObject();

            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            jasonPacket.put("roomnum", CurLiveInfo.getRoomNum());
            jasonPacket.put("id", MySelfInfo.getInstance().getId());
            jasonPacket.put("role", role);
            jasonPacket.put("operate", action);

            String json = jasonPacket.toString();
            String res = post(REPORT_ME, json);
            ILiveLog.i(TAG,"reportMe "+role+" action " + action);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 申请创建房间
     */
    public RequestBackInfo applyCreateRoom() {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("type", "live");
            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            String json = jasonPacket.toString();
            String res = post(APPLY_CREATE_ROOM, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            if (code == 0) {
                JSONObject data = response.getJSONObject("data");
                int avRoom = data.getInt("roomnum");
                MySelfInfo.getInstance().setMyRoomNum(avRoom);
                CurLiveInfo.setRoomNum(avRoom);
                String groupID = data.getString("groupid");
            }
            return new RequestBackInfo(code, errorInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 上报房间信息
     */
    public RequestBackInfo reporNewtRoomInfo(String inputJson) {
        try {

            String res = post(REPORT_ROOM_INFO, inputJson);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("errorCode");
            String errorInfo = response.getString("errorInfo");
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
