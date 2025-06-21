package com.github.no_name_provided.potion_fruit.common.annotations;

import net.neoforged.api.distmarker.Dist;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlyIn {
    @SuppressWarnings("unused")
    net.neoforged.api.distmarker.Dist value() default Dist.CLIENT;
}
