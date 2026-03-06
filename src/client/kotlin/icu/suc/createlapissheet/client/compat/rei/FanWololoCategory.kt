package icu.suc.createlapissheet.client.compat.rei

import com.zurrtum.create.AllItems
import com.zurrtum.create.client.compat.rei.CreateCategory
import com.zurrtum.create.client.compat.rei.renderer.TwoIconRenderer
import com.zurrtum.create.client.foundation.gui.AllGuiTextures
import com.zurrtum.create.client.foundation.gui.render.FanRenderState
import icu.suc.createlapissheet.Blocks
import icu.suc.createlapissheet.Items
import icu.suc.createlapissheet.MOD_ID
import icu.suc.createlapissheet.block.EvokerMansionBlock
import icu.suc.createlapissheet.compat.ReiCommon
import icu.suc.createlapissheet.compat.rei.FanWololoDisplay
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.common.entry.EntryIngredient
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.joml.Matrix3x2f
import kotlin.math.min

class FanWololoCategory : CreateCategory<FanWololoDisplay>() {
    override fun addWidgets(
        widgets: MutableList<Widget>,
        display: FanWololoDisplay,
        bounds: Rectangle
    ) {
        val outputs = mutableListOf<Point>()
        val outputIngredients = mutableListOf<EntryIngredient>()
        val chances = mutableListOf<Point>()
        val chanceIngredients = mutableListOf<EntryIngredient>()

        val results = display.outputs
        val outputSize = results.size
        val xOffsetAmount = 1 - min(3, outputSize)

        val input: Point

        if (outputSize == 1) {
            input = Point(bounds.x + 26, bounds.y + 53)

            addOutputData(
                results.first(),
                bounds.x + 146,
                bounds.y + 53,
                outputs,
                outputIngredients,
                chances,
                chanceIngredients
            )
        } else {
            input = Point(bounds.x + 26 + xOffsetAmount * 5, bounds.y + 53)

            val left = bounds.x + 146 + xOffsetAmount * 9
            val top = bounds.y + if (outputSize <= 9) 53 else 62

            results.forEachIndexed { i, result ->
                val xOffset = (i % 3) * 19
                val yOffset = (i / 3) * -19

                addOutputData(
                    result,
                    left + xOffset,
                    top + yOffset,
                    outputs,
                    outputIngredients,
                    chances,
                    chanceIngredients
                )
            }
        }

        widgets += Widgets.createDrawableWidget { graphics: GuiGraphics, _, _, _ ->
            drawSlotBackground(graphics, outputs, input)
            drawChanceSlotBackground(graphics, chances)

            AllGuiTextures.JEI_SHADOW.render(graphics, bounds.x + 51, bounds.y + 32)
            AllGuiTextures.JEI_LIGHT.render(graphics, bounds.x + 70, bounds.y + 44)
            AllGuiTextures.JEI_LONG_ARROW.render(graphics, bounds.x + 59 + 7 * xOffsetAmount, bounds.y + 56)

            graphics.guiRenderState.submitPicturesInPictureState(
                FanRenderState(
                    Matrix3x2f(graphics.pose()),
                    bounds.x + 61,
                    bounds.y + 9,
                    Blocks.EVOKER_MANSION.defaultBlockState()
                        .setValue(EvokerMansionBlock.EVOKER, EvokerMansionBlock.Evoker.CASTING)
                        .setValue(EvokerMansionBlock.NAUSEA, display.nausea)
                )
            )
        }

        widgets += createInputSlot(input).entries(display.input)

        outputs.zip(outputIngredients).forEach { (point, ingredient) ->
            widgets += createOutputSlot(point).entries(ingredient)
        }

        chances.zip(chanceIngredients).forEach { (point, ingredient) ->
            widgets += createOutputSlot(point).entries(ingredient)
        }
    }

    override fun getCategoryIdentifier() = ReiCommon.FAN_WOLOLO

    override fun getTitle() = Component.translatable("$MOD_ID.recipe.${ReiCommon.FAN_WOLOLO.path}")

    override fun getIcon(): Renderer = TwoIconRenderer(AllItems.PROPELLER, Items.SAFE_EVOKER_MANSION)

    override fun getDisplayHeight() = 82
}