package com.pds.fast.ui.common.floating.data


data class MinePetEntity(val petInfo: PetInfo?, val cfg: Cfg?) {
    companion object {
        private const val serialVersionUID = -1351376094484645457L
    }

    data class PetInfo(val petCode: String?) {
        val deviceId: String? = null
        val uid: String? = null
        val nickname: String? = null
        val drawTime: String? = null
        val desktopShow: String? = null
        val floatWinSwitch: String? = null

        companion object {
            private const val serialVersionUID = 6920562775755413518L
        }
    }

    data class Cfg(
        val backDesktopNotice: String?, val noticeSpace: String?, val timeQuantumNotice: List<QuantumNotice>?,
        val signNotice: QuantumNotice?, val clickNotice: List<String>?
    ) {

        val amStart: String? = null
        val amEnd: String? = null
        val listenMusicStart: String? = null
        val listenMusicEnd: String? = null

        companion object {
            private const val serialVersionUID = 6202971666460012584L
        }
    }

    data class QuantumNotice(val notice: String?, val times: Int?) {
        val min: Int? = null
        val max: Int? = null

        companion object {
            private const val serialVersionUID = 227487032660395463L
        }
    }
}