package com.pds.fast.ui.common.banner.entity;

import androidx.annotation.DrawableRes;

class LocalImageInfo extends SimpleBannerInfo {

    @DrawableRes
    private int bannerRes;

    public LocalImageInfo(int bannerRes) {
        this.bannerRes = bannerRes;
    }

    @Override
    public Integer getXBannerUrl() {
        return bannerRes;
    }
}
