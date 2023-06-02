package com.mirea.kt.android2023.mycontactsapp.utils;

public class Validator {

    public static String validateNumber(String number) {
        String[] badChar = {" ", "-", ",", ".", "#", "N", "(", ")", "/", "*", ";", "+"};
        String[] num = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        boolean hasPlus = number.startsWith("+");

        for (String ch : badChar) {
            number = number.replace(ch, "");
        }

        if (number.isEmpty()) {
            return null;
        }

        for (String str : num) {
            if (number.contains(str)) {
                return (hasPlus) ? ("+" + number) : number;
            }
        }
        return null;
    }
}
