package com.yaofu.basesdk.managers;

import android.media.MediaRecorder;
import android.os.Handler;

/**
 * 录音工具类
 */
public class RecordingManager {

    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长
    private MediaRecorder mRecorder;

    // 停止录音
    public void stopRecord() {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler;

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    /**
     * 开始录音 使用amr格式
     */
    public void startRecord(String audioPath) {
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        try {
            /* ②setAudioSource/setVedioSource */
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            /* ③准备 */
            mRecorder.setOutputFile(audioPath);
            mRecorder.setMaxDuration(MAX_LENGTH);
            mRecorder.prepare();
            /* ④开始 */
            mRecorder.start();
            updateMicStatus();
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.recordError(e.getMessage());
            }
        }
    }

    private void updateMicStatus() {
        if (mRecorder != null) {
            double ratio = (double) mRecorder.getMaxAmplitude() / 1;
            double db = 0; // 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio); // 一般最小40分贝，最大90分贝
                if (listener != null) {
                    listener.getAmplitude((int) db);
                }
            }
            if (mHandler != null) {
                mHandler.postDelayed(mUpdateMicStatusTimer, 100);
            }
        }
    }

    public void setListener(RecorderCallback listener) {
        this.listener = listener;
    }

    private RecorderCallback listener;

    public interface RecorderCallback {
        void getAmplitude(int decibel);
        void recordError(String errorMsg);
    }

}