package com.ndfitnessplus.Utility;

import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by G50-70 on 29/12/2017.
 */

public class URLSpanNoUnderline extends URLSpan {
    public URLSpanNoUnderline(String p_Url) {
        super(p_Url);
    }

    public void updateDrawState(TextPaint p_DrawState) {
        super.updateDrawState(p_DrawState);
        p_DrawState.setUnderlineText(false);
    }
}
