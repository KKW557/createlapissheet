package icu.suc.createlapissheet.client.compat.jei.category

import com.zurrtum.create.AllItems
import com.zurrtum.create.client.compat.jei.CreateCategory
import com.zurrtum.create.client.compat.jei.renderer.TwoIconRenderer
import com.zurrtum.create.client.foundation.gui.AllGuiTextures
import com.zurrtum.create.client.foundation.gui.render.FanRenderState
import icu.suc.createlapissheet.Blocks
import icu.suc.createlapissheet.Items
import icu.suc.createlapissheet.MOD_ID
import icu.suc.createlapissheet.RecipeTypes
import icu.suc.createlapissheet.client.compat.JeiModPlugin
import icu.suc.createlapissheet.content.processing.mansion.EvokerMansionBlock
import icu.suc.createlapissheet.item.crafting.WololoRecipe
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.recipe.IFocusGroup
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeMap
import net.minecraft.world.item.crafting.SingleRecipeInput
import org.joml.Matrix3x2f
import kotlin.math.min

class FanWololoCategory : CreateCategory<RecipeHolder<WololoRecipe>>() {
    override fun getRecipeType() = JeiModPlugin.FAN_WOLOLO

    override fun getTitle() = Component.translatable("${MOD_ID}.recipe.${JeiModPlugin.FAN_WOLOLO.uid.path}")

    override fun getHeight() = 72

    override fun getIcon() = TwoIconRenderer(AllItems.PROPELLER, Items.SAFE_EVOKER_MANSION)

    override fun setRecipe(
        builder: IRecipeLayoutBuilder,
        entry: RecipeHolder<WololoRecipe>,
        focuses: IFocusGroup
    ) {
        val recipe = entry.value()
        val results = recipe.results()
        val outputSize = results.size

        if (outputSize == 1) {
            builder.addInputSlot(21, 48)
                .setBackground(SLOT, -1, -1)
                .add(recipe.ingredient())

            addChanceSlot(builder, 141, 48, results.first())
        } else {
            val xOffsetAmount = 1 - min(3, outputSize)

            builder.addInputSlot(21 + xOffsetAmount * 5, 48)
                .setBackground(SLOT, -1, -1)
                .add(recipe.ingredient())

            val left = 141 + xOffsetAmount * 9
            val top = if (outputSize <= 9) 48 else 57

            results.forEachIndexed { i, result ->
                addChanceSlot(
                    builder,
                    left + (i % 3) * 19,
                    top + (i / 3) * -19,
                    result
                )
            }
        }
    }

    override fun draw(
        entry: RecipeHolder<WololoRecipe>,
        recipeSlotsView: IRecipeSlotsView,
        graphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val recipe = entry.value()
        val xOffsetAmount = 1 - min(3, recipe.results().size)

        AllGuiTextures.JEI_SHADOW.render(graphics, 46, 27)
        AllGuiTextures.JEI_LIGHT.render(graphics, 65, 39)
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 54 + 7 * xOffsetAmount, 51)

        graphics.guiRenderState.submitPicturesInPictureState(
            FanRenderState(
                Matrix3x2f(graphics.pose()),
                56,
                4,
                Blocks.EVOKER_MANSION.defaultBlockState()
                    .setValue(EvokerMansionBlock.EVOKER, EvokerMansionBlock.Evoker.CASTING)
                    .setValue(EvokerMansionBlock.NAUSEA, recipe.nausea)
            )
        )
    }

    companion object {
        @JvmStatic
        fun getRecipes(recipes: RecipeMap) = recipes.byType<SingleRecipeInput, WololoRecipe>(RecipeTypes.WOLOLO).toList()
    }
}