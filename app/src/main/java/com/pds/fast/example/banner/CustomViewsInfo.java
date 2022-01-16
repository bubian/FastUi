package com.pds.fast.example.banner;


import com.pds.fast.ui.common.banner.entity.BaseBannerInfo;

public class CustomViewsInfo implements BaseBannerInfo {

    private String info;

    public CustomViewsInfo(String info) {
        this.info = info;
    }

    @Override
    public String getXBannerUrl() {
        return info;
    }

    @Override
    public String getXBannerTitle() {
        return null;
    }

}
