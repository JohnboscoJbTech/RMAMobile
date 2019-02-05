package jbtechventures.com.rtma.Service;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Worker;

public class PostWorker extends Worker {

    @NonNull
    @Override
    public WorkerResult doWork() {
        // Do the work here--in this case,
        //This method will run on background thread
        syncResults(getApplicationContext());
        syncComplaints(getApplicationContext());

        // Indicate success or failure with your return value:
        return WorkerResult.SUCCESS;

        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
        //return null;

        /*OneTimeWorkRequest postingWork = new OneTimeWorkRequest.Builder(PostWorker.class).build();
        WorkManager.getInstance().enqueue(postingWork);*/
        /*Constraints myConstraints = new Constraints.Builder()
                .setRequiresDeviceIdle(true)
                .setRequiresCharging(true)
                // Many other constraints are available, see the
                // Constraints.Builder reference
                .build();*/
    }

    /**
     * Method handles syncing for Results to the server
     * calls a bg service that does the sync
     * */
    private void syncResults(Context context){
        PostService.startActionPostResult(context);
    }


    /**
     * Method handles syncing for Complaint to the server
     * calls a bg service that does the sync
     * */
    private void syncComplaints(Context context){
        PostService.startActionPostComplaint(context);
    }
}