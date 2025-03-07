package net.nonemc.leaf.launch.data.modernui.clickgui.fonts.api;
@FunctionalInterface
public interface FontManager {
	FontFamily fontFamily(FontType fontType);
	default FontRenderer font(FontType fontType, int size) {
		return fontFamily(fontType).ofSize(size);
	}
}
