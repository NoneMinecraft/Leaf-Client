package net.nonemc.leaf.font;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FontDetails {

    String fontName();

    String fileName() default "";

    int fontSize() default -1;
}