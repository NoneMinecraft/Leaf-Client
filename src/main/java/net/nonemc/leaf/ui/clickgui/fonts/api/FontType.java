package net.nonemc.leaf.ui.clickgui.fonts.api;
public enum FontType {
	DM("diramight.ttf"),
	FIXEDSYS("tahoma.ttf"),
	ICONFONT("stylesicons.ttf"),
	FluxICONFONT("flux.ttf"),
	Check("check.ttf"),
	TenacityBold("Tenacity.ttf"),
	SF("SF.ttf"),
	SFBOLD("SFBOLD.ttf"),
	CHINESE("black.ttf"),
	Tahoma("Tahoma.ttf"),
	TahomaBold("Tahoma-Bold.ttf"),
	SFTHIN("SFREGULAR.ttf"),
	MAINMENU("mainmenu.ttf"),
	OXIDE("oxide.ttf");


	private final String fileName;

	FontType(String fileName) {
		this.fileName = fileName;
	}

	public String fileName() { return fileName; }
}
