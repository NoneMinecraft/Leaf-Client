package net.nonemc.leaf.ui.clickgui.fonts.api;

public interface FontRenderer {
	float drawString(CharSequence text, float x, float y, int color, boolean dropShadow);
	float drawString(CharSequence text, double x, double y, int color, boolean dropShadow);
	String trimStringToWidth(CharSequence text, int width, boolean reverse);
	int stringWidth(CharSequence text);
	String getName();
	int getHeight();

	default float drawString(CharSequence text, float x, float y, int color) {
		return drawString(text, x, y, color, false);
	}
	default float drawString(CharSequence text, int x, int y, int color) {
		return drawString(text, x, y, color, false);
	}
	default String trimStringToWidth(CharSequence text, int width) {
		return trimStringToWidth(text, width, false);
	}

	default float drawCenteredString(CharSequence text, float x, float y, int color, boolean dropShadow) {
		return drawString(text, x - stringWidth(text) / 2.0F, y, color, dropShadow);
	}

	default float drawCenteredString(CharSequence text, float x, float y, int color) {
		return drawCenteredString(text, x, y, color, false);
	}
}
