package com.cuit.likedu.manager;

import com.cuit.likedu.bean.support.RefreshCollectionIconEvent;
import com.cuit.likedu.bean.support.RefreshCollectionListEvent;
import com.cuit.likedu.bean.support.SubEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author yuyh.
 * @date 17/1/30.
 */

public class EventManager {

    public static void refreshCollectionList() {
        EventBus.getDefault().post(new RefreshCollectionListEvent());
    }

    public static void refreshCollectionIcon() {
        EventBus.getDefault().post(new RefreshCollectionIconEvent());
    }

    public static void refreshSubCategory(String minor, String type) {
        EventBus.getDefault().post(new SubEvent(minor, type));
    }

}
