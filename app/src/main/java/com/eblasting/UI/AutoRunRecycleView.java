package com.eblasting.UI;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.eblasting.IndexActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AutoRunRecycleView extends RecyclerView {
    private static final long TIME_AUTO_POLL = 100;
    AutoPollTask autoPollTask;
    private boolean running; //标示是否正在自动轮询
    private boolean canRun;//标示是否可以自动轮询,可在不需要的是否置false

    final static int itemHeight = 48 - 1;

    static Disposable disposable;

    public AutoRunRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        autoPollTask = new AutoPollTask(this);
    }

    static class AutoPollTask implements Runnable {
        private final WeakReference<AutoRunRecycleView> mReference;

        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask(AutoRunRecycleView reference) {
            this.mReference = new WeakReference<AutoRunRecycleView>(reference);
        }

        @Override
        public void run() {
            final AutoRunRecycleView recyclerView = mReference.get();
            if (recyclerView != null && recyclerView.running && recyclerView.canRun) {
                Observable.interval(0, TIME_AUTO_POLL, TimeUnit.MILLISECONDS)
                        .take(itemHeight + 1)
                        .map(new Function<Long, Long>() {
                            @Override
                            public Long apply(@NonNull Long aLong) throws Exception {
                                return itemHeight - aLong;
                            }
                        })
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                disposable = d;
                            }

                            @Override
                            public void onNext(@NonNull Long aLong) {
                                recyclerView.scrollBy(2, 1);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                recyclerView.postDelayed(recyclerView.autoPollTask, 2000);
                            }
                        });
            }
        }
    }

    //开启:如果正在运行,先停止->再开启
    public void start() {
        canRun = true;
        running = true;
        postDelayed(autoPollTask, 2000);
    }

    public static void release() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }


}
