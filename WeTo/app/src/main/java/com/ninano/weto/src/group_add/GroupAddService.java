package com.ninano.weto.src.group_add;

import android.content.Context;

import com.ninano.weto.src.DefaultResponse2;
import com.ninano.weto.src.group_add.interfaces.GroupAddRetrofitInterface;
import com.ninano.weto.src.group_add.interfaces.GroupAddView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ninano.weto.src.ApplicationClass.MEDIA_TYPE_JSON;
import static com.ninano.weto.src.ApplicationClass.getRetrofit;

public class GroupAddService {

    private Context mContext;
    private GroupAddView mGroupAddView;

    GroupAddService(Context context, GroupAddView groupAddView){
        mContext = context;
        mGroupAddView = groupAddView;
    }

    void postGroup(int icon, String name){
        JSONObject params = new JSONObject();
        try {
            params.put("name", name);
            params.put("icon", icon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final GroupAddRetrofitInterface groupAddRetrofitInterface = getRetrofit().create(GroupAddRetrofitInterface.class);
        groupAddRetrofitInterface.postGroup(RequestBody.create(params.toString(), MEDIA_TYPE_JSON)).enqueue(new Callback<DefaultResponse2>() {
            @Override
            public void onResponse(Call<DefaultResponse2> call, Response<DefaultResponse2> response) {
                final DefaultResponse2 defaultResponse2 = response.body();
                if(defaultResponse2==null){
                    mGroupAddView.validateFailure(null);
                } else if(defaultResponse2.getCode()==100){
                    mGroupAddView.groupAddSuccess();
                } else {
                    mGroupAddView.validateFailure(defaultResponse2.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse2> call, Throwable t) {
                mGroupAddView.validateFailure(null);
            }
        });
    }
}
