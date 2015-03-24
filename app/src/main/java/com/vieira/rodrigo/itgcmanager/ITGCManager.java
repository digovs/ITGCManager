package com.vieira.rodrigo.itgcmanager;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by 305988 on 24/03/2015.
 */
public class ITGCManager extends Application{

    private final String parseApplicationId = "rpRdywhlhIR3zAH11dx9YosMKx0NbUXZcSgVwKEy";
    private final String parseClientId = "Kb0bRqkDlTxkx92d19mNwqTDxdDLIi7UFA2Vonvb";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, parseApplicationId, parseClientId);
    }


}
