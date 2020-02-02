/*
 * Sounds.java - a class for playing event sounds.
 * Copyright (C) 1996-1998 John Neffenger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package COM.volano.chat.client;
import  java.applet.*;

/**
 * This class defines the applet sounds with methods to play them.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Sounds {
    public static final int START   = 0;
    public static final int STOP    = 1;
    public static final int ENTER   = 2;
    public static final int EXIT    = 3;
    public static final int ROOMS   = 4;
    public static final int USERS   = 5;
    public static final int PROFILE = 6;
    public static final int COUNT   = 7;

    private AudioClip[] sounds = new AudioClip[COUNT];
    private AudioClip   bell;             // For entrance and audio alerts

    /**
     * Creates a new applet sound object.
     *
     * @param value  the applet parameter and property values.
     */

    public Sounds(Value value) {
        if (value.soundStart != null) {
            sounds[START] = value.applet.getAudioClip(value.soundStart);
        }
        if (value.soundStop != null) {
            sounds[STOP] = value.applet.getAudioClip(value.soundStop);
        }
        if (value.soundEnter != null) {
            sounds[ENTER] = value.applet.getAudioClip(value.soundEnter);
        }
        if (value.soundExit != null) {
            sounds[EXIT] = value.applet.getAudioClip(value.soundExit);
        }
        if (value.soundRooms != null) {
            sounds[ROOMS] = value.applet.getAudioClip(value.soundRooms);
        }
        if (value.soundUsers != null) {
            sounds[USERS] = value.applet.getAudioClip(value.soundUsers);
        }
        if (value.soundProfile != null) {
            sounds[PROFILE] = value.applet.getAudioClip(value.soundProfile);
        }
        if (value.soundAlert != null) {
            bell = value.applet.getAudioClip(value.soundAlert);
        }
    }

    /**
     * Plays an event sound if defined.
     *
     * @param id  the identifier of the event sound to play.
     */

    public void play(int id) {
        if (sounds[id] != null) {
            sounds[id].play();
        }
    }

    /**
     * Plays the entrance and audio alert sound if defined.
     */

    public void alert() {
        if (bell != null) {
            bell.play();
        }
    }
}
