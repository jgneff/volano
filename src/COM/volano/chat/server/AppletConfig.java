/*
 * AppletConfig.java - an interface for Web-based configuration of the applets.
 * Copyright (C) 2001 John Neffenger
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

package COM.volano.chat.server;
import  COM.volano.chat.Build;
import  COM.volano.chat.client.Key;
import  COM.volano.chat.client.Default;
import  COM.volano.io.UnicodeReader;
import  java.awt.Color;
import  java.awt.Font;
import  java.io.*;
import  java.net.URL;
import  java.net.MalformedURLException;
import  java.util.Properties;

/**
 * This class provides an interface for configuring the VolanoChat applets
 * through a Web interface.  This class is written with particular attention to
 * making it easy to use with the Velocity template engine.  This class acts as
 * a model for views created using the Velocity template language.  Java
 * servlets can use this model to configure the applet properties.
 *
 * @author  John Neffenger
 * @version 27 Jul 2001
 */

public class AppletConfig extends Config {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final String HEADER =  "VolanoChat Applet Properties - do not move or modify this line.";
    private static final String TEST   = "#VolanoChat Applet Properties";

    private static String[] filelist = new String[0];

    static void refreshFileList(File base) throws IOException {
        filelist = getFileList(base, TEST);
    }

    public static String[] getFileList() {
        return filelist;
    }

    AppletConfig(File base, String path) throws IOException {
        super(base, path, filelist);
    }

    // Instance methods.

    public String getFile() {
        return path;
    }

    // override.myvolanochat=true

    public boolean getOverrideMyvolanochat() {
        return getBoolean(Key.OVERRIDE_MYVOLANOCHAT, Default.OVERRIDE_MYVOLANOCHAT);
    }

    public void setOverrideMyvolanochat(String overrideMyvolanochat) {
        setBoolean(Key.OVERRIDE_MYVOLANOCHAT, overrideMyvolanochat);
    }

    // server.port=8000

    public int getServerPort() {
        return getInteger(Key.SERVER_PORT, Default.SERVER_PORT);
    }

    public void setServerPort(String serverPort) throws NumberFormatException {
        setInteger(Key.SERVER_PORT, serverPort);
    }

    // limit.public=-1

    public int getLimitPublic() {
        return getInteger(Key.LIMIT_PUBLIC, Default.LIMIT_PUBLIC);
    }

    public void setLimitPublic(String limitPublic) throws NumberFormatException {
        setInteger(Key.LIMIT_PUBLIC, limitPublic);
    }

    // limit.private=5

    public int getLimitPrivate() {
        return getInteger(Key.LIMIT_PRIVATE, Default.LIMIT_PRIVATE);
    }

    public void setLimitPrivate(String limitPrivate) throws NumberFormatException {
        setInteger(Key.LIMIT_PRIVATE, limitPrivate);
    }

    // history.enable=false

    public boolean getHistoryEnable() {
        return getBoolean(Key.HISTORY_ENABLE, Default.HISTORY_ENABLE);
    }

    public void setHistoryEnable(String historyEnable) {
        setBoolean(Key.HISTORY_ENABLE, historyEnable);
    }

    // member.document=

    public String getMemberDocument() {
        return getURL(Key.MEMBER_DOCUMENT, Default.MEMBER_DOCUMENT);
    }

    public void setMemberDocument(String memberDocument) throws MalformedURLException {
        setURL(Key.MEMBER_DOCUMENT, memberDocument);
    }

    // member.editable.name=true

    public boolean getMemberEditableName() {
        return getBoolean(Key.MEMBER_EDITABLE_NAME, Default.MEMBER_EDITABLE_NAME);
    }

    public void setMemberEditableName(String memberEditableName) {
        setBoolean(Key.MEMBER_EDITABLE_NAME, memberEditableName);
    }

    // member.editable.profile=true

    public boolean getMemberEditableProfile() {
        return getBoolean(Key.MEMBER_EDITABLE_PROFILE, Default.MEMBER_EDITABLE_PROFILE);
    }

    public void setMemberEditableProfile(String memberEditableProfile) {
        setBoolean(Key.MEMBER_EDITABLE_PROFILE, memberEditableProfile);
    }

    // member.monitor=false

    public boolean getMemberMonitor() {
        return getBoolean(Key.MEMBER_MONITOR, Default.MEMBER_MONITOR);
    }

    public void setMemberMonitor(String memberMonitor) {
        setBoolean(Key.MEMBER_MONITOR, memberMonitor);
    }

    // banner.code=COM.volano.BannerPlayer.class

    public String getBannerCode() {
        return getFile(Key.BANNER_CODE, Default.BANNER_CODE);
    }

    public void setBannerCode(String bannerCode) throws FileNotFoundException {
        setFile(Key.BANNER_CODE, bannerCode);
    }

    // banner.parameters=BannerPlayer.txt

    public String getBannerParameters() {
        return getFile(Key.BANNER_PARAMETERS, Default.BANNER_PARAMETERS);
    }

    public void setBannerParameters(String bannerParameters) throws FileNotFoundException {
        setFile(Key.BANNER_PARAMETERS, bannerParameters);
    }

    // banner.width=468

    public int getBannerWidth() {
        return getInteger(Key.BANNER_WIDTH, Default.BANNER_WIDTH);
    }

    public void setBannerWidth(String bannerWidth) throws NumberFormatException {
        setInteger(Key.BANNER_WIDTH, bannerWidth);
    }

    // banner.height=76

    public int getBannerHeight() {
        return getInteger(Key.BANNER_HEIGHT, Default.BANNER_HEIGHT);
    }

    public void setBannerHeight(String bannerHeight) throws NumberFormatException {
        setInteger(Key.BANNER_HEIGHT, bannerHeight);
    }

    // logo.code=COM.volano.BannerPlayer.class

    public String getLogoCode() {
        return getFile(Key.LOGO_CODE, Default.LOGO_CODE);
    }

    public void setLogoCode(String logoCode) throws FileNotFoundException {
        setFile(Key.LOGO_CODE, logoCode);
    }

    // logo.parameters=LogoPlayer.txt

    public String getLogoParameters() {
        return getFile(Key.LOGO_PARAMETERS, Default.LOGO_PARAMETERS);
    }

    public void setLogoParameters(String logoParameters) throws FileNotFoundException {
        setFile(Key.LOGO_PARAMETERS, logoParameters);
    }

    // logo.width=100

    public int getLogoWidth() {
        return getInteger(Key.LOGO_WIDTH, Default.LOGO_WIDTH);
    }

    public void setLogoWidth(String logoWidth) throws NumberFormatException {
        setInteger(Key.LOGO_WIDTH, logoWidth);
    }

    // logo.height=200

    public int getLogoHeight() {
        return getInteger(Key.LOGO_HEIGHT, Default.LOGO_HEIGHT);
    }

    public void setLogoHeight(String logoHeight) throws NumberFormatException {
        setInteger(Key.LOGO_HEIGHT, logoHeight);
    }

    // color.background=#C0C0C0

    public String getColorBackground() {
        return getColor(Key.COLOR_BACKGROUND, Default.COLOR_BACKGROUND);
    }

    public void setColorBackground(String colorBackground) throws NumberFormatException {
        setColor(Key.COLOR_BACKGROUND, colorBackground);
    }

    // color.foreground=#000000

    public String getColorForeground() {
        return getColor(Key.COLOR_FOREGROUND, Default.COLOR_FOREGROUND);
    }

    public void setColorForeground(String colorForeground) throws NumberFormatException {
        setColor(Key.COLOR_FOREGROUND, colorForeground);
    }

    // font.default=TimesRoman-15

    public String getFontDefault() {
        return getFont(Key.FONT_DEFAULT, Default.FONT_DEFAULT);
    }

    public void setFontDefault(String fontDefault) {
        setFont(Key.FONT_DEFAULT, fontDefault);
    }

    // alert.entrance.default=false

    public boolean getAlertEntranceDefault() {
        return getBoolean(Key.ALERT_ENTRANCE_DEFAULT, Default.ALERT_ENTRANCE_DEFAULT);
    }

    public void setAlertEntranceDefault(String alertEntranceDefault) {
        setBoolean(Key.ALERT_ENTRANCE_DEFAULT, alertEntranceDefault);
    }

    // alert.audio.default=false

    public boolean getAlertAudioDefault() {
        return getBoolean(Key.ALERT_AUDIO_DEFAULT, Default.ALERT_AUDIO_DEFAULT);
    }

    public void setAlertAudioDefault(String alertAudioDefault) {
        setBoolean(Key.ALERT_AUDIO_DEFAULT, alertAudioDefault);
    }

    // alert.count.default=false

    public boolean getAlertCountDefault() {
        return getBoolean(Key.ALERT_COUNT_DEFAULT, Default.ALERT_COUNT_DEFAULT);
    }

    public void setAlertCountDefault(String alertCountDefault) {
        setBoolean(Key.ALERT_COUNT_DEFAULT, alertCountDefault);
    }

    // webtouring.default=false

    public boolean getWebtouringDefault() {
        return getBoolean(Key.WEBTOURING_DEFAULT, Default.WEBTOURING_DEFAULT);
    }

    public void setWebtouringDefault(String webtouringDefault) {
        setBoolean(Key.WEBTOURING_DEFAULT, webtouringDefault);
    }

    // image.button.border=true

    public boolean getImageButtonBorder() {
        return getBoolean(Key.IMAGE_BUTTON_BORDER, Default.IMAGE_BUTTON_BORDER);
    }

    public void setImageButtonBorder(String imageButtonBorder) {
        setBoolean(Key.IMAGE_BUTTON_BORDER, imageButtonBorder);
    }

    // image.button1=button1.gif

    public String getImageButton1() {
        return getFile(Key.IMAGE_BUTTON1, Default.IMAGE_BUTTON1);
    }

    public void setImageButton1(String imageButton1) throws FileNotFoundException {
        setFile(Key.IMAGE_BUTTON1, imageButton1);
    }

    // image.button2=button2.gif

    public String getImageButton2() {
        return getFile(Key.IMAGE_BUTTON2, Default.IMAGE_BUTTON2);
    }

    public void setImageButton2(String imageButton2) throws FileNotFoundException {
        setFile(Key.IMAGE_BUTTON2, imageButton2);
    }

    // image.button.width=88

    public int getImageButtonWidth() {
        return getInteger(Key.IMAGE_BUTTON_WIDTH, Default.IMAGE_BUTTON_WIDTH);
    }

    public void setImageButtonWidth(String imageButtonWidth) throws NumberFormatException {
        setInteger(Key.IMAGE_BUTTON_WIDTH, imageButtonWidth);
    }

    // image.button.height=31

    public int getImageButtonHeight() {
        return getInteger(Key.IMAGE_BUTTON_HEIGHT, Default.IMAGE_BUTTON_HEIGHT);
    }

    public void setImageButtonHeight(String imageButtonHeight) throws NumberFormatException {
        setInteger(Key.IMAGE_BUTTON_HEIGHT, imageButtonHeight);
    }

    // image.logo=logo.gif

    public String getImageLogo() {
        return getFile(Key.IMAGE_LOGO, Default.IMAGE_LOGO);
    }

    public void setImageLogo(String imageLogo) throws FileNotFoundException {
        setFile(Key.IMAGE_LOGO, imageLogo);
    }

    // image.logo.width=100

    public int getImageLogoWidth() {
        return getInteger(Key.IMAGE_LOGO_WIDTH, Default.IMAGE_LOGO_WIDTH);
    }

    public void setImageLogoWidth(String imageLogoWidth) throws NumberFormatException {
        setInteger(Key.IMAGE_LOGO_WIDTH, imageLogoWidth);
    }

    // image.logo.height=200

    public int getImageLogoHeight() {
        return getInteger(Key.IMAGE_LOGO_HEIGHT, Default.IMAGE_LOGO_HEIGHT);
    }

    public void setImageLogoHeight(String imageLogoHeight) throws NumberFormatException {
        setInteger(Key.IMAGE_LOGO_HEIGHT, imageLogoHeight);
    }

    // image.logo.background=#FFFFFF

    public String getImageLogoBackground() {
        return getColor(Key.IMAGE_LOGO_BACKGROUND, Default.IMAGE_LOGO_BACKGROUND);
    }

    public void setImageLogoBackground(String imageLogoBackground) throws NumberFormatException {
        setColor(Key.IMAGE_LOGO_BACKGROUND, imageLogoBackground);
    }

    // length.chattext=1000

    public int getLengthChattext() {
        return getInteger(Key.LENGTH_CHATTEXT, Default.LENGTH_CHATTEXT);
    }

    public void setLengthChattext(String lengthChattext) throws NumberFormatException {
        setInteger(Key.LENGTH_CHATTEXT, lengthChattext);
    }

    // length.profile=1000

    public int getLengthProfile() {
        return getInteger(Key.LENGTH_PROFILE, Default.LENGTH_PROFILE);
    }

    public void setLengthProfile(String lengthProfile) throws NumberFormatException {
        setInteger(Key.LENGTH_PROFILE, lengthProfile);
    }

    // length.roomname=100

    public int getLengthRoomname() {
        return getInteger(Key.LENGTH_ROOMNAME, Default.LENGTH_ROOMNAME);
    }

    public void setLengthRoomname(String lengthRoomname) throws NumberFormatException {
        setInteger(Key.LENGTH_ROOMNAME, lengthRoomname);
    }

    // length.username=20

    public int getLengthUsername() {
        return getInteger(Key.LENGTH_USERNAME, Default.LENGTH_USERNAME);
    }

    public void setLengthUsername(String lengthUsername) throws NumberFormatException {
        setInteger(Key.LENGTH_USERNAME, lengthUsername);
    }

    // page.newwindow=false

    public boolean getPageNewwindow() {
        return getBoolean(Key.PAGE_NEWWINDOW, Default.PAGE_NEWWINDOW);
    }

    public void setPageNewwindow(String pageNewwindow) {
        setBoolean(Key.PAGE_NEWWINDOW, pageNewwindow);
    }

    // page.use.document.base=false

    public boolean getPageUseDocumentBase() {
        return getBoolean(Key.PAGE_USE_DOCUMENT_BASE, Default.PAGE_USE_DOCUMENT_BASE);
    }

    public void setPageUseDocumentBase(String pageUseDocumentBase) {
        setBoolean(Key.PAGE_USE_DOCUMENT_BASE, pageUseDocumentBase);
    }

    // We can't use getFile/setFile on the following properties since we don't
    // know whether the file paths are relative to the applet's code base or
    // document base.

    // page.access.document=document.html

    public String getPageAccessDocument() {
        return getString(Key.PAGE_ACCESS_DOCUMENT, Default.PAGE_ACCESS_DOCUMENT);
    }

    public void setPageAccessDocument(String pageAccessDocument) {
        setString(Key.PAGE_ACCESS_DOCUMENT, pageAccessDocument);
    }

    // page.access.host=host.html

    public String getPageAccessHost() {
        return getString(Key.PAGE_ACCESS_HOST, Default.PAGE_ACCESS_HOST);
    }

    public void setPageAccessHost(String pageAccessHost) {
        setString(Key.PAGE_ACCESS_HOST, pageAccessHost);
    }

    // page.access.password=password.html

    public String getPageAccessPassword() {
        return getString(Key.PAGE_ACCESS_PASSWORD, Default.PAGE_ACCESS_PASSWORD);
    }

    public void setPageAccessPassword(String pageAccessPassword) {
        setString(Key.PAGE_ACCESS_PASSWORD, pageAccessPassword);
    }

    // page.access.unable=unable.html

    public String getPageAccessUnable() {
        return getString(Key.PAGE_ACCESS_UNABLE, Default.PAGE_ACCESS_UNABLE);
    }

    public void setPageAccessUnable(String pageAccessUnable) {
        setString(Key.PAGE_ACCESS_UNABLE, pageAccessUnable);
    }

    // page.access.version=version.html

    public String getPageAccessVersion() {
        return getString(Key.PAGE_ACCESS_VERSION, Default.PAGE_ACCESS_VERSION);
    }

    public void setPageAccessVersion(String pageAccessVersion) {
        setString(Key.PAGE_ACCESS_VERSION, pageAccessVersion);
    }

    // page.help=help.html

    public String getPageHelp() {
        return getString(Key.PAGE_HELP, Default.PAGE_HELP);
    }

    public void setPageHelp(String pageHelp) {
        setString(Key.PAGE_HELP, pageHelp);
    }

    // page.about=about.html

    public String getPageAbout() {
        return getString(Key.PAGE_ABOUT, Default.PAGE_ABOUT);
    }

    public void setPageAbout(String pageAbout) {
        setString(Key.PAGE_ABOUT, pageAbout);
    }

    // page.exit=

    public String getPageExit() {
        return getString(Key.PAGE_EXIT, Default.PAGE_EXIT);
    }

    public void setPageExit(String pageExit) {
        setString(Key.PAGE_EXIT, pageExit);
    }

    // page.exit.error=

    public String getPageExitError() {
        return getString(Key.PAGE_EXIT_ERROR, Default.PAGE_EXIT_ERROR);
    }

    public void setPageExitError(String pageExitError) {
        setString(Key.PAGE_EXIT_ERROR, pageExitError);
    }

    // sound.start=

    public String getSoundStart() {
        return getFile(Key.SOUND_START, Default.SOUND_START);
    }

    public void setSoundStart(String soundStart) throws FileNotFoundException {
        setFile(Key.SOUND_START, soundStart);
    }

    // sound.stop=

    public String getSoundStop() {
        return getFile(Key.SOUND_STOP, Default.SOUND_STOP);
    }

    public void setSoundStop(String soundStop) throws FileNotFoundException {
        setFile(Key.SOUND_STOP, soundStop);
    }

    // sound.enter=

    public String getSoundEnter() {
        return getFile(Key.SOUND_ENTER, Default.SOUND_ENTER);
    }

    public void setSoundEnter(String soundEnter) throws FileNotFoundException {
        setFile(Key.SOUND_ENTER, soundEnter);
    }

    // sound.exit=

    public String getSoundExit() {
        return getFile(Key.SOUND_EXIT, Default.SOUND_EXIT);
    }

    public void setSoundExit(String soundExit) throws FileNotFoundException {
        setFile(Key.SOUND_EXIT, soundExit);
    }

    // sound.rooms=

    public String getSoundRooms() {
        return getFile(Key.SOUND_ROOMS, Default.SOUND_ROOMS);
    }

    public void setSoundRooms(String soundRooms) throws FileNotFoundException {
        setFile(Key.SOUND_ROOMS, soundRooms);
    }

    // sound.users=

    public String getSoundUsers() {
        return getFile(Key.SOUND_USERS, Default.SOUND_USERS);
    }

    public void setSoundUsers(String soundUsers) throws FileNotFoundException {
        setFile(Key.SOUND_USERS, soundUsers);
    }

    // sound.profile=

    public String getSoundProfile() {
        return getFile(Key.SOUND_PROFILE, Default.SOUND_PROFILE);
    }

    public void setSoundProfile(String soundProfile) throws FileNotFoundException {
        setFile(Key.SOUND_PROFILE, soundProfile);
    }

    // sound.alert=drip.au

    public String getSoundAlert() {
        return getFile(Key.SOUND_ALERT, Default.SOUND_ALERT);
    }

    public void setSoundAlert(String soundAlert) throws FileNotFoundException {
        setFile(Key.SOUND_ALERT, soundAlert);
    }

    // key.ignore.alt=

    public String getKeyIgnoreAlt() {
        return getChars(Key.KEY_IGNORE_ALT, Default.KEY_IGNORE_ALT);
    }

    public void setKeyIgnoreAlt(String keyIgnoreAlt) throws IOException {
        setChars(Key.KEY_IGNORE_ALT, keyIgnoreAlt);
    }

    // key.ignore.ctrl=\\u0016

    public String getKeyIgnoreCtrl() {
        return getChars(Key.KEY_IGNORE_CTRL, Default.KEY_IGNORE_CTRL);
    }

    public void setKeyIgnoreCtrl(String keyIgnoreCtrl) throws IOException {
        setChars(Key.KEY_IGNORE_CTRL, keyIgnoreCtrl);
    }

    // key.ignore.meta=\\u0076

    public String getKeyIgnoreMeta() {
        return getChars(Key.KEY_IGNORE_META, Default.KEY_IGNORE_META);
    }

    public void setKeyIgnoreMeta(String keyIgnoreMeta) throws IOException {
        setChars(Key.KEY_IGNORE_META, keyIgnoreMeta);
    }

    // key.ignore.shift=\\u0401

    public String getKeyIgnoreShift() {
        return getChars(Key.KEY_IGNORE_SHIFT, Default.KEY_IGNORE_SHIFT);
    }

    public void setKeyIgnoreShift(String keyIgnoreShift) throws IOException {
        setChars(Key.KEY_IGNORE_SHIFT, keyIgnoreShift);
    }

    // char.replace.old=\\u000a\\u000d\\u00a0

    public String getCharReplaceOld() {
        return getChars(Key.CHAR_REPLACE_OLD, Default.CHAR_REPLACE_OLD);
    }

    public void setCharReplaceOld(String charReplaceOld) throws IOException {
        setChars(Key.CHAR_REPLACE_OLD, charReplaceOld);
    }

    // char.replace.new=\\u0020\\u0020\\u0020

    public String getCharReplaceNew() {
        return getChars(Key.CHAR_REPLACE_NEW, Default.CHAR_REPLACE_NEW);
    }

    public void setCharReplaceNew(String charReplaceNew) throws IOException {
        setChars(Key.CHAR_REPLACE_NEW, charReplaceNew);
    }

    // text.f1=

    public String getTextF1() {
        return getString(Key.TEXT_F1, Default.TEXT_F1);
    }

    public void setTextF1(String textF1) {
        setString(Key.TEXT_F1, textF1);
    }

    // text.f2=

    public String getTextF2() {
        return getString(Key.TEXT_F2, Default.TEXT_F2);
    }

    public void setTextF2(String textF2) {
        setString(Key.TEXT_F2, textF2);
    }

    // text.f3=

    public String getTextF3() {
        return getString(Key.TEXT_F3, Default.TEXT_F3);
    }

    public void setTextF3(String textF3) {
        setString(Key.TEXT_F3, textF3);
    }

    // text.f4=

    public String getTextF4() {
        return getString(Key.TEXT_F4, Default.TEXT_F4);
    }

    public void setTextF4(String textF4) {
        setString(Key.TEXT_F4, textF4);
    }

    // text.f5=

    public String getTextF5() {
        return getString(Key.TEXT_F5, Default.TEXT_F5);
    }

    public void setTextF5(String textF5) {
        setString(Key.TEXT_F5, textF5);
    }

    // text.f6=

    public String getTextF6() {
        return getString(Key.TEXT_F6, Default.TEXT_F6);
    }

    public void setTextF6(String textF6) {
        setString(Key.TEXT_F6, textF6);
    }

    // text.f7=

    public String getTextF7() {
        return getString(Key.TEXT_F7, Default.TEXT_F7);
    }

    public void setTextF7(String textF7) {
        setString(Key.TEXT_F7, textF7);
    }

    // text.f8=

    public String getTextF8() {
        return getString(Key.TEXT_F8, Default.TEXT_F8);
    }

    public void setTextF8(String textF8) {
        setString(Key.TEXT_F8, textF8);
    }

    // text.f9=

    public String getTextF9() {
        return getString(Key.TEXT_F9, Default.TEXT_F9);
    }

    public void setTextF9(String textF9) {
        setString(Key.TEXT_F9, textF9);
    }

    // text.f10=

    public String getTextF10() {
        return getString(Key.TEXT_F10, Default.TEXT_F10);
    }

    public void setTextF10(String textF10) {
        setString(Key.TEXT_F10, textF10);
    }

    // text.f11=

    public String getTextF11() {
        return getString(Key.TEXT_F11, Default.TEXT_F11);
    }

    public void setTextF11(String textF11) {
        setString(Key.TEXT_F11, textF11);
    }

    // text.f12=

    public String getTextF12() {
        return getString(Key.TEXT_F12, Default.TEXT_F12);
    }

    public void setTextF12(String textF12) {
        setString(Key.TEXT_F12, textF12);
    }

    // text.button.status=Start VolanoChat

    public String getTextButtonStatus() {
        return getString(Key.TEXT_BUTTON_STATUS, Default.TEXT_BUTTON_STATUS);
    }

    public void setTextButtonStatus(String textButtonStatus) {
        setString(Key.TEXT_BUTTON_STATUS, textButtonStatus);
    }

    // text.button.message=Click button to start.

    public String getTextButtonMessage() {
        return getString(Key.TEXT_BUTTON_MESSAGE, Default.TEXT_BUTTON_MESSAGE);
    }

    public void setTextButtonMessage(String textButtonMessage) {
        setString(Key.TEXT_BUTTON_MESSAGE, textButtonMessage);
    }

    // text.button.connecting=Contacting host %0...

    public String getTextButtonConnecting() {
        return getString(Key.TEXT_BUTTON_CONNECTING, Default.TEXT_BUTTON_CONNECTING);
    }

    public void setTextButtonConnecting(String textButtonConnecting) {
        setString(Key.TEXT_BUTTON_CONNECTING, textButtonConnecting);
    }

    // text.button.accessing=Host contacted, requesting access...

    public String getTextButtonAccessing() {
        return getString(Key.TEXT_BUTTON_ACCESSING, Default.TEXT_BUTTON_ACCESSING);
    }

    public void setTextButtonAccessing(String textButtonAccessing) {
        setString(Key.TEXT_BUTTON_ACCESSING, textButtonAccessing);
    }

    // text.button.notconnected=Unable to connect to host %0 on port %1.

    public String getTextButtonNotconnected() {
        return getString(Key.TEXT_BUTTON_NOTCONNECTED, Default.TEXT_BUTTON_NOTCONNECTED);
    }

    public void setTextButtonNotconnected(String textButtonNotconnected) {
        setString(Key.TEXT_BUTTON_NOTCONNECTED, textButtonNotconnected);
    }

    // text.button.admin=Administrator password:

    public String getTextButtonAdmin() {
        return getString(Key.TEXT_BUTTON_ADMIN, Default.TEXT_BUTTON_ADMIN);
    }

    public void setTextButtonAdmin(String textButtonAdmin) {
        setString(Key.TEXT_BUTTON_ADMIN, textButtonAdmin);
    }

    // text.button.monitor=Monitor password:

    public String getTextButtonMonitor() {
        return getString(Key.TEXT_BUTTON_MONITOR, Default.TEXT_BUTTON_MONITOR);
    }

    public void setTextButtonMonitor(String textButtonMonitor) {
        setString(Key.TEXT_BUTTON_MONITOR, textButtonMonitor);
    }

    // text.member.name=Member name:

    public String getTextMemberName() {
        return getString(Key.TEXT_MEMBER_NAME, Default.TEXT_MEMBER_NAME);
    }

    public void setTextMemberName(String textMemberName) {
        setString(Key.TEXT_MEMBER_NAME, textMemberName);
    }

    // text.member.password=Member password:

    public String getTextMemberPassword() {
        return getString(Key.TEXT_MEMBER_PASSWORD, Default.TEXT_MEMBER_PASSWORD);
    }

    public void setTextMemberPassword(String textMemberPassword) {
        setString(Key.TEXT_MEMBER_PASSWORD, textMemberPassword);
    }

    // text.member.profile=[Member Profile]

    public String getTextMemberProfile() {
        return getString(Key.TEXT_MEMBER_PROFILE, Default.TEXT_MEMBER_PROFILE);
    }

    public void setTextMemberProfile(String textMemberProfile) {
        setString(Key.TEXT_MEMBER_PROFILE, textMemberProfile);
    }

    // text.main.title=VolanoChat 2.1

    public String getTextMainTitle() {
        return getString(Key.TEXT_MAIN_TITLE, Default.TEXT_MAIN_TITLE);
    }

    public void setTextMainTitle(String textMainTitle) {
        setString(Key.TEXT_MAIN_TITLE, textMainTitle);
    }

    // text.main.logo=

    public String getTextMainLogo() {
        return getString(Key.TEXT_MAIN_LOGO, Default.TEXT_MAIN_LOGO);
    }

    public void setTextMainLogo(String textMainLogo) {
        setString(Key.TEXT_MAIN_LOGO, textMainLogo);
    }

    // text.main.rooms=Rooms:

    public String getTextMainRooms() {
        return getString(Key.TEXT_MAIN_ROOMS, Default.TEXT_MAIN_ROOMS);
    }

    public void setTextMainRooms(String textMainRooms) {
        setString(Key.TEXT_MAIN_ROOMS, textMainRooms);
    }

    // text.main.norooms=Rooms:

    public String getTextMainNorooms() {
        return getString(Key.TEXT_MAIN_NOROOMS, Default.TEXT_MAIN_NOROOMS);
    }

    public void setTextMainNorooms(String textMainNorooms) {
        setString(Key.TEXT_MAIN_NOROOMS, textMainNorooms);
    }

    // text.main.oneroom=1 room:

    public String getTextMainOneroom() {
        return getString(Key.TEXT_MAIN_ONEROOM, Default.TEXT_MAIN_ONEROOM);
    }

    public void setTextMainOneroom(String textMainOneroom) {
        setString(Key.TEXT_MAIN_ONEROOM, textMainOneroom);
    }

    // text.main.manyrooms=%0 rooms:

    public String getTextMainManyrooms() {
        return getString(Key.TEXT_MAIN_MANYROOMS, Default.TEXT_MAIN_MANYROOMS);
    }

    public void setTextMainManyrooms(String textMainManyrooms) {
        setString(Key.TEXT_MAIN_MANYROOMS, textMainManyrooms);
    }

    // text.main.users=People:

    public String getTextMainUsers() {
        return getString(Key.TEXT_MAIN_USERS, Default.TEXT_MAIN_USERS);
    }

    public void setTextMainUsers(String textMainUsers) {
        setString(Key.TEXT_MAIN_USERS, textMainUsers);
    }

    // text.main.nousers=People:

    public String getTextMainNousers() {
        return getString(Key.TEXT_MAIN_NOUSERS, Default.TEXT_MAIN_NOUSERS);
    }

    public void setTextMainNousers(String textMainNousers) {
        setString(Key.TEXT_MAIN_NOUSERS, textMainNousers);
    }

    // text.main.oneuser=1 person:

    public String getTextMainOneuser() {
        return getString(Key.TEXT_MAIN_ONEUSER, Default.TEXT_MAIN_ONEUSER);
    }

    public void setTextMainOneuser(String textMainOneuser) {
        setString(Key.TEXT_MAIN_ONEUSER, textMainOneuser);
    }

    // text.main.manyusers=%0 persons:

    public String getTextMainManyusers() {
        return getString(Key.TEXT_MAIN_MANYUSERS, Default.TEXT_MAIN_MANYUSERS);
    }

    public void setTextMainManyusers(String textMainManyusers) {
        setString(Key.TEXT_MAIN_MANYUSERS, textMainManyusers);
    }

    // text.main.onstage=On stage:

    public String getTextMainOnstage() {
        return getString(Key.TEXT_MAIN_ONSTAGE, Default.TEXT_MAIN_ONSTAGE);
    }

    public void setTextMainOnstage(String textMainOnstage) {
        setString(Key.TEXT_MAIN_ONSTAGE, textMainOnstage);
    }

    // text.main.filter=Room filter:

    public String getTextMainFilter() {
        return getString(Key.TEXT_MAIN_FILTER, Default.TEXT_MAIN_FILTER);
    }

    public void setTextMainFilter(String textMainFilter) {
        setString(Key.TEXT_MAIN_FILTER, textMainFilter);
    }

    // text.main.username=Your name:

    public String getTextMainUsername() {
        return getString(Key.TEXT_MAIN_USERNAME, Default.TEXT_MAIN_USERNAME);
    }

    public void setTextMainUsername(String textMainUsername) {
        setString(Key.TEXT_MAIN_USERNAME, textMainUsername);
    }

    // text.main.profile=Your profile:

    public String getTextMainProfile() {
        return getString(Key.TEXT_MAIN_PROFILE, Default.TEXT_MAIN_PROFILE);
    }

    public void setTextMainProfile(String textMainProfile) {
        setString(Key.TEXT_MAIN_PROFILE, textMainProfile);
    }

    // text.main.broadcast=Broadcast:

    public String getTextMainBroadcast() {
        return getString(Key.TEXT_MAIN_BROADCAST, Default.TEXT_MAIN_BROADCAST);
    }

    public void setTextMainBroadcast(String textMainBroadcast) {
        setString(Key.TEXT_MAIN_BROADCAST, textMainBroadcast);
    }

    // text.main.getrooms=Get Rooms

    public String getTextMainGetrooms() {
        return getString(Key.TEXT_MAIN_GETROOMS, Default.TEXT_MAIN_GETROOMS);
    }

    public void setTextMainGetrooms(String textMainGetrooms) {
        setString(Key.TEXT_MAIN_GETROOMS, textMainGetrooms);
    }

    // text.main.enter=Enter Room

    public String getTextMainEnter() {
        return getString(Key.TEXT_MAIN_ENTER, Default.TEXT_MAIN_ENTER);
    }

    public void setTextMainEnter(String textMainEnter) {
        setString(Key.TEXT_MAIN_ENTER, textMainEnter);
    }

    // text.main.connect=Connect

    public String getTextMainConnect() {
        return getString(Key.TEXT_MAIN_CONNECT, Default.TEXT_MAIN_CONNECT);
    }

    public void setTextMainConnect(String textMainConnect) {
        setString(Key.TEXT_MAIN_CONNECT, textMainConnect);
    }

    // text.main.disconnect=Disconnect

    public String getTextMainDisconnect() {
        return getString(Key.TEXT_MAIN_DISCONNECT, Default.TEXT_MAIN_DISCONNECT);
    }

    public void setTextMainDisconnect(String textMainDisconnect) {
        setString(Key.TEXT_MAIN_DISCONNECT, textMainDisconnect);
    }

    // text.chat.status=Select a name for the profile. Double click a name for private chat.

    public String getTextChatStatus() {
        return getString(Key.TEXT_CHAT_STATUS, Default.TEXT_CHAT_STATUS);
    }

    public void setTextChatStatus(String textChatStatus) {
        setString(Key.TEXT_CHAT_STATUS, textChatStatus);
    }

    // text.chat.event.status=Type your question and press Enter to send it.

    public String getTextChatEventStatus() {
        return getString(Key.TEXT_CHAT_EVENT_STATUS, Default.TEXT_CHAT_EVENT_STATUS);
    }

    public void setTextChatEventStatus(String textChatEventStatus) {
        setString(Key.TEXT_CHAT_EVENT_STATUS, textChatEventStatus);
    }

    // text.chat.event.sent=Your question has been submitted to the moderator.

    public String getTextChatEventSent() {
        return getString(Key.TEXT_CHAT_EVENT_SENT, Default.TEXT_CHAT_EVENT_SENT);
    }

    public void setTextChatEventSent(String textChatEventSent) {
        setString(Key.TEXT_CHAT_EVENT_SENT, textChatEventSent);
    }

    // text.menu.places=Places

    public String getTextMenuPlaces() {
        return getString(Key.TEXT_MENU_PLACES, Default.TEXT_MENU_PLACES);
    }

    public void setTextMenuPlaces(String textMenuPlaces) {
        setString(Key.TEXT_MENU_PLACES, textMenuPlaces);
    }

    // text.menu.getrooms=Get Rooms

    public String getTextMenuGetrooms() {
        return getString(Key.TEXT_MENU_GETROOMS, Default.TEXT_MENU_GETROOMS);
    }

    public void setTextMenuGetrooms(String textMenuGetrooms) {
        setString(Key.TEXT_MENU_GETROOMS, textMenuGetrooms);
    }

    // text.menu.enter=Enter Room

    public String getTextMenuEnter() {
        return getString(Key.TEXT_MENU_ENTER, Default.TEXT_MENU_ENTER);
    }

    public void setTextMenuEnter(String textMenuEnter) {
        setString(Key.TEXT_MENU_ENTER, textMenuEnter);
    }

    // text.menu.exit=Exit

    public String getTextMenuExit() {
        return getString(Key.TEXT_MENU_EXIT, Default.TEXT_MENU_EXIT);
    }

    public void setTextMenuExit(String textMenuExit) {
        setString(Key.TEXT_MENU_EXIT, textMenuExit);
    }

    // text.menu.options=Options

    public String getTextMenuOptions() {
        return getString(Key.TEXT_MENU_OPTIONS, Default.TEXT_MENU_OPTIONS);
    }

    public void setTextMenuOptions(String textMenuOptions) {
        setString(Key.TEXT_MENU_OPTIONS, textMenuOptions);
    }

    // text.menu.font.name=Font Name

    public String getTextMenuFontName() {
        return getString(Key.TEXT_MENU_FONT_NAME, Default.TEXT_MENU_FONT_NAME);
    }

    public void setTextMenuFontName(String textMenuFontName) {
        setString(Key.TEXT_MENU_FONT_NAME, textMenuFontName);
    }

    // text.menu.font.style=Font Style

    public String getTextMenuFontStyle() {
        return getString(Key.TEXT_MENU_FONT_STYLE, Default.TEXT_MENU_FONT_STYLE);
    }

    public void setTextMenuFontStyle(String textMenuFontStyle) {
        setString(Key.TEXT_MENU_FONT_STYLE, textMenuFontStyle);
    }

    // text.menu.font.regular=Regular

    public String getTextMenuFontRegular() {
        return getString(Key.TEXT_MENU_FONT_REGULAR, Default.TEXT_MENU_FONT_REGULAR);
    }

    public void setTextMenuFontRegular(String textMenuFontRegular) {
        setString(Key.TEXT_MENU_FONT_REGULAR, textMenuFontRegular);
    }

    // text.menu.font.italic=Italic

    public String getTextMenuFontItalic() {
        return getString(Key.TEXT_MENU_FONT_ITALIC, Default.TEXT_MENU_FONT_ITALIC);
    }

    public void setTextMenuFontItalic(String textMenuFontItalic) {
        setString(Key.TEXT_MENU_FONT_ITALIC, textMenuFontItalic);
    }

    // text.menu.font.bold=Bold

    public String getTextMenuFontBold() {
        return getString(Key.TEXT_MENU_FONT_BOLD, Default.TEXT_MENU_FONT_BOLD);
    }

    public void setTextMenuFontBold(String textMenuFontBold) {
        setString(Key.TEXT_MENU_FONT_BOLD, textMenuFontBold);
    }

    // text.menu.font.bolditalic=Bold Italic

    public String getTextMenuFontBolditalic() {
        return getString(Key.TEXT_MENU_FONT_BOLDITALIC, Default.TEXT_MENU_FONT_BOLDITALIC);
    }

    public void setTextMenuFontBolditalic(String textMenuFontBolditalic) {
        setString(Key.TEXT_MENU_FONT_BOLDITALIC, textMenuFontBolditalic);
    }

    // text.menu.font.increase=Increase Font

    public String getTextMenuFontIncrease() {
        return getString(Key.TEXT_MENU_FONT_INCREASE, Default.TEXT_MENU_FONT_INCREASE);
    }

    public void setTextMenuFontIncrease(String textMenuFontIncrease) {
        setString(Key.TEXT_MENU_FONT_INCREASE, textMenuFontIncrease);
    }

    // text.menu.font.decrease=Decrease Font

    public String getTextMenuFontDecrease() {
        return getString(Key.TEXT_MENU_FONT_DECREASE, Default.TEXT_MENU_FONT_DECREASE);
    }

    public void setTextMenuFontDecrease(String textMenuFontDecrease) {
        setString(Key.TEXT_MENU_FONT_DECREASE, textMenuFontDecrease);
    }

    // text.menu.alert.entrance=Entrance Alerts

    public String getTextMenuAlertEntrance() {
        return getString(Key.TEXT_MENU_ALERT_ENTRANCE, Default.TEXT_MENU_ALERT_ENTRANCE);
    }

    public void setTextMenuAlertEntrance(String textMenuAlertEntrance) {
        setString(Key.TEXT_MENU_ALERT_ENTRANCE, textMenuAlertEntrance);
    }

    // text.menu.alert.audio=Audio Alerts

    public String getTextMenuAlertAudio() {
        return getString(Key.TEXT_MENU_ALERT_AUDIO, Default.TEXT_MENU_ALERT_AUDIO);
    }

    public void setTextMenuAlertAudio(String textMenuAlertAudio) {
        setString(Key.TEXT_MENU_ALERT_AUDIO, textMenuAlertAudio);
    }

    // text.menu.alert.count=Show Count Changes

    public String getTextMenuAlertCount() {
        return getString(Key.TEXT_MENU_ALERT_COUNT, Default.TEXT_MENU_ALERT_COUNT);
    }

    public void setTextMenuAlertCount(String textMenuAlertCount) {
        setString(Key.TEXT_MENU_ALERT_COUNT, textMenuAlertCount);
    }

    // text.menu.webtouring=Web Touring

    public String getTextMenuWebtouring() {
        return getString(Key.TEXT_MENU_WEBTOURING, Default.TEXT_MENU_WEBTOURING);
    }

    public void setTextMenuWebtouring(String textMenuWebtouring) {
        setString(Key.TEXT_MENU_WEBTOURING, textMenuWebtouring);
    }

    // text.menu.help=Help

    public String getTextMenuHelp() {
        return getString(Key.TEXT_MENU_HELP, Default.TEXT_MENU_HELP);
    }

    public void setTextMenuHelp(String textMenuHelp) {
        setString(Key.TEXT_MENU_HELP, textMenuHelp);
    }

    // text.menu.topics=Help Contents

    public String getTextMenuTopics() {
        return getString(Key.TEXT_MENU_TOPICS, Default.TEXT_MENU_TOPICS);
    }

    public void setTextMenuTopics(String textMenuTopics) {
        setString(Key.TEXT_MENU_TOPICS, textMenuTopics);
    }

    // text.menu.about=About VolanoChat

    public String getTextMenuAbout() {
        return getString(Key.TEXT_MENU_ABOUT, Default.TEXT_MENU_ABOUT);
    }

    public void setTextMenuAbout(String textMenuAbout) {
        setString(Key.TEXT_MENU_ABOUT, textMenuAbout);
    }

    // text.menu.room=Room

    public String getTextMenuRoom() {
        return getString(Key.TEXT_MENU_ROOM, Default.TEXT_MENU_ROOM);
    }

    public void setTextMenuRoom(String textMenuRoom) {
        setString(Key.TEXT_MENU_ROOM, textMenuRoom);
    }

    // text.menu.close=Close

    public String getTextMenuClose() {
        return getString(Key.TEXT_MENU_CLOSE, Default.TEXT_MENU_CLOSE);
    }

    public void setTextMenuClose(String textMenuClose) {
        setString(Key.TEXT_MENU_CLOSE, textMenuClose);
    }

    // text.menu.people=People

    public String getTextMenuPeople() {
        return getString(Key.TEXT_MENU_PEOPLE, Default.TEXT_MENU_PEOPLE);
    }

    public void setTextMenuPeople(String textMenuPeople) {
        setString(Key.TEXT_MENU_PEOPLE, textMenuPeople);
    }

    // text.menu.people.ring=Ring %0

    public String getTextMenuPeopleRing() {
        return getString(Key.TEXT_MENU_PEOPLE_RING, Default.TEXT_MENU_PEOPLE_RING);
    }

    public void setTextMenuPeopleRing(String textMenuPeopleRing) {
        setString(Key.TEXT_MENU_PEOPLE_RING, textMenuPeopleRing);
    }

    // text.menu.people.ignore=Ignore %0

    public String getTextMenuPeopleIgnore() {
        return getString(Key.TEXT_MENU_PEOPLE_IGNORE, Default.TEXT_MENU_PEOPLE_IGNORE);
    }

    public void setTextMenuPeopleIgnore(String textMenuPeopleIgnore) {
        setString(Key.TEXT_MENU_PEOPLE_IGNORE, textMenuPeopleIgnore);
    }

    // text.menu.people.unignore=Unignore %0

    public String getTextMenuPeopleUnignore() {
        return getString(Key.TEXT_MENU_PEOPLE_UNIGNORE, Default.TEXT_MENU_PEOPLE_UNIGNORE);
    }

    public void setTextMenuPeopleUnignore(String textMenuPeopleUnignore) {
        setString(Key.TEXT_MENU_PEOPLE_UNIGNORE, textMenuPeopleUnignore);
    }

    // text.menu.people.count=Count

    public String getTextMenuPeopleCount() {
        return getString(Key.TEXT_MENU_PEOPLE_COUNT, Default.TEXT_MENU_PEOPLE_COUNT);
    }

    public void setTextMenuPeopleCount(String textMenuPeopleCount) {
        setString(Key.TEXT_MENU_PEOPLE_COUNT, textMenuPeopleCount);
    }

    // text.menu.links.title=

    public String getTextMenuLinksTitle() {
        return getString(Key.TEXT_MENU_LINKS_TITLE, Default.TEXT_MENU_LINKS_TITLE);
    }

    public void setTextMenuLinksTitle(String textMenuLinksTitle) {
        setString(Key.TEXT_MENU_LINKS_TITLE, textMenuLinksTitle);
    }

    // text.menu.links.names=

    public String getTextMenuLinksNames() {
        return getString(Key.TEXT_MENU_LINKS_NAMES, Default.TEXT_MENU_LINKS_NAMES);
    }

    public void setTextMenuLinksNames(String textMenuLinksNames) {
        setString(Key.TEXT_MENU_LINKS_NAMES, textMenuLinksNames);
    }

    // text.menu.links.locations=

    public String getTextMenuLinksLocations() {
        return getString(Key.TEXT_MENU_LINKS_LOCATIONS, Default.TEXT_MENU_LINKS_LOCATIONS);
    }

    public void setTextMenuLinksLocations(String textMenuLinksLocations) {
        setString(Key.TEXT_MENU_LINKS_LOCATIONS, textMenuLinksLocations);
    }

    // text.status.focus.rooms=List of rooms.

    public String getTextStatusFocusRooms() {
        return getString(Key.TEXT_STATUS_FOCUS_ROOMS, Default.TEXT_STATUS_FOCUS_ROOMS);
    }

    public void setTextStatusFocusRooms(String textStatusFocusRooms) {
        setString(Key.TEXT_STATUS_FOCUS_ROOMS, textStatusFocusRooms);
    }

    // text.status.focus.users=List of people in room.

    public String getTextStatusFocusUsers() {
        return getString(Key.TEXT_STATUS_FOCUS_USERS, Default.TEXT_STATUS_FOCUS_USERS);
    }

    public void setTextStatusFocusUsers(String textStatusFocusUsers) {
        setString(Key.TEXT_STATUS_FOCUS_USERS, textStatusFocusUsers);
    }

    // text.status.focus.filter=Filter for list of room names.

    public String getTextStatusFocusFilter() {
        return getString(Key.TEXT_STATUS_FOCUS_FILTER, Default.TEXT_STATUS_FOCUS_FILTER);
    }

    public void setTextStatusFocusFilter(String textStatusFocusFilter) {
        setString(Key.TEXT_STATUS_FOCUS_FILTER, textStatusFocusFilter);
    }

    // text.status.focus.username=Your name or nickname.

    public String getTextStatusFocusUsername() {
        return getString(Key.TEXT_STATUS_FOCUS_USERNAME, Default.TEXT_STATUS_FOCUS_USERNAME);
    }

    public void setTextStatusFocusUsername(String textStatusFocusUsername) {
        setString(Key.TEXT_STATUS_FOCUS_USERNAME, textStatusFocusUsername);
    }

    // text.status.focus.profile=Optional personal information such as a Web or e-mail address.

    public String getTextStatusFocusProfile() {
        return getString(Key.TEXT_STATUS_FOCUS_PROFILE, Default.TEXT_STATUS_FOCUS_PROFILE);
    }

    public void setTextStatusFocusProfile(String textStatusFocusProfile) {
        setString(Key.TEXT_STATUS_FOCUS_PROFILE, textStatusFocusProfile);
    }

    // text.status.focus.getrooms=Get list of room names matching filter.

    public String getTextStatusFocusGetrooms() {
        return getString(Key.TEXT_STATUS_FOCUS_GETROOMS, Default.TEXT_STATUS_FOCUS_GETROOMS);
    }

    public void setTextStatusFocusGetrooms(String textStatusFocusGetrooms) {
        setString(Key.TEXT_STATUS_FOCUS_GETROOMS, textStatusFocusGetrooms);
    }

    // text.status.focus.enter=Enter a room.

    public String getTextStatusFocusEnter() {
        return getString(Key.TEXT_STATUS_FOCUS_ENTER, Default.TEXT_STATUS_FOCUS_ENTER);
    }

    public void setTextStatusFocusEnter(String textStatusFocusEnter) {
        setString(Key.TEXT_STATUS_FOCUS_ENTER, textStatusFocusEnter);
    }

    // text.status.focus.membername=Your member name.

    public String getTextStatusFocusMembername() {
        return getString(Key.TEXT_STATUS_FOCUS_MEMBERNAME, Default.TEXT_STATUS_FOCUS_MEMBERNAME);
    }

    public void setTextStatusFocusMembername(String textStatusFocusMembername) {
        setString(Key.TEXT_STATUS_FOCUS_MEMBERNAME, textStatusFocusMembername);
    }

    // text.status.focus.memberpassword=Your member password.

    public String getTextStatusFocusMemberpassword() {
        return getString(Key.TEXT_STATUS_FOCUS_MEMBERPASSWORD, Default.TEXT_STATUS_FOCUS_MEMBERPASSWORD);
    }

    public void setTextStatusFocusMemberpassword(String textStatusFocusMemberpassword) {
        setString(Key.TEXT_STATUS_FOCUS_MEMBERPASSWORD, textStatusFocusMemberpassword);
    }

    // text.status.selectroom=Select a room.

    public String getTextStatusSelectroom() {
        return getString(Key.TEXT_STATUS_SELECTROOM, Default.TEXT_STATUS_SELECTROOM);
    }

    public void setTextStatusSelectroom(String textStatusSelectroom) {
        setString(Key.TEXT_STATUS_SELECTROOM, textStatusSelectroom);
    }

    // text.status.entername=Enter your name or a nickname.

    public String getTextStatusEntername() {
        return getString(Key.TEXT_STATUS_ENTERNAME, Default.TEXT_STATUS_ENTERNAME);
    }

    public void setTextStatusEntername(String textStatusEntername) {
        setString(Key.TEXT_STATUS_ENTERNAME, textStatusEntername);
    }

    // text.status.enterpassword=Enter your password.

    public String getTextStatusEnterpassword() {
        return getString(Key.TEXT_STATUS_ENTERPASSWORD, Default.TEXT_STATUS_ENTERPASSWORD);
    }

    public void setTextStatusEnterpassword(String textStatusEnterpassword) {
        setString(Key.TEXT_STATUS_ENTERPASSWORD, textStatusEnterpassword);
    }

    // text.status.entermembername=Enter your member name.

    public String getTextStatusEntermembername() {
        return getString(Key.TEXT_STATUS_ENTERMEMBERNAME, Default.TEXT_STATUS_ENTERMEMBERNAME);
    }

    public void setTextStatusEntermembername(String textStatusEntermembername) {
        setString(Key.TEXT_STATUS_ENTERMEMBERNAME, textStatusEntermembername);
    }

    // text.status.entermemberpassword=Enter your member password.

    public String getTextStatusEntermemberpassword() {
        return getString(Key.TEXT_STATUS_ENTERMEMBERPASSWORD, Default.TEXT_STATUS_ENTERMEMBERPASSWORD);
    }

    public void setTextStatusEntermemberpassword(String textStatusEntermemberpassword) {
        setString(Key.TEXT_STATUS_ENTERMEMBERPASSWORD, textStatusEntermemberpassword);
    }

    // text.status.enterprofile=Enter an optional profile.

    public String getTextStatusEnterprofile() {
        return getString(Key.TEXT_STATUS_ENTERPROFILE, Default.TEXT_STATUS_ENTERPROFILE);
    }

    public void setTextStatusEnterprofile(String textStatusEnterprofile) {
        setString(Key.TEXT_STATUS_ENTERPROFILE, textStatusEnterprofile);
    }

    // text.status.enter=Press Enter Room or the Enter key to enter.

    public String getTextStatusEnter() {
        return getString(Key.TEXT_STATUS_ENTER, Default.TEXT_STATUS_ENTER);
    }

    public void setTextStatusEnter(String textStatusEnter) {
        setString(Key.TEXT_STATUS_ENTER, textStatusEnter);
    }

    // text.status.enteringroom=Entering %0...

    public String getTextStatusEnteringroom() {
        return getString(Key.TEXT_STATUS_ENTERINGROOM, Default.TEXT_STATUS_ENTERINGROOM);
    }

    public void setTextStatusEnteringroom(String textStatusEnteringroom) {
        setString(Key.TEXT_STATUS_ENTERINGROOM, textStatusEnteringroom);
    }

    // text.status.enteringprivate=Starting private chat with %0...

    public String getTextStatusEnteringprivate() {
        return getString(Key.TEXT_STATUS_ENTERINGPRIVATE, Default.TEXT_STATUS_ENTERINGPRIVATE);
    }

    public void setTextStatusEnteringprivate(String textStatusEnteringprivate) {
        setString(Key.TEXT_STATUS_ENTERINGPRIVATE, textStatusEnteringprivate);
    }

    // text.status.gettingrooms=Getting list of rooms...

    public String getTextStatusGettingrooms() {
        return getString(Key.TEXT_STATUS_GETTINGROOMS, Default.TEXT_STATUS_GETTINGROOMS);
    }

    public void setTextStatusGettingrooms(String textStatusGettingrooms) {
        setString(Key.TEXT_STATUS_GETTINGROOMS, textStatusGettingrooms);
    }

    // text.status.gettingusers=Getting list of people in room...

    public String getTextStatusGettingusers() {
        return getString(Key.TEXT_STATUS_GETTINGUSERS, Default.TEXT_STATUS_GETTINGUSERS);
    }

    public void setTextStatusGettingusers(String textStatusGettingusers) {
        setString(Key.TEXT_STATUS_GETTINGUSERS, textStatusGettingusers);
    }

    // text.status.gettingprofile=Getting %0's profile...

    public String getTextStatusGettingprofile() {
        return getString(Key.TEXT_STATUS_GETTINGPROFILE, Default.TEXT_STATUS_GETTINGPROFILE);
    }

    public void setTextStatusGettingprofile(String textStatusGettingprofile) {
        setString(Key.TEXT_STATUS_GETTINGPROFILE, textStatusGettingprofile);
    }

    // text.status.nosuchroom=Room no longer exists. Press Refresh Rooms.

    public String getTextStatusNosuchroom() {
        return getString(Key.TEXT_STATUS_NOSUCHROOM, Default.TEXT_STATUS_NOSUCHROOM);
    }

    public void setTextStatusNosuchroom(String textStatusNosuchroom) {
        setString(Key.TEXT_STATUS_NOSUCHROOM, textStatusNosuchroom);
    }

    // text.status.nosuchuser=User is no longer in room.

    public String getTextStatusNosuchuser() {
        return getString(Key.TEXT_STATUS_NOSUCHUSER, Default.TEXT_STATUS_NOSUCHUSER);
    }

    public void setTextStatusNosuchuser(String textStatusNosuchuser) {
        setString(Key.TEXT_STATUS_NOSUCHUSER, textStatusNosuchuser);
    }

    // text.status.nametaken=The name %0 is already taken in %1.

    public String getTextStatusNametaken() {
        return getString(Key.TEXT_STATUS_NAMETAKEN, Default.TEXT_STATUS_NAMETAKEN);
    }

    public void setTextStatusNametaken(String textStatusNametaken) {
        setString(Key.TEXT_STATUS_NAMETAKEN, textStatusNametaken);
    }

    // text.status.membertaken=The name %0 belongs to a member. Please choose another name.

    public String getTextStatusMembertaken() {
        return getString(Key.TEXT_STATUS_MEMBERTAKEN, Default.TEXT_STATUS_MEMBERTAKEN);
    }

    public void setTextStatusMembertaken(String textStatusMembertaken) {
        setString(Key.TEXT_STATUS_MEMBERTAKEN, textStatusMembertaken);
    }

    // text.status.alreadyinroom=Already in %0.

    public String getTextStatusAlreadyinroom() {
        return getString(Key.TEXT_STATUS_ALREADYINROOM, Default.TEXT_STATUS_ALREADYINROOM);
    }

    public void setTextStatusAlreadyinroom(String textStatusAlreadyinroom) {
        setString(Key.TEXT_STATUS_ALREADYINROOM, textStatusAlreadyinroom);
    }

    // text.status.roomfull=%0 is full. Select another room or try again later.

    public String getTextStatusRoomfull() {
        return getString(Key.TEXT_STATUS_ROOMFULL, Default.TEXT_STATUS_ROOMFULL);
    }

    public void setTextStatusRoomfull(String textStatusRoomfull) {
        setString(Key.TEXT_STATUS_ROOMFULL, textStatusRoomfull);
    }

    // text.status.roomcount=Room count = %0.

    public String getTextStatusRoomcount() {
        return getString(Key.TEXT_STATUS_ROOMCOUNT, Default.TEXT_STATUS_ROOMCOUNT);
    }

    public void setTextStatusRoomcount(String textStatusRoomcount) {
        setString(Key.TEXT_STATUS_ROOMCOUNT, textStatusRoomcount);
    }

    // text.status.publiclimit=You are limited to %0 concurrent chat rooms.

    public String getTextStatusPubliclimit() {
        return getString(Key.TEXT_STATUS_PUBLICLIMIT, Default.TEXT_STATUS_PUBLICLIMIT);
    }

    public void setTextStatusPubliclimit(String textStatusPubliclimit) {
        setString(Key.TEXT_STATUS_PUBLICLIMIT, textStatusPubliclimit);
    }

    // text.status.privatelimit=You are limited to %0 concurrent private chat sessions.

    public String getTextStatusPrivatelimit() {
        return getString(Key.TEXT_STATUS_PRIVATELIMIT, Default.TEXT_STATUS_PRIVATELIMIT);
    }

    public void setTextStatusPrivatelimit(String textStatusPrivatelimit) {
        setString(Key.TEXT_STATUS_PRIVATELIMIT, textStatusPrivatelimit);
    }

    // text.status.noprofile=%0 has no profile.

    public String getTextStatusNoprofile() {
        return getString(Key.TEXT_STATUS_NOPROFILE, Default.TEXT_STATUS_NOPROFILE);
    }

    public void setTextStatusNoprofile(String textStatusNoprofile) {
        setString(Key.TEXT_STATUS_NOPROFILE, textStatusNoprofile);
    }

    // text.status.profile=%0: %2

    public String getTextStatusProfile() {
        return getString(Key.TEXT_STATUS_PROFILE, Default.TEXT_STATUS_PROFILE);
    }

    public void setTextStatusProfile(String textStatusProfile) {
        setString(Key.TEXT_STATUS_PROFILE, textStatusProfile);
    }

    // text.status.closing=Closing VolanoChat...

    public String getTextStatusClosing() {
        return getString(Key.TEXT_STATUS_CLOSING, Default.TEXT_STATUS_CLOSING);
    }

    public void setTextStatusClosing(String textStatusClosing) {
        setString(Key.TEXT_STATUS_CLOSING, textStatusClosing);
    }

    // text.system.entrance=[%0] %1

    public String getTextSystemEntrance() {
        return getString(Key.TEXT_SYSTEM_ENTRANCE, Default.TEXT_SYSTEM_ENTRANCE);
    }

    public void setTextSystemEntrance(String textSystemEntrance) {
        setString(Key.TEXT_SYSTEM_ENTRANCE, textSystemEntrance);
    }

    // text.system.audio=[%0] Audio alert from %1.

    public String getTextSystemAudio() {
        return getString(Key.TEXT_SYSTEM_AUDIO, Default.TEXT_SYSTEM_AUDIO);
    }

    public void setTextSystemAudio(String textSystemAudio) {
        setString(Key.TEXT_SYSTEM_AUDIO, textSystemAudio);
    }

    // text.system.broadcast=[%0] %1

    public String getTextSystemBroadcast() {
        return getString(Key.TEXT_SYSTEM_BROADCAST, Default.TEXT_SYSTEM_BROADCAST);
    }

    public void setTextSystemBroadcast(String textSystemBroadcast) {
        setString(Key.TEXT_SYSTEM_BROADCAST, textSystemBroadcast);
    }

    // text.system.partnerleft=[%0] %1 left private chat.

    public String getTextSystemPartnerleft() {
        return getString(Key.TEXT_SYSTEM_PARTNERLEFT, Default.TEXT_SYSTEM_PARTNERLEFT);
    }

    public void setTextSystemPartnerleft(String textSystemPartnerleft) {
        setString(Key.TEXT_SYSTEM_PARTNERLEFT, textSystemPartnerleft);
    }

    // text.system.disconnected=[%0] Disconnected. Close VolanoChat and restart.

    public String getTextSystemDisconnected() {
        return getString(Key.TEXT_SYSTEM_DISCONNECTED, Default.TEXT_SYSTEM_DISCONNECTED);
    }

    public void setTextSystemDisconnected(String textSystemDisconnected) {
        setString(Key.TEXT_SYSTEM_DISCONNECTED, textSystemDisconnected);
    }
}
