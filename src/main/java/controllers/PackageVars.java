package controllers;

import blackboardbot.BlackBoardBot;
import blackboardbot.Constraints;

enum Action {REVSTAT, TITLE_COLOR, REMOVE_ICONS, TOGGLE_AVAILABILITY, SET_LANDING, CHECK_DATES}

enum TargetType {SINGLE, CONSTRAINT}

class PackageVars {
    static Action action;
    static TargetType targetType;
    static String courseUrl;
    static String calendarUrl;
    static String year;
    static String session;
    static Constraints constraints;
    static BlackBoardBot bot;
}
