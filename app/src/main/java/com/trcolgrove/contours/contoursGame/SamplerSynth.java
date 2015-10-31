package com.trcolgrove.contours.contoursGame;

import android.content.Context;

import org.puredata.core.PdBase;

/**
 * Basic wrapper for autosampler PdClass used for sampled instruments
 *
 * Created by Thomas on 10/5/15.
 */
public class SamplerSynth extends PdSynth {

    SamplerSynth(String pdPatchName, Context context) {
        super(pdPatchName, context);
    }

    public void setSound(String soundName) {
        PdBase.sendList("soundfont", "set", soundName);
    }

    @Override
    public void noteOn(int midiNum, int velocity) {
        PdBase.sendList("note", midiNum, velocity);
    }

    @Override
    public void noteOff(int midiNum) {
        PdBase.sendList("note", midiNum, 0);
    }

}