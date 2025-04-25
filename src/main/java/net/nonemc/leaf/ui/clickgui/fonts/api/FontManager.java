package net.nonemc.leaf.ui.clickgui.fonts.api;
@FunctionalInterface
public interface FontManager {
	FontFamily fontFamily(FontType fontType);
	default FontRenderer font(FontType fontType, int size) {
		return fontFamily(fontType).ofSize(size);
	}
}
