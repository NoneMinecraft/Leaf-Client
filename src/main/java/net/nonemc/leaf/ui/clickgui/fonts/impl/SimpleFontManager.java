package net.nonemc.leaf.ui.clickgui.fonts.impl;

import net.nonemc.leaf.ui.clickgui.fonts.api.FontFamily;
import net.nonemc.leaf.ui.clickgui.fonts.api.FontManager;
import net.nonemc.leaf.ui.clickgui.fonts.api.FontType;
import net.nonemc.leaf.ui.clickgui.fonts.util.SneakyThrowing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;

public final class SimpleFontManager implements FontManager {

	private SimpleFontManager() {}

	public static FontManager create() {
		return new SimpleFontManager();
	}

	private static final String FONT_DIRECTORY = "leaf/font/";
	private final FontRegistry fonts = new FontRegistry();

	@Override
	public FontFamily fontFamily(FontType fontType) {
		return fonts.fontFamily(fontType);
	}

	private static final class FontRegistry extends EnumMap<FontType, FontFamily> {

		private FontRegistry() { super(FontType.class); }

		private FontFamily fontFamily(FontType fontType) {
			return computeIfAbsent(fontType, ignored -> {
				try {
					return SimpleFontFamily.create(fontType, readFontFromResources(fontType));
				} catch(IOException e) {
					throw SneakyThrowing.sneakyThrow(e);
				}
			});
		}

		private static Font readFontFromResources(FontType fontType) throws IOException {
			IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
			ResourceLocation location = new ResourceLocation(FONT_DIRECTORY + fontType.fileName());
			IResource resource;

			try {
				resource = resourceManager.getResource(location);
			} catch(IOException e) {
				throw new IOException("Couldn't find resource: " + location, e);
			}

			try(InputStream resourceInputStream = resource.getInputStream()) {
				return readFont(resourceInputStream);
			}
		}

		private static Font readFont(InputStream resource) {
			Font font;

			try {
				font = Font.createFont(Font.TRUETYPE_FONT, resource);
			} catch(FontFormatException e) {
				throw new RuntimeException("Resource does not contain the required font tables for the specified format", e);
			} catch(IOException e) {
				throw new RuntimeException("Couldn't completely read font resource", e);
			}

			return font;
		}
	}
}
