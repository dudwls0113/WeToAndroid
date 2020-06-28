package com.ninano.weto.src.todo_add;

import com.ninano.weto.src.DefaultResponse2;
import com.ninano.weto.src.main.todo_group.models.Member;
import com.ninano.weto.src.todo_add.interfaces.AddGroupToDoRetrofitInterface;
import com.ninano.weto.src.todo_add.interfaces.AddGroupToDoView;
import com.ninano.weto.src.todo_add.models.AddGroupToDoMemberData;
import com.ninano.weto.src.todo_add.models.AddGroupToResponse;

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
        addGroupToDoRetrofitInterface.postToDo(RequestBody.create(params.toString(), MEDIA_TYPE_JSON)).enqueue(new Callback<AddGroupToResponse>() {
            @Override
            public void onResponse(Call<AddGroupToResponse> call, Response<AddGroupToResponse> response) {
                final AddGroupToResponse addGroupToResponse = response.body();
                if(addGroupToResponse==null){
                    mAddGroupToDoView.validateFailure(null);
                } else if(addGroupToResponse.getCode()==100){
                    mAddGroupToDoView.postToDoSuccess(addGroupToResponse.getGroupNo(), addGroupToResponse.getSeverTodoNo());
                } else {
                    mAddGroupToDoView.validateFailure(addGroupToResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AddGroupToResponse> call, Throwable t) {
                System.out.println(t.toString());
                mAddGroupToDoView.validateFailure(null);
            }
        });
    }
}
