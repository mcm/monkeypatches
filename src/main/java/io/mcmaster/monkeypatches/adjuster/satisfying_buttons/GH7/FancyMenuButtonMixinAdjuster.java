package io.mcmaster.monkeypatches.adjuster.satisfying_buttons.GH7;

import java.util.List;

import org.objectweb.asm.tree.MethodNode;

import com.bawnorton.mixinsquared.adjuster.tools.AdjustableAnnotationNode;
import com.bawnorton.mixinsquared.adjuster.tools.AdjustableWrapOperationNode;
import com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

/**
 * MixinSquared annotation adjuster for Satisfying Buttons' FancyMenuButtonMixin
 * to fix a startup crash with newer FancyMenu versions.
 *
 * Fixes: https://github.com/txnimc/SatisfyingButtons/issues/7
 *
 * Problem: Satisfying Buttons wraps the {@code WidgetSprites.get(ZZ)} call in
 * FancyMenu's {@code ExtendedButton.renderBackground} so the vanilla button
 * texture doesn't switch to the hovered sprite instantly. The released jar
 * targets {@code renderBackground(Lnet/minecraft/client/gui/GuiGraphics;)V},
 * but FancyMenu 3.9.x changed the signature to
 * {@code renderBackground(GuiGraphics, float)}. With
 * {@code defaultRequire = 1}, the injector fails validation and crashes the
 * game during FancyMenu initialization.
 *
 * Solution: Rewrite the {@code @WrapOperation} method selector to the bare
 * method name {@code renderBackground}. ExtendedButton declares exactly one
 * method with that name in both old and new FancyMenu versions, and the
 * wrapped {@code WidgetSprites.get(ZZ)} call is unchanged, so the handler
 * applies as originally intended and the fade-in feature keeps working.
 *
 * This is a MixinSquared adjuster rather than a mixin because the broken
 * selector lives in another mod's mixin annotation, which can't be targeted by
 * a regular mixin. It is registered via
 * {@code META-INF/services/com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster}.
 *
 * The adjustment only happens when the selector still has the exact broken
 * value, so it becomes a no-op if Satisfying Buttons fixes this upstream. No
 * configuration option is available because adjusters run while mixin configs
 * are loaded, before the config system initializes.
 */
public class FancyMenuButtonMixinAdjuster implements MixinAnnotationAdjuster {
    private static final String MIXIN_CLASS = "toni.satisfyingbuttons.mixin.FancyMenuButtonMixin";
    private static final String BROKEN_SELECTOR = "renderBackground(Lnet/minecraft/client/gui/GuiGraphics;)V";
    private static final String FIXED_SELECTOR = "renderBackground";

    @Override
    public AdjustableAnnotationNode adjust(List<String> targetClassNames, String mixinClassName, MethodNode handlerNode,
            AdjustableAnnotationNode annotationNode) {
        if (!MIXIN_CLASS.equals(mixinClassName) || !annotationNode.is(WrapOperation.class))
            return annotationNode;

        return annotationNode.as(AdjustableWrapOperationNode.class)
                .withMethod(methods -> methods.stream()
                        .map(method -> BROKEN_SELECTOR.equals(method) ? FIXED_SELECTOR : method)
                        .toList());
    }
}
