package com.hananawwad.ketabi.firebase;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hananawwad.ketabi.BuildConfig;
import com.hananawwad.ketabi.util.Constants;
import com.hananawwad.ketabi.util.FLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hugo.weaving.DebugLog;
import lombok.Setter;

/**
 * @author hananawwad
 */
public class RPNFirebaseHelper {

    @Setter
    RPNEvents rpnEvents;
    Firebase firebase;

    public RPNFirebaseHelper(){
        firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.RPNs);
    }

    @DebugLog
    public List<String> fetchListOfRPN(){

        /** Checks RPN list **/
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(rpnEvents == null){
                    return;
                }

                List<String> returnList= new ArrayList<String>();
                HashMap<String, ?> mapHashMap = (HashMap<String, ?>) dataSnapshot.getValue();
                for(Map.Entry<String, ?> mapEntry : mapHashMap.entrySet()){
                    FLog.d(this, mapEntry.getKey());
                    returnList.add(mapEntry.getKey());
                }

                rpnEvents.onRPNFetched(returnList, null);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if(rpnEvents != null){
                    rpnEvents.onRPNFetched(null, firebaseError);
                }
            }
        });
        return null;
    }

    public interface RPNEvents{
        void onRPNFetched(List<String> rpnList, FirebaseError firebaseError);
    }

    public void setRpnEvents(RPNEvents rpnEvents) {
        this.rpnEvents = rpnEvents;
    }
}
