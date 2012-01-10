package com.anheinno.android.libs.ui;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public interface ProgressUIInterface {
    public void updateGauge(int val);
    public void resetGauge(String msg, int max, int start);
    public void resetGauge(String msg, int wait_seconds);
}
