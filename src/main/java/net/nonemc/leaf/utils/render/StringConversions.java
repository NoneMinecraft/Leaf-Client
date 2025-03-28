
package net.nonemc.leaf.utils.render;

public class StringConversions {
    public static Object castNumber(String newValueText, Object currentValue) {
        if (newValueText.contains((CharSequence)".")) {
            if (newValueText.toLowerCase().contains((CharSequence)"f")) {
                return Float.valueOf((float)Float.parseFloat((String)newValueText));
            }
            return Double.parseDouble((String)newValueText);
        }
        if (StringConversions.isNumeric(newValueText)) {
            return Integer.parseInt((String)newValueText);
        }
        return newValueText;
    }

    public static boolean isNumeric(String text) {
        try {
            Integer.parseInt((String)text);
            return true;
        }
        catch (NumberFormatException numberFormatException) {
            return false;
        }
    }
}
