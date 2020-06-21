package com.ninano.weto.src.todo_add;

import com.ninano.weto.src.DefaultResponse2;
import com.ninano.weto.src.main.todo_group.models.Member;
import com.ninano.weto.src.todo_add.interfaces.AddGroupToDoRetrofitInterface;
import com.ninano.weto.src.todo_add.interfaces.AddGroupToDoView;
import com.ninano.weto.src.todo_add.models.AddGroupToDoMemberData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninano.weto.src.ApplicationClass.MEDIA_TYPE_JSON;
import static com.ninano.weto.src.ApplicationClass.getRetrofit;

public class AddGroupToDoService {

    private AddGroupToDoView mAddGroupToDoView;

    AddGroupToDoService(AddGroupToDoView addGroupToDoView){
        this.mAddGroupToDoView = addGroupToDoView;
    }

    void postToDoLocation(int groupNo, String title, String content, int icon, int type, ArrayList<AddGroupToDoMemberData> friendList, char isImportant,
                          double latitude, double longitude, int locationMode, String locationName, int radius, String ssid, char isWiFi, int timeSlot){
        JSONObject params = new JSONObject();
        try {
            params.put("groupNo", groupNo);
            params.put("title", title);
            params.put("content", content);
            params.put("icon", icon);
            params.put("type", type);
            JSONArray jsonArray = new JSONArray();
            for(int i=0; i<friendList.size(); i++){
                JSONObject temp = new JSONObject();
                temp.put("userNo", friendList.get(i).getUserId());
                jsonArray.put(temp);
            }
            params.put("friendList",jsonArray);
            params.put("isImportant", Character.toString(isImportant));
            params.put("latitude", latitude);
            params.put("longitude", longitude);
            params.put("locationMode", locationMode);
            params.put("locationName", locationName);
            params.put("radius", radius);
            params.put("ssid", ssid);
            params.put("isWiFi", Character.toString(isWiFi));
            params.put("timeSlot", timeSlot);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AddGroupToDoRetrofitInterface addGroupToDoRetrofitInterface = getRetrofit().create(AddGroupToDoRetrofitInterface.class);
        addGroupToDoRetrofitInterface.postToDo(RequestBody.create(params.toString(), MEDIA_TYPE_JSON)).enqueue(new Callback<DefaultResponse2>() {
            @Override
            public void onResponse(Call<DefaultResponse2> call, Response<DefaultResponse2> response) {
                final DefaultResponse2 defaultResponse2 = response.body();
                if(defaultResponse2==null){
                    mAddGroupToDoView.validateFailure(null);
                } else if(defaultResponse2.getCode()==100){
                    mAddGroupToDoView.postToDoSuccess();
                } else {
                    mAddGroupToDoView.validateFailure(defaultResponse2.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse2> call, Throwable t) {
                System.out.println(t.toString());
                mAddGroupToDoView.validateFailure(null);
            }
        });
    }
}
