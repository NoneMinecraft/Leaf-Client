package net.nonemc.leaf.launch.data.modernui.mainmenu.config.configs;
/*
{"Language":{"x":52.0,"y":218.5,"xScale":1.0,"yScale":1.0,"textScale":1.0,"color":-932944641,"cornerRadius":5},
"Single":{"x":55.0,"y":67.5,"xScale":1.0,"yScale":1.0,"textScale":1.0,"color":-932944641,"cornerRadius":5},
"Alt":{"x":54.0,"y":141.5,"xScale":1.0,"yScale":1.0,"textScale":1.0,"color":-932944641,"cornerRadius":5},
"Multi":{"x":55.0,"y":102.5,"xScale":1.0,"yScale":1.0,"textScale":1.0,"color":-932944641,"cornerRadius":5},
"Option":{"x":53.0,"y":177.5,"xScale":1.0,"yScale":1.0,"textScale":1.0,"color":-932944641,"cornerRadius":5}}
 */
public class ButtonConfig {
    public float x;
    public float y;
    public float xScale;
    public float yScale;
    public float textScale;
    public int color;
    public int cornerRadius;

    public ButtonConfig(float x, float y,
                        float xScale, float yScale, float textScale,
                        int color, int cornerRadius) {
        this.x = x;
        this.y = y;
        this.xScale = xScale;
        this.yScale = yScale;
        this.textScale = textScale;
        this.color = color;
        this.cornerRadius = cornerRadius;
    }

}