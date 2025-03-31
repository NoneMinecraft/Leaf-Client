package net.nonemc.leaf.launch.data.modernui.mainmenu.config.configs;
/*
[{"text":"Leaf Client",
  "x":46.0,
  "y":2.0,
  "red":100,
  "green":200,
  "blue":255,
  "alpha":255,
  "scale":2.0,
  "centered":true,
  "fontPath":"leaf/font/font.ttf",
  "fontSize": 24}]
 */
public class TextConfig {
    public String text = "";
    public float x = 0;
    public float y = 0;
    public int red = 255;
    public int green = 255;
    public int blue = 255;
    public int alpha = 255;
    public float scale = 1.0f;
    public boolean centered = false;
    public String fontPath = "";
    public int fontSize = 18;

    public int getColorARGB() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
