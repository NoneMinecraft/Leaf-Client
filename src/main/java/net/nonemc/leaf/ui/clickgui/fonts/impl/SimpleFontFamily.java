package net.nonemc.leaf.ui.clickgui.fonts.impl;

import net.nonemc.leaf.ui.clickgui.fonts.api.FontFamily;
import net.nonemc.leaf.ui.clickgui.fonts.api.FontRenderer;
import net.nonemc.leaf.ui.clickgui.fonts.api.FontType;
final class SimpleFontFamily implements FontFamily {

	private final FontType fontType;
	private final java.awt.Font awtFont;

	private SimpleFontFamily(FontType fontType, java.awt.Font awtFont) {
		this.fontType = fontType;
		this.awtFont = awtFont;
	}

	static FontFamily create(FontType fontType, java.awt.Font awtFont) {
		return new SimpleFontFamily(fontType, awtFont);
	}

	@Override
	public FontRenderer ofSize(int size) {
			return SimpleFontRenderer.create(awtFont.deriveFont(java.awt.Font.PLAIN, size));
	}

	@Override
	public FontType font() { return fontType; }
}