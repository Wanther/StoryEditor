package org.nojob.storyeditor.common;


import javafx.application.Platform;

import java.util.concurrent.*;

public abstract class AsyncTazk<Result> implements Callable<Result>{

    private FutureTask<Result> mTask;

    /**
     * invoke when is just submit to executor
     */
    protected void onStart(){}
    /**
     * this method runs in background
     */
    protected abstract Result doInBackground() throws Exception;

    // following methods run on UI thread
    protected void onSuccess(Result result){}

    protected void onCancelled(){

    }
    protected void onError(Exception e){

    }
    /**
     * whenever success or failed or cancelled, this method invoked at last
     */
    protected void onFinished(){

    }

    public Future<Result> execute(Executor exec){
        onStart();

        if (mTask != null) {
            throw new RuntimeException("do not execute the same task");
        }

        mTask = new AsyncTask(this);
        exec.execute(mTask);

        return mTask;
    }

    @Override
    public final Result call() throws Exception {
        return doInBackground();
    }

    /*package*/ void postIf(Runnable callback){
        Platform.runLater(callback);
    }

    /*package*/ Future<?> getTask() {
        return mTask;
    }

    /*package*/ class AsyncTask extends FutureTask<Result>{

        public AsyncTask(Callable<Result> callable) {
            super(callable);
        }

        @Override
        public void run() {
            super.run();
        }

        @Override
        protected void done() {
            postIf(() -> {
                try {
                    if(isCancelled()){
                        throw new CancellationException("task is cancelled");
                    }

                    onSuccess(get());

                } catch (InterruptedException | CancellationException e) {
                    onCancelled();
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if(cause instanceof Exception){
                        onError((Exception)cause);
                    }else{
                        // java.lang.Error ...
                        throw new RuntimeException(cause);
                    }
                } finally {
                    onFinished();
                    mTask = null;
                }
            });
        }

    }
}
