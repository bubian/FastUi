package com.pds.fast.ui.common.floating.data

data class MinePetModel(val petInfo: PetInfo?, val cfg: Cfg?) {

    companion object {
        @JvmStatic
        fun parse(entity: MinePetEntity?) = if (null == entity) MinePetModel(null, null)
        else MinePetModel(PetInfo.parse(entity.petInfo), Cfg.parse(entity.cfg))
    }

    fun isDesktopShow() = "1" == petInfo?.desktopShow

    data class PetInfo(val petCode: String?)  {
        var deviceId: String? = null
        var uid: String? = null
        var nickname: String? = null
        var drawTime: String? = null
        var desktopShow: String? = null
        var floatWinSwitch: String? = null

        companion object {
            @JvmStatic
            fun parse(entity: MinePetEntity.PetInfo?) = if (null == entity) PetInfo(null) else PetInfo(entity.petCode).apply {
                deviceId = entity.deviceId
                uid = entity.uid
                nickname = entity.nickname
                drawTime = entity.drawTime
                desktopShow = entity.desktopShow
                floatWinSwitch = entity.floatWinSwitch
            }
        }
    }

    data class Cfg(
        val backDesktopNotice: String?, val noticeSpace: String?, val timeQuantumNotice: List<QuantumNotice>?,
        val signNotice: QuantumNotice?, val clickNotice: List<String>?
    ) {
        var amStart: String? = null
        var amEnd: String? = null
        var listenMusicStart: String? = null
        var listenMusicEnd: String? = null

        companion object {
            @JvmStatic
            fun parse(entity: MinePetEntity.Cfg?) = if (null == entity) Cfg(
                null, null, null, null, null
            )
            else Cfg(
                entity.backDesktopNotice, entity.noticeSpace, QuantumNotice.parse(entity.timeQuantumNotice),
                QuantumNotice(entity.signNotice?.notice, entity.signNotice?.times), entity.clickNotice
            ).apply {
                amStart = entity.amStart ?: "06:00:00"
                amEnd = entity.amEnd ?: "10:30:00"
                listenMusicStart = entity.listenMusicStart ?: "10:31:00"
                listenMusicEnd = entity.listenMusicEnd ?: "23:59:00"
            }
        }
    }

    data class QuantumNotice(val notice: String?, val times: Int?, val min: Int? = null, val max: Int? = null) {

        companion object {
            @JvmStatic
            fun parse(entity: List<MinePetEntity.QuantumNotice>?) = if (entity.isNullOrEmpty()) null else arrayListOf<QuantumNotice>().apply {
                entity.forEach { add(QuantumNotice(it.notice, it.times, it.min, it.max)) }
            }
        }
    }
}